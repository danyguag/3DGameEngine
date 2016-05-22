package com.studios.base.engine.framework.threading;

import java.util.concurrent.locks.ReentrantLock;

import com.studios.base.engine.framework.containers.util.EngineList;

public class ThreadQueue<T extends EngineThread>
{
	public ReentrantLock m_cpuLock = new ReentrantLock();
	private EngineList<T> m_threads;
	
	public ThreadQueue()
	{
		m_threads = new EngineList<T>();
	}
	
	public void AddThread(T Thread)
	{
		Thread.SetReentrantLock(m_cpuLock);
		Thread.start();
		m_threads.add(Thread);
	}
	
	public EngineList<T> GetThreads()
	{
		return m_threads;
	}
}
