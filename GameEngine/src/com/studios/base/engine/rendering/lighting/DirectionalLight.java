package com.studios.base.engine.rendering.lighting;

import com.studios.base.engine.framework.math.Vector3f;

public class DirectionalLight implements Light
{
	public Vector3f m_position;
	public Vector3f m_color;
	
	public DirectionalLight(Vector3f Position, Vector3f Color)
	{
		m_position = Position;
		m_color = Color;
	}

	@Override
	public LightType GetType() 
	{
		return LightType.DIRECTIONAL;
	}

	@Override
	public Vector3f GetPosition() 
	{
		return m_position;
	}

	@Override
	public void SetPosition(Vector3f Position) 
	{
		m_position = Position;
	}

	@Override
	public Vector3f GetColor() 
	{
		return m_color;
	}

	@Override
	public void SetColor(Vector3f Color) 
	{
		m_color = Color;
	}
	
}
