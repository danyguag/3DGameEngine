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
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

import com.esotericsoftware.kryo.Kryo;
import com.studios.base.engine.framework.debugging.DebugManager;
import com.studios.base.engine.net.FrameworkMessage.Ping;

// BOZO - Layer to handle handshake state.

/** Represents a TCP and optionally a UDP connection between a {@link Client} and a {@link Server}. If either underlying connection
 * is closed or errors, both connections are closed.
 * @author Nathan Sweet <misc@n4te.com> */
public class Connection 
{
	private final String CLASS_NAME = getClass().getSimpleName();
	
	protected int id = -1;
	protected String name;
	protected EndPoint endPoint;
	protected UdpConnection udp;
	protected InetSocketAddress udpRemoteAddress;
	protected Listener[] listeners = {};
	protected Object listenerLock = new Object();
	protected int lastPingID;
	protected long lastPingSendTime;
	protected int returnTripTime;
	protected volatile boolean isConnected;
	protected volatile KryoNetException lastProtocolError;

	protected Connection () {
	}
	
	public void initialize(Serialization ser, int buffersize)
	{
		udp = new UdpConnection(ser, buffersize);
	}
	
	/** Returns the server assigned ID. Will return -1 if this connection has never been connected or the last assigned ID if this
	 * connection has been disconnected. */
	public int getID () {
		return id;
	}

	/** Returns true if this connection is connected to the remote end. Note that a connection can become disconnected at any time. */
	public boolean isConnected () {
		return isConnected;
	}
	
   /**
    * Returns the last protocol error that occured on the connection.
    * 
    * @return The last protocol error or null if none error occured.
    */
   public KryoNetException getLastProtocolError() {
      return lastProtocolError;
   }

	/** Sends the object over the network using UDP.
	 * @return The number of bytes sent.
	 * @see Kryo#register(Class, com.esotericsoftware.kryo.Serializer)
	 * @throws IllegalStateException if this connection was not opened with both TCP and UDP. */
	public int Write (Object object) {
		if (object == null) throw new IllegalArgumentException("object cannot be null.");
		SocketAddress address = udpRemoteAddress;
		if (address == null && udp != null) address = udp.connectedAddress;
		if (address == null && isConnected) throw new IllegalStateException("Connection is not connected via UDP.");

		try {
			if (address == null) throw new SocketException("Connection is closed.");

			int length = udp.send(this, object, address);
			if (length == 0) {
			}
			if (length != -1) 
			{
				String objectString = object == null ? "null" : object.getClass().getSimpleName();
				if (!(object instanceof FrameworkMessage)) 
				{
					DebugManager.Log(CLASS_NAME, this + " sent UDP: " + objectString + " (" + length + ")");
				} 
				else
				{
					DebugManager.Log(CLASS_NAME, this + " sent UDP: " + objectString + " (" + length + ")");
				}
			} else
				DebugManager.Log(CLASS_NAME, this + " was unable to send, UDP socket buffer full.");
			
			return length;
		} catch (IOException ex) {
			DebugManager.Log(CLASS_NAME, "Unable to send UDP with connection: " + this + ".  Error: " + ex);
			Close();
			return 0;
		} catch (KryoNetException ex) {
			DebugManager.Log(CLASS_NAME, "Unable to send UDP with connection: " + this + ".  Error: " + ex);
			Close();
			return 0;
		}
	}

	public void Close () {
		boolean wasConnected = isConnected;
		isConnected = false;
		if (udp != null && udp.connectedAddress != null) udp.close();
		if (wasConnected) {
			notifyDisconnected();
			DebugManager.Log(CLASS_NAME, this + " disconnected.");
		}
		setConnected(false, -1);
	}

	/** Requests the connection to communicate with the remote computer to determine a new value for the
	 * {@link #getReturnTripTime() return trip time}. When the connection receives a {@link FrameworkMessage.Ping} object with
	 * {@link Ping#isReply isReply} set to true, the new return trip time is available. */
	public void updateReturnTripTime () 
	{
		Ping ping = new Ping();
		ping.id = lastPingID++;
		lastPingSendTime = System.currentTimeMillis();
		Write(ping);
	}

