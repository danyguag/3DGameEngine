/* Copyright (c) 2008, Nathan Sweet
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * - Neither the name of Esoteric Software nor the names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.studios.base.engine.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.esotericsoftware.kryo.Kryo;
import com.studios.base.engine.framework.debugging.DebugManager;
import com.studios.base.engine.net.FrameworkMessage.DiscoverHost;
import com.studios.base.engine.net.FrameworkMessage.RegisterUDP;

/** Represents a UDP connection to a {@link Server}.
 * @author Nathan Sweet <misc@n4te.com> */
public class Client extends Connection implements EndPoint 
{
	private final String CLASS_NAME = getClass().getSimpleName();
	
	static 
	{
		try 
		{
			// Needed for NIO selectors on Android 2.2.
			System.setProperty("java.net.preferIPv6Addresses", "false");
		}
		catch (AccessControlException Ignored) 
		{
		}
	}

	private int m_emptySelects;
	private int m_connectTimeout;
	private int m_connectUdpPort;
	private boolean m_isClosed;
	
	private volatile boolean m_isUdpRegistered;
	private volatile boolean m_isShutdown;

	private Object m_udpRegistrationLock = new Object();
	private final Object m_updateLock = new Object();
	private final Serialization m_serialization;
	private Selector m_selector;
	private Thread m_updateThread;
	private InetAddress m_connectHost;
	private ClientDiscoveryHandler m_discoveryHandler;

	/** Creates a Client with a write buffer size of 8192 and an object buffer size of 2048. */
	public Client() 
	{
		this(8192, 2048);
	}

	public Client(int WriteBufferSize, int ObjectBufferSize) 
	{
		this(WriteBufferSize, ObjectBufferSize, new KryoSerialization());
	}

	public Client(int WriteBufferSize, int ObjectBufferSize, Serialization Serialization) 
	{
		super();
		endPoint = this;

		m_serialization = Serialization;

		m_discoveryHandler = ClientDiscoveryHandler.DEFAULT;

		try 
		{
			m_selector = Selector.open();
		}
		catch (IOException e)
		{
			throw new RuntimeException("Error opening selector.", e);
		}
	}

	public void SetDiscoveryHandler(ClientDiscoveryHandler nDiscoveryHandler) 
	{
		m_discoveryHandler = nDiscoveryHandler;
	}

	public Serialization GetSerialization() 
	{
		return m_serialization;
	}

	public Kryo GetKryo() 
	{
		return ((KryoSerialization)m_serialization).getKryo();
	}

	/** Opens a UDP client.
	 * @see #connect(int, InetAddress, int, int) */
	public void Connect(int Timeout, String Host, int UdpPort) throws IOException 
	{
		Connect(Timeout, InetAddress.getByName(Host), UdpPort);
	}

	/** Opens a UDP client. Blocks until the connection is complete or the timeout is reached.
	 * <p>
	 * Because the framework must perform some minimal communication before the connection is considered successful,
	 * {@link #Update(int)} must be called on a separate thread during the connection process.
	 * @throws IllegalStateException if called from the connection's update thread.
	 * @throws IOException if the client could not be opened or connecting times out. */
	public void Connect(int Timeout, InetAddress Host, int UdpPort) throws IOException 
	{
		if (Host == null) 
			throw new IllegalArgumentException("host cannot be null.");
		if (Thread.currentThread() == GetUpdateThread())
			throw new IllegalStateException("Cannot connect on the connection's update thread.");
		m_connectTimeout = Timeout;
		m_connectHost = Host;
		m_connectUdpPort = UdpPort;
		DebugManager.Log(CLASS_NAME, "Connecting: " + Host + ":" + UdpPort);

		id = -1;
		try 
		{
			udp = new UdpConnection(m_serialization, 8192);
			InetSocketAddress udpAddress = new InetSocketAddress(Host, UdpPort);
			long endTime;
			synchronized (m_updateLock) 
			{
				m_isUdpRegistered = false;
				m_selector.wakeup();
				endTime = System.currentTimeMillis() + Timeout;
				udp.connect(m_selector, udpAddress);
				
			}

			// Wait for RegisterUDP reply.
			synchronized (m_udpRegistrationLock) 
			{
				long KeepUpTime;
				while (!m_isUdpRegistered && (KeepUpTime = System.currentTimeMillis()) < endTime) 
				{
					RegisterUDP registerUDP = new RegisterUDP();
					registerUDP.connectionID = id;
					udp.keepAliveMillis = (int) KeepUpTime;
					DebugManager.Log(CLASS_NAME, KeepUpTime);
					udp.send(this, registerUDP, udpAddress);
					try 
					{
						m_udpRegistrationLock.wait(100);
					}
					catch (InterruptedException Ignored) 
					{
					}
				}
				if (!m_isUdpRegistered)
					throw new SocketTimeoutException("Connected, but timed out during UDP registration: " + Host + ":" + UdpPort);
			}
		}
		catch (IOException e) 
		{
			Close();
			throw e;
		}
	}

