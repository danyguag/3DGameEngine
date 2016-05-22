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
import java.net.InetSocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.util.IntMap;
import com.studios.base.engine.framework.containers.util.EngineList;
import com.studios.base.engine.framework.debugging.DebugManager;
import com.studios.base.engine.net.FrameworkMessage.DiscoverHost;
import com.studios.base.engine.net.FrameworkMessage.KeepAlive;
import com.studios.base.engine.net.FrameworkMessage.RegisterUDP;
import com.studios.base.engine.net.messaging.Packet;

/**
 * Manages TCP and optionally UDP connections from many {@link Client Clients}.
 * 
 * @author Nathan Sweet <misc@n4te.com>
 */
public class Server implements EndPoint 
{
	private final String CLASS_NAME = getClass().getSimpleName();
	
	private final Serialization m_serialization;
	private final int m_writeBufferSize, m_objectBufferSize;
	private final Selector m_selector;
	private int m_emptySelects;
	private UdpConnection m_udp;
	private EngineList<Connection> m_connections = new EngineList<Connection>();
	private IntMap<Connection> m_pendingConnections = new IntMap<Connection>();
	private Listener[] m_listeners = {};
	private Object m_listenerLock = new Object();
	private Object m_connectionLock = new Object();
	private int m_nextConnectionID = 1;
	private volatile boolean m_shutdown;
	private Object m_updateLock = new Object();
	private Thread m_updateThread;
	private ServerDiscoveryHandler m_discoveryHandler;

	private Listener m_dispatchListener = new Listener() 
	{
		public void Connected(Connection connection) 
		{
			Listener[] listeners = Server.this.m_listeners;
			for (int i = 0, n = listeners.length; i < n; i++)
				listeners[i].Connected(connection);
		}

		public void Disconnected(Connection connection) 
		{
			RemoveConnection(connection);
			Listener[] listeners = Server.this.m_listeners;
			for (int i = 0, n = listeners.length; i < n; i++)
				listeners[i].Disconnected(connection);
		}

		public void MessageReceived(Connection connection, Object object) 
		{
			Listener[] listeners = Server.this.m_listeners;
			for (int i = 0, n = listeners.length; i < n; i++)
				listeners[i].MessageReceived(connection, object);
		}

		public void Idle(Connection connection) 
		{
			Listener[] listeners = Server.this.m_listeners;
			for (int i = 0, n = listeners.length; i < n; i++)
				listeners[i].Idle(connection);
		}
	};

	/**
	 * Creates a Server with a write buffer size of 16384 and an object buffer
	 * size of 2048.
	 */
	public Server() 
	{
		this(16384, 2048);
	}

	/**
	 * @param writeBufferSize
	 *            One buffer of this size is allocated for each connected
	 *            client. Objects are serialized to the write buffer where the
	 *            bytes are queued until they can be written to the TCP socket.
	 *            <p>
	 *            Normally the socket is writable and the bytes are written
	 *            immediately. If the socket cannot be written to and enough
	 *            serialized objects are queued to overflow the buffer, then the
	 *            connection will be closed.
	 *            <p>
	 *            The write buffer should be sized at least as large as the
	 *            largest object that will be sent, plus some head room to allow
	 *            for some serialized objects to be queued in case the buffer is
	 *            temporarily not writable. The amount of head room needed is
	 *            dependent upon the size of objects being sent and how often
	 *            they are sent.
	 * @param objectBufferSize
	 *            One (using only TCP) or three (using both TCP and UDP) buffers
	 *            of this size are allocated. These buffers are used to hold the
	 *            bytes for a single object graph until it can be sent over the
	 *            network or deserialized.
	 *            <p>
	 *            The object buffers should be sized at least as large as the
	 *            largest object that will be sent or received.
	 */
	public Server(int writeBufferSize, int objectBufferSize) 
	{
		this(writeBufferSize, objectBufferSize, new KryoSerialization());
	}

	public Server(int writeBufferSize, int objectBufferSize, Serialization serialization) 
	{
		this.m_writeBufferSize = writeBufferSize;
		this.m_objectBufferSize = objectBufferSize;

		this.m_serialization = serialization;

		this.m_discoveryHandler = ServerDiscoveryHandler.DEFAULT;

		try 
		{
			m_selector = Selector.open();
		}
		catch (IOException ex) 
		{
			throw new RuntimeException("Error opening selector.", ex);
		}
	}

