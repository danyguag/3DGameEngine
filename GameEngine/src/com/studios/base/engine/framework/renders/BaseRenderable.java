package com.studios.base.engine.framework.renders;

import com.studios.base.engine.framework.components.GameComponent;
import com.studios.base.engine.framework.resourceManagement.Resource;

public interface BaseRenderable
{
	boolean ShouldRender();
	boolean UsingParent();
	void SetUsingParent(boolean UsingParent);
	void SetRender(boolean Render);
	Resource GetResource();
	int GetVertexAttribArrayIndex();
	int GetVertexCount();  
	int GetDrawMode();     
	int GetDrawType();  
	int GetIndicesOffset();
	RenderType GetType();
	
	GameComponent GetGameComponentParent();
	void SetParent(GameComponent Parent);
}
