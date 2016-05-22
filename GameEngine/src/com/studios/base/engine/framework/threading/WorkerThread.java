package com.studios.base.engine.framework.threading;

public abstract class WorkerThread extends EngineThread
{
	@Override
	public void destroy()
	{
		
	}
	
	@Override
	public void Update() 
	{
		Run();
	}
	
	public abstract void Run();
	
	@Override
	public void GetContext() 
	{ }

	@Override
	public void SetContextAgain() { }

	@Override
	public boolean RenderingThread() 
	{
		return false;
	}

	@Override
	public void GetGlSync() 
	{ }
}
