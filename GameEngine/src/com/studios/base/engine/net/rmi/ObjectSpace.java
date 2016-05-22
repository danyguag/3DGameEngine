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

package com.studios.base.engine.net.rmi;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import com.esotericsoftware.kryo.util.IntMap;
import com.esotericsoftware.kryo.util.Util;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.studios.base.engine.framework.debugging.DebugManager;
import com.studios.base.engine.net.Connection;
import com.studios.base.engine.net.EndPoint;
import com.studios.base.engine.net.FrameworkMessage;
import com.studios.base.engine.net.KryoNetException;
import com.studios.base.engine.net.Listener;
import com.studios.base.engine.net.util.ObjectIntMap;

/** Allows methods on objects to be invoked remotely over TCP or UDP. Objects are {@link #Register(int, Object) registered} with an
 * ID. The remote end of connections that have been {@link #AddConnection(Connection) added} are allowed to
 * {@link #GetRemoteObject(Connection, int, Class) access} registered objects.
 * <p>
 * It costs at least 2 bytes more to use remote method invocation than just sending the parameters. If the method has a return
 * value which is not {@link RemoteObject#setNonBlocking(boolean) ignored}, an extra byte is written. If the type of a parameter is
 * not final (note primitives are final) then an extra byte is written for that parameter.
 * @author Nathan Sweet <misc@n4te.com> */
public class ObjectSpace 
{
	private final String CLASS_NAME = getClass().getSimpleName();
	
	private static final int returnValueMask = 1 << 7;
	private static final int returnExceptionMask = 1 << 6;
	private static final int responseIdMask = 0xff & ~returnValueMask & ~returnExceptionMask;

	private static final Object instancesLock = new Object();
	private static ObjectSpace[] instances = new ObjectSpace[0];
	private static final HashMap<Class, CachedMethod[]> methodCache = new HashMap<Class, CachedMethod[]>();
	private static boolean asm = true;

	private final IntMap<Object> idToObject = new IntMap<Object>();
	private final ObjectIntMap<Object> objectToID = new ObjectIntMap<Object>();
	private Connection[] connections = {};
	private final Object connectionsLock = new Object();
	private Executor executor;

	private final Listener invokeListener = new Listener() 
	{
		public void MessageReceived(final Connection nConnection, Object Message) 
		{
			if (!(Message instanceof InvokeMethod)) 
				return;
			if (connections != null) 
			{
				int i = 0, n = connections.length;
				for (; i < n; i++)
					if (nConnection == connections[i])
						break;
				if (i == n) 
					return; // The InvokeMethod message is not for a connection in this ObjectSpace.
			}
			final InvokeMethod invokeMethod = (InvokeMethod)Message;
			final Object target = idToObject.get(invokeMethod.objectID);
			if (target == null) 
			{
				DebugManager.Log(CLASS_NAME, "Ignoring remote invocation request for unknown object ID: " + invokeMethod.objectID);
				return;
			}
			if (executor == null)
				invoke(nConnection, target, invokeMethod);
			else
			{
				executor.execute(new Runnable() 
				{
					public void run() {
						invoke(nConnection, target, invokeMethod);
					}
				});
			}
		}

		public void Disconnected(Connection nConnection) 
		{
			RemoveConnection(nConnection);
		}
	};

	/** Creates an ObjectSpace with no connections. Connections must be {@link #AddConnection(Connection) added} to allow the remote
	 * end of the connections to access objects in this ObjectSpace. */
	public ObjectSpace() 
	{
		synchronized(instancesLock) 
		{
			ObjectSpace[] instances = ObjectSpace.instances;
			ObjectSpace[] newInstances = new ObjectSpace[instances.length + 1];
			newInstances[0] = this;
			System.arraycopy(instances, 0, newInstances, 1, instances.length);
			ObjectSpace.instances = newInstances;
		}
	}

