package com.studios.base.engine.framework.debugging;

public class Timer
{
    private final long start;
    private String m_message;

    public Timer(String message)
    {
        m_message = message;
        start = System.nanoTime();
    }

	public double Stop()
    {
        long now = System.nanoTime();
        double time = (now - start) / 1000000;
        DebugManager.Log("Timer", m_message + ": " + time + " ms");
        return time;
    }

	public long GetStart() 
    {
		return start;
	}

	public String GetMessage() 
	{
		return m_message;
	}
}