	/** Returns the last calculated TCP return trip time, or -1 if {@link #updateReturnTripTime()} has never been called or the
	 * {@link FrameworkMessage.Ping} response has not yet been received. */
	public int getReturnTripTime () {
		return returnTripTime;
	}

	/** If the listener already exists, it is not added again. */
	public void AddListener (Listener listener) {
		if (listener == null) throw new IllegalArgumentException("listener cannot be null.");
		synchronized (listenerLock) {
			Listener[] listeners = this.listeners;
			int n = listeners.length;
			for (int i = 0; i < n; i++)
				if (listener == listeners[i]) return;
			Listener[] newListeners = new Listener[n + 1];
			newListeners[0] = listener;
			System.arraycopy(listeners, 0, newListeners, 1, n);
			this.listeners = newListeners;
		}
		DebugManager.Log(CLASS_NAME, "Connection listener added: " + listener.getClass().getName());
	}

	public void RemoveListener (Listener listener) {
		if (listener == null) throw new IllegalArgumentException("listener cannot be null.");
		synchronized (listenerLock) {
			Listener[] listeners = this.listeners;
			int n = listeners.length;
			if (n == 0) return;
			Listener[] newListeners = new Listener[n - 1];
			for (int i = 0, ii = 0; i < n; i++) {
				Listener copyListener = listeners[i];
				if (listener == copyListener) continue;
				if (ii == n - 1) return;
				newListeners[ii++] = copyListener;
			}
			this.listeners = newListeners;
		}
		DebugManager.Log(CLASS_NAME, "Connection listener removed: " + listener.getClass().getName());
	}

	void notifyConnected () {
		Listener[] listeners = this.listeners;
		for (int i = 0, n = listeners.length; i < n; i++)
			listeners[i].Connected(this);
	}

	void notifyDisconnected () {
		Listener[] listeners = this.listeners;
		for (int i = 0, n = listeners.length; i < n; i++)
			listeners[i].Disconnected(this);
	}

	void notifyIdle () {
		Listener[] listeners = this.listeners;
		for (int i = 0, n = listeners.length; i < n; i++) {
			listeners[i].Idle(this);
			if (!isIdle()) break;
		}
	}

	void notifyReceived (Object object) {
		if (object instanceof Ping) {
			Ping ping = (Ping)object;
			if (ping.isReply) {
				if (ping.id == lastPingID - 1) {
					returnTripTime = (int)(System.currentTimeMillis() - lastPingSendTime);
					DebugManager.Log(CLASS_NAME, this + " return trip time: " + returnTripTime);
				}
			} else {
				ping.isReply = true;
				Write(ping);
			}
		}
		Listener[] listeners = this.listeners;
		for (int i = 0, n = listeners.length; i < n; i++)
			listeners[i].MessageReceived(this, object);
	}

	/** Returns the local {@link Client} or {@link Server} to which this connection belongs. */
	public EndPoint getEndPoint () {
		return endPoint;
	}

	/** Returns the IP address and port of the remote end of the UDP connection, or null if this connection is not connected. */
	public InetSocketAddress getRemoteAddressUDP () {
		InetSocketAddress connectedAddress = udp.connectedAddress;
		if (connectedAddress != null) return connectedAddress;
		return udpRemoteAddress;
	}

	/** Sets the friendly name of this connection. This is returned by {@link #toString()} and is useful for providing application
	 * specific identifying information in the logging. May be null for the default name of "Connection X", where X is the
	 * connection ID. */
	public void setName (String name) {
		this.name = name;
	}

	/** Returns the number of bytes that are waiting to be written to the TCP socket, if any. */
	public int getTcpWriteBufferSize () {
		return udp.writeBuffer.position();
	}

	/** @see #setIdleThreshold(float) */
	public boolean isIdle () {
		return udp.writeBuffer.position() / (float)udp.writeBuffer.capacity() < udp.idleThreshold;
	}
	
	public void setIdleThreshold (float idleThreshold) {
		udp.idleThreshold = idleThreshold;
	}

	public String toString () {
		if (name != null) return name;
		return "Connection " + id;
	}

	void setConnected (boolean isConnected, int SentID) {
		this.isConnected = isConnected;
		id = SentID;
		if (isConnected && name == null) name = "Connection " + id;
	}
}
