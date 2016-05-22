package com.studios.base.engine.framework.animation;

import com.studios.base.engine.framework.transform.Transform3f;
import com.studios.base.engine.rendering.data.Vertex3f;
import com.studios.base.engine.rendering.shader.Shader;

public class Bone 
{
	private Vertex3f m_vertex;
	private Transform3f m_transform;
	
	private int m_frame;
	
	public Bone(Vertex3f Vertex, Transform3f nTransform)
	{
		m_vertex = Vertex;
		m_transform = nTransform;
	}
	
	public void Render(Shader shader)
	{
	}

	public Vertex3f GetVertex()
	{
		return m_vertex;
	}

	public void SetVertex(Vertex3f m_vertex)
	{
		this.m_vertex = m_vertex;
	}

	public Transform3f GetTransform() 
	{
		return m_transform;
	}

	public void SetTransform(Transform3f m_transform) 
	{
		this.m_transform = m_transform;
	}

	public int GetFrame() 
	{
		return m_frame;
	}

	public void SetFrame(int Frame) 
	{
		m_frame = Frame;
	}
}