	/** Creates an ObjectSpace with the specified connection. More connections can be {@link #AddConnection(Connection) added}. */
	public ObjectSpace (Connection connection) 
	{
		this();
		AddConnection(connection);
	}

	/** Sets the executor used to invoke methods when an invocation is received from a remote endpoint. By default, no executor is
	 * set and invocations occur on the network thread, which should not be blocked for long.
	 * @param Executor May be null. */
	public void SetExecutor(Executor Executor) 
	{
		this.executor = Executor;
	}

	/** Registers an object to allow the remote end of the ObjectSpace's connections to access it using the specified ID.
	 * <p>
	 * If a connection is added to multiple ObjectSpaces, the same object ID should not be registered in more than one of those
	 * ObjectSpaces.
	 * @param ObjectID Must not be Integer.MAX_VALUE.
	 * @see #GetRemoteObject(Connection, int, Class...) */
	public void Register(int ObjectID, Object Object) 
	{
		if (ObjectID == Integer.MAX_VALUE) 
			throw new IllegalArgumentException("objectID cannot be Integer.MAX_VALUE.");
		if (Object == null) 
			throw new IllegalArgumentException("object cannot be null.");
		idToObject.put(ObjectID, Object);
		objectToID.put(Object, ObjectID);
		DebugManager.Log(CLASS_NAME, "Object registered with ObjectSpace as " + ObjectID + ": " + Object);
	}

	/** Removes an object. The remote end of the ObjectSpace's connections will no longer be able to access it. */
	public void Remove(int ObjectID) 
	{
		Object object = idToObject.remove(ObjectID);
		if (object != null)
			objectToID.remove(object, 0);
		DebugManager.Log(CLASS_NAME, "Object " + ObjectID + " removed from ObjectSpace: " + object);
	}

	/** Removes an object. The remote end of the ObjectSpace's connections will no longer be able to access it. */
	public void remove (Object object) 
	{
		if (!idToObject.containsValue(object, true)) return;
		int objectID = idToObject.findKey(object, true, -1);
		idToObject.remove(objectID);
		objectToID.remove(object, 0);
		DebugManager.Log(CLASS_NAME, "Object " + objectID + " removed from ObjectSpace: " + object);
	}

	/** Causes this ObjectSpace to stop listening to the connections for method invocation messages. */
	public void Close() 
	{
		Connection[] connections = this.connections;
		for (int i = 0; i < connections.length; i++)
			connections[i].RemoveListener(invokeListener);

		synchronized (instancesLock) 
		{
			ArrayList<Connection> temp = new ArrayList(Arrays.asList(instances));
			temp.remove(this);
			instances = temp.toArray(new ObjectSpace[temp.size()]);
		}

		DebugManager.Log(CLASS_NAME, "Closed ObjectSpace.");
	}

	/** Allows the remote end of the specified connection to access objects registered in this ObjectSpace. */
	public void AddConnection(Connection nConnection) 
	{
		if (nConnection == null) 
			throw new IllegalArgumentException("connection cannot be null.");

		synchronized (connectionsLock) 
		{
			Connection[] newConnections = new Connection[connections.length + 1];
			newConnections[0] = nConnection;
			System.arraycopy(connections, 0, newConnections, 1, connections.length);
			connections = newConnections;
		}

		nConnection.AddListener(invokeListener);

		DebugManager.Log(CLASS_NAME, "Added connection to ObjectSpace: " + nConnection);
	}

	/** Removes the specified connection, it will no longer be able to access objects registered in this ObjectSpace. */
	public void RemoveConnection(Connection nConnection) 
	{
		if (nConnection == null) throw new IllegalArgumentException("connection cannot be null.");

		nConnection.RemoveListener(invokeListener);

		synchronized (connectionsLock) 
		{
			ArrayList<Connection> temp = new ArrayList<Connection>(Arrays.asList(connections));
			temp.remove(nConnection);
			connections = temp.toArray(new Connection[temp.size()]);
		}

		DebugManager.Log(CLASS_NAME, "Removed connection from ObjectSpace: " + nConnection);
	}

