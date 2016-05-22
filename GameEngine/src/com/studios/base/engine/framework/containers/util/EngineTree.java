package com.studios.base.engine.framework.containers.util;

public class EngineTree<T> 
{
	private EngineList<EngineTree<T>> m_tree;
	private T m_self;
	
	public EngineTree(T Self)
	{
		m_tree = new EngineList<EngineTree<T>>();
		m_self = Self;
	}
	
	public void AddBranch(EngineTree<T> Branch)
	{
		m_tree.Add(Branch);
	}
	
	public void RemoveBranch(EngineTree<T> Branch)
	{
		m_tree.Remove(Branch);
	}
	
	public EngineList<EngineTree<T>> GetTree()
	{
		return m_tree;
	}
	
	public T GetSelf()
	{
		return m_self;
	}
}
