package com.studios.base.engine.framework.containers.util;

import java.util.Enumeration;
import java.util.Hashtable;

public class EngineDictionary<K, V> extends Hashtable<K, V>
{
	private static final long serialVersionUID = -376698138059122507L;
	
	public EngineList<K> GetKeys()
	{
		EngineList<K> Result = new EngineList<K>();
		Enumeration<K> RawRes = keys();
		while (RawRes.hasMoreElements())
			Result.Add(RawRes.nextElement());
		return Result;
	}
	
	public EngineList<V> GetValues()
	{
		EngineList<V> Result = new EngineList<V>();
		for (V Value : values())
			Result.Add(Value);
		return Result;
	}
}
