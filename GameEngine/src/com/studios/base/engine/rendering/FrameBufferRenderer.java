package com.studios.base.engine.rendering;

import com.studios.base.engine.rendering.enums.ClearType;

public interface FrameBufferRenderer 
{
	public void Init();
	public void Render();
	public void CleanUp();

	public ClearType[] GetClearTypes();
}