	public void SetDiscoveryHandler(ServerDiscoveryHandler newDiscoveryHandler) 
	{
		m_discoveryHandler = newDiscoveryHandler;
	}

	public Serialization GetSerialization() 
	{
		return m_serialization;
	}

	public Kryo GetKryo() 
	{
		return ((KryoSerialization) m_serialization).getKryo();
	}

	/**
	 * @param udpPort
	 *            May be null.
	 */
	public void Bind(InetSocketAddress udpPort) throws IOException
	{
		synchronized (m_updateLock) {
			m_selector.wakeup();
			try {
				m_udp = new UdpConnection(m_serialization, m_objectBufferSize);
				m_udp.bind(m_selector, udpPort);
				DebugManager.Log(CLASS_NAME, "Accepting connections on port: " + udpPort + "/UDP");
			} catch (IOException ex) {
				Close();
				throw ex;
			}
		}
		DebugManager.Log(CLASS_NAME, "Server has opened.");
	}

	/**
	 * Accepts any new connections and reads or writes any pending data for the
	 * current connections.
	 * 
	 * @param timeout
	 *            Wait for up to the specified milliseconds for a connection to
	 *            be ready to process. May be zero to return immediately if
	 *            there are no connections to process.
	 */
	public void Update(int Timeout) throws IOException 
	{
		m_updateThread = Thread.currentThread();
		synchronized (m_updateLock) { // Blocks to avoid a select while the
									// selector is used to bind the server
									// connection.
		}
		long startTime = System.currentTimeMillis();
		int select = 0;
		if (Timeout > 0) 
		{
			select = m_selector.select(Timeout);
		}
		else 
		{
			select = m_selector.selectNow();
		}
		if (select == 0) 
		{
			m_emptySelects++;
			if (m_emptySelects == 100) 
			{
				m_emptySelects = 0;
				// NIO freaks and returns immediately with 0 sometimes, so try
				// to keep from hogging the CPU.
				long elapsedTime = System.currentTimeMillis() - startTime;
				try 
				{
					if (elapsedTime < 25)
						Thread.sleep(25 - elapsedTime);
				}
				catch (InterruptedException e)
				{
				}
			}
		}
		else 
		{
			m_emptySelects = 0;
			Set<SelectionKey> keys = m_selector.selectedKeys();
			synchronized (keys)
			{
				UdpConnection udp = this.m_udp;
				outer: for (Iterator<SelectionKey> iter = keys.iterator(); iter.hasNext();) 
				{
					KeepAlive();
					SelectionKey selectionKey = iter.next();
					iter.remove();
					Connection fromConnection = (Connection) selectionKey.attachment();
					try 
					{
						// Must be a UDP read operation.
						if (udp == null) 
						{
							selectionKey.channel().close();
							continue;
						}
						InetSocketAddress fromAddress;
						try 
						{
							fromAddress = udp.readFromAddress();
						}
						catch (IOException ex) 
						{
							DebugManager.Log(CLASS_NAME, "Error reading UDP data. Error: " + ex.getMessage());
							continue;
						}
						if (fromAddress == null)
							continue;
						EngineList<Connection> Connections = m_connections;
						for (Connection nConnection : Connections) 
						{
							if (fromAddress.equals(nConnection.udpRemoteAddress)) 
							{
								fromConnection = nConnection;
								break;
							}
						}
//						if (fromConnection == null)
//							return;
						
					
						Object object;
						try 
						{
							object = udp.readObject(fromConnection);
							System.out.println(object);
						} catch (KryoNetException ex) 
						{
							if (fromConnection != null) 
							{
								DebugManager.Log(CLASS_NAME, "Error reading UDP from connection: " + fromConnection + ".  Error: " + ex);
							} 
							else
								DebugManager.Log(CLASS_NAME, "Error reading UDP from unregistered address: " + fromAddress + ".  Error: " + ex);
							continue;
						}
						
						
						if (object instanceof Packet)
						{
							m_listeners[0].MessageReceived(fromConnection, object);
						}
						
						if (object instanceof FrameworkMessage) 
						{
							if (object instanceof RegisterUDP) 
							{
								RegisterUDP reg = (RegisterUDP) object;
								// Store the fromAddress on the connection and
								// reply over TCP with a RegisterUDP to indicate
								// success.
								
								int fromConnectionID = reg.connectionID;
								Connection connection = null;
								
								if (fromConnectionID == -1)
								{
									fromConnectionID = AcceptOperation();
									
									connection = m_pendingConnections.remove(fromConnectionID);
								}
								
								
								if (connection != null) 
								{
									if (connection.udpRemoteAddress != null)
										continue outer;
									connection.udpRemoteAddress = fromAddress;
									AddConnection(connection);
									RegisterUDP Reg = new RegisterUDP();
									Reg.connectionID = fromConnectionID;
									connection.Write(Reg);
									
									DebugManager.Log(CLASS_NAME, "Port " + udp.datagramChannel.socket().getLocalPort()
											+ "/UDP connected to: " + fromAddress);
									connection.notifyConnected();
									continue;
								}
								DebugManager.Log(CLASS_NAME, "Ignoring incoming RegisterUDP with invalid connection ID: "
											+ fromConnectionID);
								continue;
							}
							if (object instanceof DiscoverHost) 
							{
								try 
								{
									boolean responseSent = m_discoveryHandler.onDiscoverHost(udp.datagramChannel,
											fromAddress, m_serialization);
									if (responseSent)
										DebugManager.Log(CLASS_NAME, "Responded to host discovery from: " + fromAddress);
								} 
								catch (IOException ex) 
								{
									DebugManager.Log(CLASS_NAME, "Error replying to host discovery from: " + fromAddress + ".  Error: " + ex);
								}
								continue;
							}
						}

						if (fromConnection != null) 
						{
							String objectString = object == null ? "null" : object.getClass().getSimpleName();
							if (object instanceof FrameworkMessage) 
							{
								DebugManager.Log(CLASS_NAME, fromConnection + " received UDP: " + objectString);
							} 
							else
								DebugManager.Log(CLASS_NAME, fromConnection + " received UDP: " + objectString);
							fromConnection.notifyReceived(object);
							DebugManager.Log(CLASS_NAME, "Notified Connection");
							continue;
						}
						DebugManager.Log(CLASS_NAME, "Ignoring UDP from unregistered address: " + fromAddress);
					} catch (CancelledKeyException ex) 
					{
						if (fromConnection != null)
							fromConnection.Close();
						else
							selectionKey.channel().close();
					}
				}
			}
		}
		long time = System.currentTimeMillis();
		synchronized (m_connectionLock) 
		{
			EngineList<Connection> Connections = m_connections;
			for (int Index = 0; Index < Connections.size(); Index++)
			{
				Connection nConnection = Connections.get(Index);
				if (nConnection.udp.needsKeepAlive(time)) 
					KeepAlive();
				

				if (nConnection.udp.isTimedOut(time))
				{
					DebugManager.Log(CLASS_NAME, nConnection + " timed out.");
					nConnection.Close();
				}
				
				if (nConnection.isIdle())
					nConnection.notifyIdle();
			}
		}
	}
	
