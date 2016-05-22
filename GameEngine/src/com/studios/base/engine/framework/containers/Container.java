package com.studios.base.engine.framework.containers;

import java.util.ArrayList;

import com.studios.base.engine.framework.containers.util.EngineList;

public class Container<T>
{
	private int m_size;
	
	private EngineList<Container<T>> m_children;
	private EngineList<T> m_storage;
	
	public Container(int Size)
	{
		m_size = Size;
		m_children = new EngineList<Container<T>>(Size);
		m_storage = new EngineList<T>(Size);
	}
	
	public void AddContainer(Container<T> nContainer)
	{
		if ((m_children.size() + m_storage.size()) >= m_size)
			return;
		m_children.add(nContainer);
	}
	
	public void AddToContainer(T nStorage)
	{
		if ((m_children.size() + m_storage.size()) >= m_size)
			return;
		m_storage.add(nStorage);
	}
	
	public ArrayList<Container<T>> GetChildren()
	{
		return m_children;
	}
	
	public ArrayList<T> GetStorage()
	{
		return m_storage;
	}
}