	/** Calls {@link #Connect(int, InetAddress, int) connect} with the values last passed to connect.
	 * @throws IllegalStateException if connect has never been called. */
	public void Reconnect() throws IOException 
	{
		Reconnect(m_connectTimeout);
	}

	/** Calls {@link #Connect(int, InetAddress, int) connect} with the specified timeout and the other values last passed to
	 * connect.
	 * @throws IllegalStateException if connect has never been called. */
	public void Reconnect(int Timeout) throws IOException 
	{
		if (m_connectHost == null) 
			throw new IllegalStateException("This client has never been connected.");
		Connect(Timeout, m_connectHost, m_connectUdpPort);
	}

	/** Reads or writes any pending data for this client. Multiple threads should not call this method at the same time.
	 * @param Timeout Wait for up to the specified milliseconds for data to be ready to process. May be zero to return immediately
	 *           if there is no data to process. */
	public void Update(int Timeout) throws IOException 
	{
		m_updateThread = Thread.currentThread();
		synchronized (m_updateLock) 
		{ // Blocks to avoid a select while the selector is used to bind the server connection.
		}
		long startTime = System.currentTimeMillis();
		int select = 0;
		if (Timeout > 0) 
			select = m_selector.select(Timeout);
		else 
			select = m_selector.selectNow();

		if (select == 0) 
		{
			m_emptySelects++;
			if (m_emptySelects == 100) 
			{
				m_emptySelects = 0;
				// NIO freaks and returns immediately with 0 sometimes, so try to keep from hogging the CPU.
				long elapsedTime = System.currentTimeMillis() - startTime;
				try 
				{
					if (elapsedTime < 25) Thread.sleep(25 - elapsedTime);
				}
				catch (InterruptedException e) 
				{
				}
			}
		} 
		else 
		{
			m_emptySelects = 0;
			m_isClosed = false;
			Set<SelectionKey> keys = m_selector.selectedKeys();
			synchronized (keys) 
			{
				for (Iterator<SelectionKey> iter = keys.iterator(); iter.hasNext();) 
				{
					KeepAlive(System.currentTimeMillis());
					SelectionKey selectionKey = iter.next();
					iter.remove();
					try 
					{
						int ops = selectionKey.readyOps();
						if ((ops & SelectionKey.OP_READ) == SelectionKey.OP_READ) 
						{
							inner : while (true)
							{
								if (udp.readFromAddress() == null)
									break inner;
								Object object = udp.readObject(this);
								if (udp != null && !m_isUdpRegistered) 
								{
									if (object instanceof RegisterUDP) 
									{
										synchronized (m_udpRegistrationLock) 
										{
											m_isUdpRegistered = true;
											m_udpRegistrationLock.notifyAll();
											setConnected(true, ((RegisterUDP) object).connectionID);
											DebugManager.Log(CLASS_NAME + "dsfg", this + " received UDP: RegisterUDP, is this it?");
											DebugManager.Log(CLASS_NAME, "Port " + udp.datagramChannel.socket().getLocalPort()
												+ "/UDP connected to: " + udp.connectedAddress);
										}
										notifyConnected();
										break;
									}
									else if (object instanceof FrameworkMessage.KeepAlive)
									{
										KeepAlive(System.currentTimeMillis());
									}
									continue;
								}
								
								if (object != null)
									notifyReceived(object);
								
								if (!isConnected) continue;
								
								
							}
							if (udp.readFromAddress() == null) 
								continue;
							Object object = udp.readObject(this);
							if (object == null) 
								continue;
							String objectString = object == null ? "null" : object.getClass().getSimpleName();
							DebugManager.Log(CLASS_NAME, this + "asdf" + " received UDP: " + objectString);
							notifyReceived(object);
						}
					} 
					catch (CancelledKeyException Ignored) 
					{
						// Connection is closed.
					}
				}
			}
		}
		
		if (isConnected) 
		{
			long time = System.currentTimeMillis();
			if (udp.needsKeepAlive(time)) 
			{
				DebugManager.Log(CLASS_NAME, this + " Keep Alive");
				KeepAlive(time);
			} 
				
			if (isIdle()) 
				notifyIdle();
		}
	}