	private int AcceptOperation() 
	{
		Connection connection = NewConnection();
		connection.initialize(m_serialization, m_writeBufferSize);
		connection.udpRemoteAddress = null;
		connection.endPoint = this;
		UdpConnection udp = this.m_udp;
		if (udp != null) 
			connection.udp = udp;

		int id = m_nextConnectionID++;
		if (m_nextConnectionID == -1) m_nextConnectionID = 1;
		connection.setConnected(true, id);
		connection.AddListener(m_dispatchListener);

		if (udp == null)
			AddConnection(connection);
		else
		{
			m_pendingConnections.put(id, connection);
		}

		if (udp == null) connection.notifyConnected();
		return connection.id;
	}

	private void KeepAlive() 
	{
		long time = System.currentTimeMillis();
		EngineList<Connection> Connections = m_connections;
		for (int Index = 0; Index < Connections.size(); Index++) 
		{
			Connection nConnection = Connections.get(Index);
			if (nConnection.udp.needsKeepAlive(time))
				nConnection.Write(FrameworkMessage.keepAlive);
		}
	}
	
	@Override
	public void run() 
	{
		DebugManager.Log(CLASS_NAME, "Server thread started.");
		m_shutdown = false;
		while (!m_shutdown) 
		{
			try 
			{
				Update(250);
			}
			catch (IOException ex) 
			{
			
				DebugManager.Log("Server", "Error updating server connections.  Error: " + ex.getMessage());
				Close();
			}
		}
		DebugManager.Log(CLASS_NAME, "Server thread stopped.");
	}

	public void Start() 
	{
		new Thread(this, "Server").start();
	}

