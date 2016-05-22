package com.studios.base.engine.framework.components;

import com.studios.base.engine.framework.math.Vector3f;
import com.studios.base.engine.framework.threading.Snapshot;
import com.studios.base.engine.framework.transform.TransformType;
import com.studios.base.engine.rendering.shader.ComputeShader;
import com.studios.base.engine.rendering.shader.Shader;

public class ParticleComponent extends GameComponent
{
	private Vector3f m_position;
	
	private String ShaderPath;
	private String ShaderName;
	private int WorkGroupX;
	private int WorkGroupY;
	private int WorkGroupZ;
	
	public ParticleComponent(Vector3f Position)
	{
		super.Name = "Particle Component";
		m_position = Position;
	}
	
	@Override
	public void Init() 
	{
		GetTransform3f().SetPosition(m_position);
		GetGraphics().AddComputeShader(ShaderPath, ShaderName, WorkGroupX, WorkGroupY, WorkGroupZ);
	}
	
	@Override
	public void Render(Shader nShader)
	{
		
	}
	
	@Override
	public void Update(Snapshot CurrentGameShot) 
	{
	}
	
	public ComputeShader GetComputeShader()
	{
		return GetGraphics().GetComputeShaderByName(ShaderName);
	}

	@Override
	public TransformType GetTransformType() 
	{
		return TransformType.NEITHER;
	}
}