	/** Invokes the method on the object and, if necessary, sends the result back to the connection that made the invocation
	 * request. This method is invoked on the update thread of the {@link EndPoint} for this ObjectSpace and unless an
	 * {@link #SetExecutor(Executor) executor} has been set.
	 * @param nConnection The remote side of this connection requested the invocation. */
	protected void invoke(Connection nConnection, Object Target, InvokeMethod nInvokeMethod)
	{
		String argString = "";
		if (nInvokeMethod.args != null) 
		{
			argString = Arrays.deepToString(nInvokeMethod.args);
			argString = argString.substring(1, argString.length() - 1);
		}
		DebugManager.Log(CLASS_NAME,
				nConnection + " received: " + Target.getClass().getSimpleName() + "#" + nInvokeMethod.cachedMethod.method.getName()
					+ "(" + argString + ")");

		byte responseData = nInvokeMethod.responseData;
		boolean transmitReturnValue = (responseData & returnValueMask) == returnValueMask;
		boolean transmitExceptions = (responseData & returnExceptionMask) == returnExceptionMask;
		int responseID = responseData & responseIdMask;

		CachedMethod cachedMethod = nInvokeMethod.cachedMethod;
		Object result = null;
		try 
		{
			result = cachedMethod.invoke(Target, nInvokeMethod.args);
		}
		catch (InvocationTargetException ex) 
		{
			if (transmitExceptions)
				result = ex.getCause();
			else
				throw new KryoNetException("Error invoking method: " + cachedMethod.method.getDeclaringClass().getName() + "."
					+ cachedMethod.method.getName(), ex);
		}
		catch (Exception ex) 
		{
			throw new KryoNetException("Error invoking method: " + cachedMethod.method.getDeclaringClass().getName() + "."
				+ cachedMethod.method.getName(), ex);
		}

		if (responseID == 0) 
			return;

		InvokeMethodResult invokeMethodResult = new InvokeMethodResult();
		invokeMethodResult.objectID = nInvokeMethod.objectID;
		invokeMethodResult.responseID = (byte)responseID;

		// Do not return non-primitives if transmitReturnValue is false.
		if (!transmitReturnValue && !nInvokeMethod.cachedMethod.method.getReturnType().isPrimitive()) 
		{
			invokeMethodResult.result = null;
		} 
		else 
		{
			invokeMethodResult.result = result;
		}

		int length = nConnection.Write(invokeMethodResult);
		DebugManager.Log(CLASS_NAME, nConnection + " sent TCP: " + result + " (" + length + ")");
	}

	/** Identical to {@link #GetRemoteObject(Connection, int, Class...)} except returns the object cast to the specified interface
	 * type. The returned object still implements {@link RemoteObject}. */
	static public <T> T GetRemoteObject(final Connection nConnection, int ObjectID, Class<T> Iface) 
	{
		return (T)GetRemoteObject(nConnection, ObjectID, new Class[] {Iface});
	}

	/** Returns a proxy object that implements the specified interfaces. Methods invoked on the proxy object will be invoked
	 * remotely on the object with the specified ID in the ObjectSpace for the specified connection. If the remote end of the
	 * connection has not {@link #AddConnection(Connection) added} the connection to the ObjectSpace, the remote method invocations
	 * will be ignored.
	 * <p>
	 * Methods that return a value will throw {@link TimeoutException} if the response is not received with the
	 * {@link RemoteObject#setResponseTimeout(int) response timeout}.
	 * <p>
	 * If {@link RemoteObject#setNonBlocking(boolean) non-blocking} is false (the default), then methods that return a value must
	 * not be called from the update thread for the connection. An exception will be thrown if this occurs. Methods with a void
	 * return value can be called on the update thread.
	 * <p>
	 * If a proxy returned from this method is part of an object graph sent over the network, the object graph on the receiving
	 * side will have the proxy object replaced with the registered object.
	 * @see RemoteObject */
	public static RemoteObject GetRemoteObject(Connection nConnection, int ObjectID, Class... ifaces) 
	{
		if (nConnection == null) 
			throw new IllegalArgumentException("connection cannot be null.");
		if (ifaces == null) 
			throw new IllegalArgumentException("ifaces cannot be null.");
		Class[] temp = new Class[ifaces.length + 1];
		temp[0] = RemoteObject.class;
		System.arraycopy(ifaces, 0, temp, 1, ifaces.length);
		return (RemoteObject)Proxy.newProxyInstance(ObjectSpace.class.getClassLoader(), temp, new RemoteInvocationHandler(
			nConnection, ObjectID));
	}