	public void Stop() 
	{
		if (m_shutdown)
			return;
		Close();
		DebugManager.Log(CLASS_NAME, "Server thread stopping.");
		m_shutdown = true;
	}

	/**
	 * Allows the connections used by the server to be subclassed. This can be
	 * useful for storage per connection without an additional lookup.
	 */
	protected Connection NewConnection() 
	{
		return new Connection();
	}

	private void AddConnection(Connection connection) 
	{
		synchronized (m_connectionLock) 
		{
			m_connections.Add(connection);
		}
	}

	public void RemoveConnection(Connection connection) 
	{
		synchronized (m_connectionLock) 
		{
			m_connections.Remove(connection);
		}
	}

	// BOZO - Provide mechanism for sending to multiple clients without
	// serializing multiple times.

	public void SendToAll(Object Message) 
	{
		synchronized (m_connectionLock) 
		{
			EngineList<Connection> Connections = m_connections;
			for (int Index = 0; Index < Connections.size(); Index++) 
			{
				Connection nConnection = Connections.get(Index);
				nConnection.Write(Message);
			}
		}
		
	}

	public void WriteToAllExcept(int nConnectionID, Object Message) 
	{
		synchronized (m_connectionLock) 
		{
			EngineList<Connection> Connections = m_connections;
			for (int Index = 0; Index < Connections.size(); Index++) 
			{
				Connection nConnection = Connections.get(Index);
				if (nConnection.id != nConnectionID)
					nConnection.Write(Message);
			}
		}
	}

	public void WriteToConnection(int nConnectionID, Object Message) 
	{
		synchronized (m_connectionLock) 
		{
			EngineList<Connection> Connections = m_connections;
			for (int Index = 0; Index < Connections.size(); Index++) 
			{
				Connection nConnection = Connections.get(Index);
				if (nConnection.id == nConnectionID) 
				{
					nConnection.Write(Message);
					break;
				}
			}
		}
	}

	public void AddListener(Listener nListener) 
	{
		if (nListener == null)
			throw new IllegalArgumentException("listener cannot be null.");
		synchronized (m_listenerLock) 
		{
			int n = m_listeners.length;
			for (int i = 0; i < n; i++)
				if (nListener == m_listeners[i])
					return;
			Listener[] newListeners = new Listener[n + 1];
			newListeners[0] = nListener;
			System.arraycopy(m_listeners, 0, newListeners, 1, n);
			this.m_listeners = newListeners;
		}
		DebugManager.Log(CLASS_NAME, "Server listener added: " + nListener.getClass().getName());
	}

	public void RemoveListener(Listener nListener) 
	{
		if (nListener == null)
			throw new IllegalArgumentException("listener cannot be null.");
		synchronized (m_listenerLock) 
		{
			Listener[] listeners = this.m_listeners;
			int n = listeners.length;
			Listener[] newListeners = new Listener[n - 1];
			for (int i = 0, ii = 0; i < n; i++) 
			{
				Listener copyListener = listeners[i];
				if (nListener == copyListener)
					continue;
				if (ii == n - 1)
					return;
				newListeners[ii++] = copyListener;
			}
			this.m_listeners = newListeners;
		}
		DebugManager.Log(CLASS_NAME, "Server listener removed: " + nListener.getClass().getName());
	}

	/** Closes all open connections and the server port(s). */
	public void Close() 
	{
		synchronized (m_connectionLock) 
		{
			if (m_connections.size() > 0)
				DebugManager.Log(CLASS_NAME, "Closing server connections...");
			for (int i = 0, n = m_connections.size(); i < n; i++)
				m_connections.get(i).Close();
			m_connections = null;
		}

		m_udp.close();
		m_udp = null;

		synchronized (m_updateLock) 
		{ 
			// Blocks to avoid a select while the
			// selector is used to bind the server
			// connection.
		}
		// Select one last time to complete closing the socket.
		m_selector.wakeup();
		try 
		{
			m_selector.selectNow();
		} 
		catch (IOException Ignored) 
		{
		}
	}

	/**
	 * Releases the resources used by this server, which may no longer be used.
	 */
	public void Dispose() throws IOException 
	{
		Close();
		m_selector.close();
	}

	public Thread GetUpdateThread() 
	{
		return m_updateThread;
	}

	/**
	 * Returns the current connections. The array returned should not be
	 * modified.
	 */
	public EngineList<Connection> GetConnections() 
	{
		return m_connections;
	}
}
