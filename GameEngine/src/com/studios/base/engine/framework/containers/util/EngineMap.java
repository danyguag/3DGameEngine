package com.studios.base.engine.framework.containers.util;

import java.util.HashMap;

public class EngineMap<K, V> extends HashMap<K, V> 
{
	private static final long serialVersionUID = 2901535640561143679L;

	public EngineMap()
	{
		super();
	}
	
	public EngineMap(int Min)
	{
		super(Min);
	}
	
	public void Put(K Key, V Value)
	{
		put(Key, Value);
	}
	
	public EngineList<K> GetKeys()
	{
		EngineList<K> Result = new EngineList<K>();
		for (K Key : keySet())
			Result.Add(Key);
		return Result;
	}
	
	public EngineList<V> GetValues()
	{
		EngineList<V> Result = new EngineList<V>();
		for (V Value : values())
			Result.Add(Value);
		return Result;
	}

	public int Length()
	{
		return size();
	}
}