	/** Handles network communication when methods are invoked on a proxy. */
	private static class RemoteInvocationHandler implements InvocationHandler 
	{
		private final Connection m_connection;
		final int m_objectID;
		private int m_timeoutMillis = 3000;
		private boolean m_nonBlocking;
		private boolean m_transmitReturnValue = true;
		private boolean m_transmitExceptions = true;
		private boolean m_remoteToString;
		private boolean m_udp;
		private Byte m_lastResponseID;
		private byte m_nextResponseId = 1;
		private Listener m_responseListener;

		private final ReentrantLock m_lock = new ReentrantLock();
		private final Condition m_responseCondition = m_lock.newCondition();
		private final InvokeMethodResult[] m_responseTable = new InvokeMethodResult[64];
		private final boolean[] m_pendingResponses = new boolean[64];

		public RemoteInvocationHandler(Connection nConnection, final int ObjectID) 
		{
			super();
			this.m_connection = nConnection;
			this.m_objectID = ObjectID;

			m_responseListener = new Listener() 
			{
				public void MessageReceived(Connection nConnection, Object Message)
				{
					if (!(Message instanceof InvokeMethodResult)) return;
					InvokeMethodResult invokeMethodResult = (InvokeMethodResult)Message;
					if (invokeMethodResult.objectID != ObjectID) return;

					int responseID = invokeMethodResult.responseID;
					synchronized (this) 
					{
						if (m_pendingResponses[responseID]) m_responseTable[responseID] = invokeMethodResult;
					}

					m_lock.lock();
					try 
					{
						m_responseCondition.signalAll();
					} 
					finally 
					{
						m_lock.unlock();
					}
				}

				public void Disconnected(Connection connection)
				{
					Close();
				}
			};
			nConnection.AddListener(m_responseListener);
		}

