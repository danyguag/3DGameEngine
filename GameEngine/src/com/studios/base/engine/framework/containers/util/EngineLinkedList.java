package com.studios.base.engine.framework.containers.util;

import java.util.LinkedList;

public class EngineLinkedList<T> extends LinkedList<T> 
{
	private static final long serialVersionUID = 924857029384701298L;
	
	public boolean Add(T Data)
	{
		return add(Data);
	}
}
