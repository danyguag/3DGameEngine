package com.studios.base.engine.rendering.lighting;

import com.studios.base.engine.framework.math.Vector3f;

public interface Light 
{
	public LightType GetType();
	public Vector3f GetPosition();
	public void SetPosition(Vector3f Position);
	public Vector3f GetColor();
	public void SetColor(Vector3f Color);
}