		public Object invoke(Object Proxy, Method nMethod, Object[] Args) throws Exception 
		{
			Class declaringClass = nMethod.getDeclaringClass();
			if (declaringClass == RemoteObject.class) 
			{
				String name = nMethod.getName();
				if (name.equals("close")) 
				{
					Close();
					return null;
				}
				else if (name.equals("setResponseTimeout")) 
				{
					m_timeoutMillis = (Integer)Args[0];
					return null;
				} 
				else if (name.equals("setNonBlocking")) 
				{
					m_nonBlocking = (Boolean)Args[0];
					return null;
				}
				else if (name.equals("setTransmitReturnValue")) 
				{
					m_transmitReturnValue = (Boolean)Args[0];
					return null;
				}
				else if (name.equals("setUDP")) 
				{
					m_udp = (Boolean)Args[0];
					return null;
				}
				else if (name.equals("setTransmitExceptions")) 
				{
					m_transmitExceptions = (Boolean)Args[0];
					return null;
				}
				else if (name.equals("setRemoteToString")) 
				{
					m_remoteToString = (Boolean)Args[0];
					return null;
				}
				else if (name.equals("waitForLastResponse")) 
				{
					if (m_lastResponseID == null) throw new IllegalStateException("There is no last response to wait for.");
					return waitForResponse(m_lastResponseID);
				}
				else if (name.equals("getLastResponseID")) 
				{
					if (m_lastResponseID == null) throw new IllegalStateException("There is no last response ID.");
					return m_lastResponseID;
				}
				else if (name.equals("waitForResponse")) 
				{
					if (!m_transmitReturnValue && !m_transmitExceptions && m_nonBlocking)
						throw new IllegalStateException("This RemoteObject is currently set to ignore all responses.");
					return waitForResponse((Byte)Args[0]);
				}
				else if (name.equals("getConnection")) 
				{
					return m_connection;
				}
				// Should never happen, for debugging purposes only
				throw new KryoNetException("Invocation handler could not find RemoteObject method. Check ObjectSpace.java");
			} 
			else if (!m_remoteToString && declaringClass == Object.class && nMethod.getName().equals("toString")) //
				return "<proxy>";

			InvokeMethod invokeMethod = new InvokeMethod();
			invokeMethod.objectID = m_objectID;
			invokeMethod.args = Args;

			CachedMethod[] cachedMethods = getMethods(m_connection.getEndPoint().GetKryo(), nMethod.getDeclaringClass());
			for (int i = 0, n = cachedMethods.length; i < n; i++) 
			{
				CachedMethod cachedMethod = cachedMethods[i];
				if (cachedMethod.method.equals(nMethod)) 
				{
					invokeMethod.cachedMethod = cachedMethod;
					break;
				}
			}
			if (invokeMethod.cachedMethod == null) throw new KryoNetException("Method not found: " + nMethod);

			// A invocation doesn't need a response if it's async and no return values or exceptions are wanted back.
			boolean needsResponse = !m_udp && (m_transmitReturnValue || m_transmitExceptions || !m_nonBlocking);
			byte responseID = 0;
			if (needsResponse) 
			{
				synchronized (this) 
				{
					// Increment the response counter and put it into the low bits of the responseID.
					responseID = m_nextResponseId++;
					if (m_nextResponseId > responseIdMask) m_nextResponseId = 1;
					m_pendingResponses[responseID] = true;
				}
				// Pack other data into the high bits.
				byte responseData = responseID;
				if (m_transmitReturnValue) responseData |= returnValueMask;
				if (m_transmitExceptions) responseData |= returnExceptionMask;
				invokeMethod.responseData = responseData;
			} 
			else 
			{
				invokeMethod.responseData = 0; // A response data of 0 means to not respond.
			}
			int length = m_connection.Write(invokeMethod);
			String argString = "";
			if (Args != null) {
				argString = Arrays.deepToString(Args);
				argString = argString.substring(1, argString.length() - 1);
			}
			DebugManager.Log(getClass().getSimpleName(), m_connection + " sent " + (m_udp ? "UDP" : "TCP") + ": " + nMethod.getDeclaringClass().getSimpleName()
					+ "#" + nMethod.getName() + "(" + argString + ") (" + length + ")");

			m_lastResponseID = (byte)(invokeMethod.responseData & responseIdMask);
			if (m_nonBlocking || m_udp) 
			{
				Class returnType = nMethod.getReturnType();
				if (returnType.isPrimitive()) 
				{
					if (returnType == int.class) return 0;
					if (returnType == boolean.class) return Boolean.FALSE;
					if (returnType == float.class) return 0f;
					if (returnType == char.class) return (char)0;
					if (returnType == long.class) return 0l;
					if (returnType == short.class) return (short)0;
					if (returnType == byte.class) return (byte)0;
					if (returnType == double.class) return 0d;
				}
				return null;
			}
			try 
			{
				Object result = waitForResponse(m_lastResponseID);
				if (result != null && result instanceof Exception)
					throw (Exception)result;
				else
					return result;
			}
			catch (TimeoutException ex) 
			{
				throw new TimeoutException("Response timed out: " + nMethod.getDeclaringClass().getName() + "." + nMethod.getName());
			}
			finally 
			{
				synchronized (this) 
				{
					m_pendingResponses[responseID] = false;
					m_responseTable[responseID] = null;
				}
			}
		}

