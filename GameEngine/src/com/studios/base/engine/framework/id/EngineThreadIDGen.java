package com.studios.base.engine.framework.id;

import java.util.concurrent.atomic.AtomicInteger;

public class EngineThreadIDGen
{
	private static final AtomicInteger m_threadID = new AtomicInteger(0);
	
	public Integer GetNextThreadID()
	{
		return m_threadID.getAndIncrement();
	}
}