	public void KeepAlive(long Time) 
	{
		if (!isConnected) 
			return;
		if (m_isUdpRegistered && udp.needsKeepAlive(Time))
			Write(FrameworkMessage.keepAlive);
	}

	public void run() 
	{
		DebugManager.Log(CLASS_NAME, "Client thread started.");
		m_isShutdown = false;
		while (!m_isShutdown) 
		{
			try 
			{
				Update(250);
			}
			catch (IOException e) 
			{
				if (isConnected)
					DebugManager.Log(CLASS_NAME, this + " update: " + e.getMessage());
				else
					DebugManager.Log(CLASS_NAME, "Unable to update connection: " + e.getMessage());
				Close();
			}
			catch (KryoNetException e)
			{
				lastProtocolError = e;
				if (isConnected)
					DebugManager.Log(CLASS_NAME, "Error updating connection: " + this + ", Error: " + e.getMessage());
				else
					DebugManager.Log(CLASS_NAME, "Error updating connection.: " + e.getMessage());
				Close();
				throw e;
			}
		}
		DebugManager.Log(CLASS_NAME, "Client thread stopped.");
	}

	public void Start() 
	{
		// Try to let any previous update thread stop.
		if (m_updateThread != null) 
		{
			m_isShutdown = true;
			try 
			{
				m_updateThread.join(5000);
			}
			catch (InterruptedException Ignored) 
			{
			}
		}
		m_updateThread = new Thread(this, "Client");
		m_updateThread.setDaemon(true);
		m_updateThread.start();
	}

	public void Stop() 
	{
		if (m_isShutdown) 
			return;
		Close();
		DebugManager.Log(CLASS_NAME, "Client thread stopping.");
		m_isShutdown = true;
		m_selector.wakeup();
	}

	public void Close() 
	{
		super.Close();
		synchronized (m_updateLock) 
		{ // Blocks to avoid a select while the selector is used to bind the server connection.
		}
		// Select one last time to complete closing the socket.
		if (!m_isClosed)
		{
			m_isClosed = true;
			m_selector.wakeup();
			try 
			{
				m_selector.selectNow();
			}
			catch (IOException Ignored) 
			{
			}
		}
	}

	/** Releases the resources used by this client, which may no longer be used. */
	public void Dispose() throws IOException 
	{
		Close();
		m_selector.close();
	}

	public void AddListener(Listener listener) 
	{
		super.AddListener(listener);
		DebugManager.Log(CLASS_NAME, "Client listener added.");
	}

	public void RemoveListener(Listener listener) 
	{
		super.RemoveListener(listener);
		DebugManager.Log(CLASS_NAME, "Client listener removed.");
	}

	/** An empty object will be sent if the UDP connection is inactive more than the specified milliseconds. Network hardware may
	 * keep a translation table of inside to outside IP addresses and a UDP keep alive keeps this table entry from expiring. Set to
	 * zero to disable. Defaults to 19000. */
	public void SetKeepAliveUDP(int KeepAliveMillis) 
	{
		if (udp == null) 
			throw new IllegalStateException("Not connected via UDP.");
		udp.keepAliveMillis = KeepAliveMillis;
	}

