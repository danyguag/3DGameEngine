package com.studios.base.engine.framework.components;

import com.studios.base.engine.framework.math.Vector3f;
import com.studios.base.engine.framework.threading.Snapshot;
import com.studios.base.engine.framework.transform.TransformType;
import com.studios.base.engine.physics.PhysicsEngine;
import com.studios.base.engine.rendering.shader.Shader;

public class PhysicsComponent extends GameComponent
{
	private PhysicsEngine m_physicsEngine;
	
	public PhysicsComponent()
	{
		super.Name = "PhysicsComponent";
	}

	@Override
	public void Init() 
	{ 
	}
	
	@Override
	public void Render(Shader nShader)
	{
		
	}
	
	@Override
	public void Update(Snapshot CurrentGameShot) { }
	
	
	public void Simulate(Vector3f Direction, float Speed)
	{
		GetTransform3f().GetPosition().PlusEqual(new Vector3f(Direction.GetX() * Speed, 
				Direction.GetY() * Speed, Direction.GetZ() * Speed));
	}

	public void Simulate(float X, float Y, float Z, float Speed)
	{
		Simulate(new Vector3f(X, Y, Z), Speed);
	}
	
	public void SetPhysicsEngine()
	{
		m_physicsEngine = m_parent.GetCoreEngine().GetPhysicsEngine();
	}
	
	public PhysicsEngine GetPhysicsEngine()
	{
		return m_physicsEngine;
	}
	
	@Override
	public TransformType GetTransformType() 
	{
		return TransformType.NEITHER;
	}
}
