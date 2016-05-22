package com.studios.base.engine.rendering;

import com.studios.base.engine.framework.math.Vector3f;
import com.studios.base.engine.rendering.shader.Shader;

public class RenderOptions 
{
	private Vector3f m_ambient;

	private Shader m_uiShader;
	private Shader m_lineShader;
	private Shader m_fontShader;
	
	public RenderOptions(Shader UIShader, Shader LineShader, Shader FontShader)
	{
		m_ambient = new Vector3f(.2f, .2f, .2f);
		
		m_uiShader = UIShader;
		m_lineShader = LineShader;
		m_fontShader = FontShader;
	}
	
	public void SetFontShader(Shader FontShader)
	{
		m_fontShader = FontShader;
	}
	
	public void SetUIShader(Shader UIShader)
	{
		m_uiShader = UIShader;
	}
	
	public void SetLineShader(Shader LineShader)
	{
		m_lineShader = LineShader;
	}
	
	public void SetAmbient(float X, float Y, float Z)
	{
		m_ambient = new Vector3f(X, Y, Z);
	}
	
	public Shader GetUIShader()
	{
		return m_uiShader;
	}
	
	public Shader GetLineShader()
	{
		return m_lineShader;
	}

	public Shader GetFontshader()
	{
		return m_fontShader;
	}
	
	public Vector3f GetAmbientLight()
	{
		return m_ambient;
	}
}
