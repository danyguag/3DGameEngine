package com.studios.base.engine.framework.containers;

import com.studios.base.engine.framework.math.Vector2f;
import com.studios.base.engine.framework.renders.BaseRenderable;

public class RenderContainer<T extends BaseRenderable> extends Container<T>
{
	private T m_parent;
		
	public RenderContainer(T Parent, int Size) 
	{
		super(Size);
		m_parent = Parent;
		AddToContainer(m_parent);
	}
	
	public void Add(T Child, Vector2f Position)
	{
		AddToContainer(Child);
	}
	
	public T GetBase()
	{
		return m_parent;
	}
}
