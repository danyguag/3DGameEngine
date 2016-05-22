package com.studios.base.engine.framework.game;

import com.studios.base.engine.framework.components.GameComponent;
import com.studios.base.engine.framework.math.Matrix4f;
import com.studios.base.engine.framework.threading.Snapshot;
import com.studios.base.engine.framework.transform.TransformType;
import com.studios.base.engine.rendering.shader.Shader;

public class Camera extends GameComponent
{	
	private static final float FOV = 70;
	private static final float NEAR_PLANE = .1f;
	private static final float FAR_PLANE = 1000;
	
	private float m_pitch, m_yaw, m_roll;

	private Matrix4f m_projectionMatrix;
	
	public Camera() 
	{ 
		super.Name = "Camera";
		m_pitch = m_yaw = m_roll = 0;
	}

	@Override
	public void Init() 
	{
		m_projectionMatrix = CreateProjectionMatrix();
	}

	@Override
	public void Render(Shader nShader)
	{
	}
	
	@Override
	public void Update(Snapshot CurrentGameShot) 
	{
	}
	
	public Matrix4f GetViewMatrix()
	{
//		Matrix4f Result = new Matrix4f();
//		Result.setIdentity();
		
//		Matrix4f.rotate((float) Math.toRadians(GetPitch()), new Vector3f(1, 0, 0), Result, Result);
//		Matrix4f.rotate((float) Math.toRadians(GetYaw()), new Vector3f(0, 1, 0), Result, Result);
//		Matrix4f.rotate((float) Math.toRadians(GetRoll()), new Vector3f(0, 0, 1), Result, Result);
		Matrix4f Roation = new Matrix4f().InitRotation(GetPitch(), GetYaw(), GetRoll());
		
		Matrix4f translation = new Matrix4f().InitTranslation(-GetTransform3f().GetPosition().GetX(), -GetTransform3f().GetPosition().GetY(), -GetTransform3f().GetPosition().GetZ());
		
		return Roation.Mul(translation);
	}
	
	public Matrix4f GetProjectionMatrix()
	{
		return m_projectionMatrix;
	}
	
	private Matrix4f CreateProjectionMatrix() 
	{
		float AR = (float) GetCoreEngine().GetWindowEngine().GetWindow().GetWidth() / (float) GetCoreEngine().GetWindowEngine().GetWindow().GetHeight();
		float y_scale = (float) ((1 / Math.tan(Math.toRadians(FOV / 2))) * AR);
		float x_scale = y_scale / AR;
		float frustum_length = FAR_PLANE - NEAR_PLANE;
		Matrix4f Result = new Matrix4f();
		Result.Set(0, 0, x_scale);
		Result.Set(1, 1, y_scale);
		Result.Set(2, 2, -((FAR_PLANE + NEAR_PLANE) / frustum_length));
		Result.Set(2, 3,-1);
		Result.Set(3, 2, -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length));
		Result.Set(3, 3, 0);
		return Result;
//		return new Matrix4f().InitPerspective((float) Math.toRadians(70), AR, .01f, 1000);
	}

	
	public float GetPitch()
	{
		return m_pitch;
	}
	
	public float GetYaw()
	{
		return m_yaw;
	}
	
	public float GetRoll()
	{
		return m_roll;
	}

	public void SetYaw(float nPitch)
	{
		m_pitch = nPitch;
	}

	public void SetPitch(float nYaw)
	{
		m_yaw = nYaw;
	}

	public void SetRoll(float nRoll)
	{
		m_roll = nRoll;
	}

	@Override
	public TransformType GetTransformType() 
	{
		return TransformType.TRANSFROM_3F;
	}
}
