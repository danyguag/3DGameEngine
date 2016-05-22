package com.studios.base.engine.framework.containers.util;

import java.util.ArrayList;

public class EngineList<T> extends ArrayList<T> 
{
	private static final long serialVersionUID = -8797551087554807347L;

	public EngineList()
	{
		super();
	}
	
	public EngineList(int Min)
	{
		super(Min);
	}
	
	public T Get(Integer Index)
	{
		return get(Index);
	}
	
	public boolean Add(T Data)
	{
		return add(Data);
	}
	
	public boolean Remove(T Data)
	{
		return remove(Data);
	}

	public int Length()
	{
		return size();
	}
}