		private Object waitForResponse(byte responseID) 
		{
			if (m_connection.getEndPoint().GetUpdateThread() == Thread.currentThread())
				throw new IllegalStateException("Cannot wait for an RMI response on the connection's update thread.");

			long endTime = System.currentTimeMillis() + m_timeoutMillis;

			while (true) 
			{
				long remaining = endTime - System.currentTimeMillis();
				InvokeMethodResult invokeMethodResult;
				synchronized (this) 
				{
					invokeMethodResult = m_responseTable[responseID];
				}
				if (invokeMethodResult != null) 
				{
					m_lastResponseID = null;
					return invokeMethodResult.result;
				}
				else 
				{
					if (remaining <= 0) throw new TimeoutException("Response timed out.");

					m_lock.lock();
					try 
					{
						m_responseCondition.await(remaining, TimeUnit.MILLISECONDS);
					}
					catch (InterruptedException e) 
					{
						Thread.currentThread().interrupt();
						throw new KryoNetException(e);
					}
					finally 
					{
						m_lock.unlock();
					}
				}
			}
		}

		void Close() 
		{
			m_connection.RemoveListener(m_responseListener);
		}
	}

	/** Internal message to invoke methods remotely. */
	public static class InvokeMethod implements FrameworkMessage, KryoSerializable 
	{
		public int objectID;
		public CachedMethod cachedMethod;
		public Object[] args;

		// The top bits of the ID indicate if the remote invocation should respond with return values and exceptions, respectively.
		// The remaining bites are a counter. This means up to 63 responses can be stored before undefined behavior occurs due to
		// possible duplicate IDs. A response data of 0 means to not respond.
		public byte responseData;

		public void write(Kryo kryo, Output output) 
		{
			output.writeInt(objectID, true);
			output.writeInt(cachedMethod.methodClassID, true);
			output.writeByte(cachedMethod.methodIndex);

			Serializer[] serializers = cachedMethod.serializers;
			Object[] args = this.args;
			for (int i = 0, n = serializers.length; i < n; i++) 
			{
				Serializer serializer = serializers[i];
				if (serializer != null)
					kryo.writeObjectOrNull(output, args[i], serializer);
				else
					kryo.writeClassAndObject(output, args[i]);
			}

			output.writeByte(responseData);
		}

		public void read(Kryo kryo, Input input) 
		{
			objectID = input.readInt(true);

			int methodClassID = input.readInt(true);
			Class methodClass = kryo.getRegistration(methodClassID).getType();

			byte methodIndex = input.readByte();
			try 
			{
				cachedMethod = getMethods(kryo, methodClass)[methodIndex];
			}
			catch (IndexOutOfBoundsException ex) 
			{
				throw new KryoException("Invalid method index " + methodIndex + " for class: " + methodClass.getName());
			}

			Serializer[] serializers = cachedMethod.serializers;
			Class[] parameterTypes = cachedMethod.method.getParameterTypes();
			Object[] args = new Object[serializers.length];
			this.args = args;
			for (int i = 0, n = args.length; i < n; i++) 
			{
				Serializer serializer = serializers[i];
				if (serializer != null)
					args[i] = kryo.readObjectOrNull(input, parameterTypes[i], serializer);
				else
					args[i] = kryo.readClassAndObject(input);
			}

			responseData = input.readByte();
		}
	}

	/** Internal message to return the result of a remotely invoked method. */
	public static class InvokeMethodResult implements FrameworkMessage 
	{
		public int objectID;
		public byte responseID;
		public Object result;
	}

