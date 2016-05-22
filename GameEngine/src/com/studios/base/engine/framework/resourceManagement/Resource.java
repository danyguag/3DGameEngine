package com.studios.base.engine.framework.resourceManagement;

import com.studios.base.engine.framework.containers.util.EngineList;

public interface Resource 
{
	public int GetVaoID();
	public int GetVertexCount();
	public void SetVaoID();
	public EngineList<Integer> GetBuffers();
	public int GetBufferByIndex(int Index);
}
