package com.studios.base.engine.framework.animation;

import com.studios.base.engine.rendering.shader.Shader;

public class Frame 
{
	private Bone m_bone;
	private int m_frame;
	
	public Frame(Bone nBone, int Frame)
	{
		m_bone = nBone;
		m_frame = Frame;
	}
	
	public void Render(Shader shader, int Frame)
	{
		if (Frame == m_frame)
		{
			m_bone.Render(shader);
		}
	}
	
	public Bone GetBone()
	{
		return m_bone;
	}
	
	public int GetFrame()
	{
		return m_frame;
	}
}