	static CachedMethod[] getMethods (Kryo kryo, Class type) 
	{
		CachedMethod[] cachedMethods = methodCache.get(type); // Maybe should cache per Kryo instance?
		if (cachedMethods != null) return cachedMethods;

		ArrayList<Method> allMethods = new ArrayList();
		Class nextClass = type;
		while (nextClass != null) 
		{
			Collections.addAll(allMethods, nextClass.getDeclaredMethods());
			nextClass = nextClass.getSuperclass();
			if (nextClass == Object.class) break;
		}
		ArrayList<Method> methods = new ArrayList(Math.max(1, allMethods.size()));
		for (int i = 0, n = allMethods.size(); i < n; i++) 
		{
			Method method = allMethods.get(i);
			int modifiers = method.getModifiers();
			if (Modifier.isStatic(modifiers)) continue;
			if (Modifier.isPrivate(modifiers)) continue;
			if (method.isSynthetic()) continue;
			methods.add(method);
		}
		Collections.sort(methods, new Comparator<Method>() 
		{
			public int compare(Method o1, Method o2) 
			{
				// Methods are sorted so they can be represented as an index.
				int diff = o1.getName().compareTo(o2.getName());
				if (diff != 0) return diff;
				Class[] argTypes1 = o1.getParameterTypes();
				Class[] argTypes2 = o2.getParameterTypes();
				if (argTypes1.length > argTypes2.length) return 1;
				if (argTypes1.length < argTypes2.length) return -1;
				for (int i = 0; i < argTypes1.length; i++) 
				{
					diff = argTypes1[i].getName().compareTo(argTypes2[i].getName());
					if (diff != 0) return diff;
				}
				throw new RuntimeException("Two methods with same signature!"); // Impossible.
			}
		});

		Object methodAccess = null;
		if (asm && !Util.isAndroid && Modifier.isPublic(type.getModifiers())) methodAccess = MethodAccess.get(type);

		int n = methods.size();
		cachedMethods = new CachedMethod[n];
		for (int i = 0; i < n; i++) 
		{
			Method method = methods.get(i);
			Class[] parameterTypes = method.getParameterTypes();

			CachedMethod cachedMethod = null;
			if (methodAccess != null) 
			{
				try 
				{
					AsmCachedMethod asmCachedMethod = new AsmCachedMethod();
					asmCachedMethod.methodAccessIndex = ((MethodAccess)methodAccess).getIndex(method.getName(), parameterTypes);
					asmCachedMethod.methodAccess = (MethodAccess)methodAccess;
					cachedMethod = asmCachedMethod;
				} 
				catch (RuntimeException ignored) 
				{
				}
			}

			if (cachedMethod == null) 
				cachedMethod = new CachedMethod();
			cachedMethod.method = method;
			cachedMethod.methodClassID = kryo.getRegistration(method.getDeclaringClass()).getId();
			cachedMethod.methodIndex = i;

			// Store the serializer for each final parameter.
			cachedMethod.serializers = new Serializer[parameterTypes.length];
			for (int ii = 0, nn = parameterTypes.length; ii < nn; ii++)
				if (kryo.isFinal(parameterTypes[ii])) cachedMethod.serializers[ii] = kryo.getSerializer(parameterTypes[ii]);

			cachedMethods[i] = cachedMethod;
		}
		methodCache.put(type, cachedMethods);
		return cachedMethods;
	}

	/** Returns the first object registered with the specified ID in any of the ObjectSpaces the specified connection belongs to. */
	static Object GetRegisteredObject(Connection nConnection, int ObjectID)
	{
		ObjectSpace[] instances = ObjectSpace.instances;
		for (int i = 0, n = instances.length; i < n; i++) 
		{
			ObjectSpace objectSpace = instances[i];
			// Check if the connection is in this ObjectSpace.
			Connection[] connections = objectSpace.connections;
			for (int j = 0; j < connections.length; j++) 
			{
				if (connections[j] != nConnection) 
					continue;
				// Find an object with the objectID.
				Object object = objectSpace.idToObject.get(ObjectID);
				if (object != null) return object;
			}
		}
		return null;
	}

