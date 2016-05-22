package com.studios.base.engine.framework.id;

public abstract class IDGen 
{
	protected int m_currentID = 0;
	
	public abstract int GetNextID();
	
	protected int GetCurrentID()
	{
		return m_currentID;
	}
}
