package com.studios.base.engine.framework.threading;

import java.util.concurrent.locks.ReentrantLock;

import com.studios.base.engine.core.CoresManager;

public abstract class EngineThread extends Thread
{
	private ReentrantLock m_lock;
	private int m_threadID = -1;
	
	private boolean m_cpulocked = false;
	
	@Override
	public void run()
	{
//		if (RenderingThread())
//		{
//			int SetCount = 0;
//			while (GetContext() == null)
//			{
//				++SetCount;
//				SetContextAgain();
//				
//				if (SetCount >= 1000)
//					break;
//			}
//			try
//			{
//				if (SetCount < 1000)
//					GetContext().makeCurrent();
//			}
//			catch (LWJGLException e)
//			{
//				e.printStackTrace(CoresManager.LoggerStream);
//			}
//		}
		
		Update();
//		if (RenderingThread())
//		{
//			GetContext().destroy();
//			GetGlSync();
//		}
		UnLock();
	}

	public abstract void GetContext();
	public abstract void SetContextAgain();
	
	public abstract boolean RenderingThread();
	public abstract void GetGlSync();
	public abstract void Update();
	
	protected ReentrantLock GetCpuLock()
	{
		return m_lock;
	}
	
	public void SetReentrantLock(ReentrantLock Lock)
	{
		m_lock = Lock;
	}
	
	public int ThreadID()
	{
		if (m_threadID == -1)
			m_threadID = CoresManager.IDGen.Thread.GetNextThreadID();
		return m_threadID;
	}
	
	protected void LockCpu()
	{
		if (m_cpulocked)
			return;
		else
		{
			m_cpulocked = true;
			GetCpuLock().lock();
		}
	}
	
	@Override
	public void destroy() 
	{
		
	}
	
	protected void UnLock()
	{
		if (m_cpulocked)
			GetCpuLock().unlock();
	}
}