	/** Returns the first ID registered for the specified object with any of the ObjectSpaces the specified connection belongs to,
	 * or Integer.MAX_VALUE if not found. */
	static int GetRegisteredID(Connection nConnection, Object nObject) 
	{
		ObjectSpace[] instances = ObjectSpace.instances;
		for (int i = 0, n = instances.length; i < n; i++) 
		{
			ObjectSpace objectSpace = instances[i];
			// Check if the connection is in this ObjectSpace.
			Connection[] connections = objectSpace.connections;
			for (int j = 0; j < connections.length; j++) 
			{
				if (connections[j] != nConnection) continue;
				// Find an ID with the object.
				int id = objectSpace.objectToID.get(nObject, Integer.MAX_VALUE);
				if (id != Integer.MAX_VALUE) return id;
			}
		}
		return Integer.MAX_VALUE;
	}

	/** Registers the classes needed to use ObjectSpaces. This should be called before any connections are opened.
	 * @see Kryo#register(Class, Serializer) */
	static public void registerClasses(final Kryo kryo) 
	{
		kryo.register(Object[].class);
		kryo.register(InvokeMethod.class);

		FieldSerializer<InvokeMethodResult> resultSerializer = new FieldSerializer<InvokeMethodResult>(kryo,
			InvokeMethodResult.class) 
		{
			public void write(Kryo kryo, Output output, InvokeMethodResult result) 
			{
				super.write(kryo, output, result);
				output.writeInt(result.objectID, true);
			}

			public InvokeMethodResult read(Kryo kryo, Input input, Class<InvokeMethodResult> type) 
			{
				InvokeMethodResult result = super.read(kryo, input, type);
				result.objectID = input.readInt(true);
				return result;
			}
		};
		resultSerializer.removeField("objectID");
		kryo.register(InvokeMethodResult.class, resultSerializer);

		kryo.register(InvocationHandler.class, new Serializer() 
		{
			public void write (Kryo kryo, Output output, Object object) 
			{
				RemoteInvocationHandler handler = (RemoteInvocationHandler)Proxy.getInvocationHandler(object);
				output.writeInt(handler.m_objectID, true);
			}

			public Object read (Kryo kryo, Input input, Class type) 
			{
				int objectID = input.readInt(true);
				Connection connection = (Connection)kryo.getContext().get("connection");
				Object object = GetRegisteredObject(connection, objectID);
				if (object == null) 
					DebugManager.Log(getClass().getSimpleName(), "Unknown object ID " + objectID + " for connection: " + connection);
				return object;
			}
		});
	}

	/** If true, an attempt will be made to use ReflectASM for invoking methods. Default is true. */
	static public void SetAsm(boolean asm) 
	{
		ObjectSpace.asm = asm;
	}

	static class CachedMethod 
	{
		Method method;
		int methodClassID;
		int methodIndex;
		Serializer[] serializers;

		public Object invoke (Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
			return method.invoke(target, args);
		}
	}

	static class AsmCachedMethod extends CachedMethod 
	{
		MethodAccess methodAccess;
		int methodAccessIndex = -1;

		public Object invoke (Object target, Object[] args) throws IllegalAccessException, InvocationTargetException 
		{
			try 
			{
				return methodAccess.invoke(target, methodAccessIndex, args);
			}
			catch (Exception ex) 
			{
				throw new InvocationTargetException(ex);
			}
		}
	}

	/** Serializes an object registered with an ObjectSpace so the receiving side gets a {@link RemoteObject} proxy rather than the
	 * bytes for the serialized object.
	 * @author Nathan Sweet <misc@n4te.com> */
	static public class RemoteObjectSerializer extends Serializer 
	{
		public void write (Kryo kryo, Output output, Object object) 
		{
			Connection connection = (Connection)kryo.getContext().get("connection");
			int id = GetRegisteredID(connection, object);
			if (id == Integer.MAX_VALUE) throw new KryoNetException("Object not found in an ObjectSpace: " + object);
			output.writeInt(id, true);
		}

		public Object read (Kryo kryo, Input input, Class type) 
		{
			int objectID = input.readInt(true);
			Connection connection = (Connection)kryo.getContext().get("connection");
			return ObjectSpace.GetRemoteObject(connection, objectID, type);
		}
	}
}
