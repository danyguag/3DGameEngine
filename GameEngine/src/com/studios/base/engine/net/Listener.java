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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.studios.base.engine.framework.debugging.DebugManager;

/** Used to be notified about connection events. */
public class Listener 
{
	/** Called when the remote end has been connected. This will be invoked before any objects are received by
	 * {@link #MessageReceived(Connection, Object)}. This will be invoked on the same thread as {@link Client#Update(int)} and
	 * {@link Server#update(int)}. This method should not block for long periods as other network activity will not be processed
	 * until it returns. */
	public void Connected(Connection nConnection) 
	{
	}

	/** Called when the remote end is no longer connected. There is no guarantee as to what thread will invoke this method. */
	public void Disconnected(Connection nConnection) 
	{
	}

	/** Called when an object has been received from the remote end of the connection. This will be invoked on the same thread as
	 * {@link Client#Update(int)} and {@link Server#update(int)}. This method should not block for long periods as other network
	 * activity will not be processed until it returns. */
	public void MessageReceived(Connection nConnection, Object Message) 
	{
	}

	/** Called when the connection is below the {@link Connection#setIdleThreshold(float) idle threshold}. */
	public void Idle(Connection nConnection) 
	{
	}

	/** Uses reflection to called "received(Connection, XXX)" on the listener, where XXX is the received object type. Note this
	 * class uses a HashMap lookup and (cached) reflection, so is not as efficient as writing a series of "instanceof" statements. */
	static public class ReflectionListener extends Listener 
	{
		private final HashMap<Class, Method> m_classToMethod = new HashMap<Class, Method>();

		public void MessageReceived (Connection nConnection, Object Message)
		{
			Class type = Message.getClass();
			Method method = m_classToMethod.get(type);
			if (method == null) 
			{
				if (m_classToMethod.containsKey(type)) 
					return; // Only fail on the first attempt to find the method.
				try 
				{
					method = getClass().getMethod("received", new Class[] {Connection.class, type});
					method.setAccessible(true);
				}
				catch (SecurityException ex) 
				{
					DebugManager.Log(getClass().getSimpleName(), "Unable to access method: received(Connection, " + type.getName() + "). Error: " + ex);
					return;
				} 
				catch (NoSuchMethodException ex) 
				{
					DebugManager.Log(getClass().getSimpleName(),
						"Unable to find listener method: " + getClass().getName() + "#received(Connection, " + type.getName() + ")");
					return;
				}
				finally 
				{
					m_classToMethod.put(type, method);
				}
			}
			try 
			{
				method.invoke(this, nConnection, Message);
			} 
			catch(Throwable e) 
			{
				if (e instanceof InvocationTargetException && e.getCause() != null) e = e.getCause();
				if (e instanceof RuntimeException) throw (RuntimeException)e;
				throw new RuntimeException("Error invoking method: " + getClass().getName() + "#received(Connection, "
					+ type.getName() + ")", e);
			}
		}
	}

	/** Wraps a listener and queues notifications as {@link Runnable runnables}. This allows the runnables to be processed on a
	 * different thread, preventing the connection's update thread from being blocked. */
	static public abstract class QueuedListener extends Listener 
	{
		private final Listener m_listener;

		public QueuedListener (Listener nListener) 
		{
			if (nListener == null) 
				throw new IllegalArgumentException("listener cannot be null.");
			this.m_listener = nListener;
		}

		public void Connected(final Connection nConnection) 
		 {
			queue(new Runnable() 
			{
				public void run () 
				{
					m_listener.Connected(nConnection);
				}
			});
		}

		public void Disconnected(final Connection nConnection) 
		{
			queue(new Runnable() 
			{
				public void run() 
				{
					m_listener.Disconnected(nConnection);
				}
			});
		}

		public void MessageReceived(final Connection nConnection, final Object Message) 
		{
			queue(new Runnable() 
			{
				public void run() 
				{
					m_listener.MessageReceived(nConnection, Message);
				}
			});
		}

		public void Idle(final Connection nConnection) 
		{
			queue(new Runnable() 
			{
				public void run() 
				{
					m_listener.Idle(nConnection);
				}
			});
		}

		 protected abstract void queue (Runnable runnable);
	}

	/** Wraps a listener and processes notification events on a separate thread. */
	static public class ThreadedListener extends QueuedListener 
	{
		protected final ExecutorService m_threadPool;

		/** Creates a single thread to process notification events. */
		public ThreadedListener (Listener nListener) 
		{
			this(nListener, Executors.newFixedThreadPool(1));
		}

		/** Uses the specified threadPool to process notification events. */
		public ThreadedListener(Listener nListener, ExecutorService nThreadPool) 
		{
			super(nListener);
			if (nThreadPool == null) 
				throw new IllegalArgumentException("threadPool cannot be null.");
			m_threadPool = nThreadPool;
		}

		public void queue(Runnable nRunnable)
		{
			m_threadPool.execute(nRunnable);
		}
	}

	/** Delays the notification of the wrapped listener to simulate lag on incoming objects. Notification events are processed on a
	 * separate thread after a delay. Note that only incoming objects are delayed. To delay outgoing objects, use a LagListener at
	 * the other end of the connection. */
	static public class LagListener extends QueuedListener 
	{
		private final ScheduledExecutorService m_threadPool;
		private final int m_lagMillisMin, m_lagMillisMax;
		private final LinkedList<Runnable> m_runnables = new LinkedList<Runnable>();

		public LagListener(int LagMillisMin, int LagMillisMax, Listener nListener) 
		{
			super(nListener);
			this.m_lagMillisMin = LagMillisMin;
			this.m_lagMillisMax = LagMillisMax;
			m_threadPool = Executors.newScheduledThreadPool(1);
		}

		public void queue(Runnable nRunnable)
		{
			synchronized (m_runnables) 
			{
				m_runnables.addFirst(nRunnable);
			}
			int Lag = m_lagMillisMin + (int)(Math.random() * (m_lagMillisMax - m_lagMillisMin));
			m_threadPool.schedule(new Runnable() 
			{
				public void run() 
				{
					Runnable runnable;
					synchronized (m_runnables) 
					{
						runnable = m_runnables.removeLast();
					}
					runnable.run();
				}
			}, Lag, TimeUnit.MILLISECONDS);
		}
	}
}