	public Thread GetUpdateThread() 
	{
		return m_updateThread;
	}

	private void Broadcast(int UdpPort, DatagramSocket Socket) throws IOException 
	{
		ByteBuffer DataBuffer = ByteBuffer.allocate(64);
		m_serialization.write(null, DataBuffer, new DiscoverHost());
		DataBuffer.flip();
		byte[] Data = new byte[DataBuffer.limit()];
		DataBuffer.get(Data);
		for (NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) 
		{
			for (InetAddress address : Collections.list(iface.getInetAddresses())) 
			{
				// Java 1.5 doesn't support getting the subnet mask, so try the two most common.
				byte[] ip = address.getAddress();
				ip[3] = -1; // 255.255.255.0
				try 
				{
					Socket.send(new DatagramPacket(Data, Data.length, InetAddress.getByAddress(ip), UdpPort));
				} 
				catch (Exception Ignored) 
				{
				}
				ip[2] = -1; // 255.255.0.0
				try 
				{
					Socket.send(new DatagramPacket(Data, Data.length, InetAddress.getByAddress(ip), UdpPort));
				}
				catch (Exception Ignored) 
				{
				}
			}
		}
		DebugManager.Log(CLASS_NAME, "Broadcasted host discovery on port: " + UdpPort);
	}

	/** Broadcasts a UDP message on the LAN to discover any running servers. The address of the first server to respond is returned.
	 * @param UdpPort The UDP port of the server.
	 * @param TimeoutMillis The number of milliseconds to wait for a response.
	 * @return the first server found, or null if no server responded. */
	public InetAddress DiscoverHost(int UdpPort, int TimeoutMillis) 
	{
		DatagramSocket Socket = null;
		try 
		{
			Socket = new DatagramSocket();
			Broadcast(UdpPort, Socket);
			Socket.setSoTimeout(TimeoutMillis);
			DatagramPacket nPacket = m_discoveryHandler.onRequestNewDatagramPacket();
			try 
			{
				Socket.receive(nPacket);
			}
			catch (SocketTimeoutException ex) 
			{
				DebugManager.Log(CLASS_NAME, "Host discovery timed out.");
				return null;
			}
			DebugManager.Log(CLASS_NAME, "Discovered server: " + nPacket.getAddress());
			m_discoveryHandler.onDiscoveredHost(nPacket, GetKryo());
			return nPacket.getAddress();
		}
		catch (IOException e)
		{
			DebugManager.Log(CLASS_NAME, "Host discovery failed. Error: " + e.getMessage());
			return null;
		} 
		finally 
		{
			if (Socket != null) 
				Socket.close();
			m_discoveryHandler.onFinally();
		}
	}

	/** Broadcasts a UDP message on the LAN to discover any running servers.
	 * @param UdpPort The UDP port of the server.
	 * @param TimeoutMillis The number of milliseconds to wait for a response. */
	public List<InetAddress> DiscoverHosts(int UdpPort, int TimeoutMillis) 
	{
		List<InetAddress> Hosts = new ArrayList<InetAddress>();
		DatagramSocket Socket = null;
		try 
		{
			Socket = new DatagramSocket();
			Broadcast(UdpPort, Socket);
			Socket.setSoTimeout(TimeoutMillis);
			while (true) 
			{
				DatagramPacket packet = m_discoveryHandler.onRequestNewDatagramPacket();
				try 
				{
					Socket.receive(packet);
				}
				catch (SocketTimeoutException e) 
				{
					DebugManager.Log(CLASS_NAME, "Host discovery timed out.");
					return Hosts;
				}
				DebugManager.Log(CLASS_NAME, "Discovered server: " + packet.getAddress());
				m_discoveryHandler.onDiscoveredHost(packet, GetKryo());
				Hosts.add(packet.getAddress());
			}
		} 
		catch (IOException e) 
		{
			DebugManager.Log(CLASS_NAME, "Host discovery failed. Error: " + e.getMessage());
			return Hosts;
		}
		finally 
		{
			if (Socket != null) 
				Socket.close();
			m_discoveryHandler.onFinally();
		}
	}
}
