package com.studios.base.engine.rendering.model;

import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glLineWidth;

import com.studios.base.engine.framework.components.GameComponent;
import com.studios.base.engine.framework.math.Vector2f;
import com.studios.base.engine.framework.math.Vector3f;
import com.studios.base.engine.framework.renders.BaseRenderable;
import com.studios.base.engine.framework.renders.RenderType;
import com.studios.base.engine.framework.resourceManagement.LineResource;
import com.studios.base.engine.framework.resourceManagement.Resource;
import com.studios.base.engine.rendering.data.Vertex2f;

public class Line implements BaseRenderable
{
	private Vertex2f m_lineStart;
	private Vertex2f m_lineEnd;

	private Vector3f m_color;
	
	private int m_vertexCount;
	private int m_width;
	
	private LineResource m_resource;
	private GameComponent m_parent;
	
	private boolean m_render = true;
	private boolean m_usingParent = true;
	
	private final int m_drawMode = GL_LINE_STRIP;
	private final int m_drawType = GL_UNSIGNED_INT;
	private final int m_indicesDrawOffset = 0;
	private final int m_vertexAttribArrayIndex = 1;
	
	public Line(Vertex2f LineStart, Vertex2f LineEnd, Vector3f Color, int Width)
	{
		m_resource = new LineResource();
		
		m_lineStart = LineStart;
		m_lineEnd   = LineEnd;
		m_color = Color;
		
		Vertex2f[] Vertices = new Vertex2f[] 
				{
						m_lineStart,
						m_lineEnd
				};
		
		m_vertexCount = Vertices.length * 2; 
		m_width = Width;
		
		if (m_width > 1 || m_width < 0)
			m_width = 1 / m_width;
		
		glLineWidth(m_width);
		
		m_resource.Init(Vertices);
	}
	
	public void SetLineEndAndStart(Vertex2f LineStart, Vertex2f LineEnd)
	{
		m_lineStart = LineStart;
		m_lineEnd = LineEnd;
	}
	
	@Override
	public void SetRender(boolean Render) 
	{
		m_render = Render;
	}

	@Override
	public int GetVertexAttribArrayIndex() 
	{
		return m_vertexAttribArrayIndex;
	}

	@Override
	public int GetDrawMode() 
	{
		return m_drawMode;
	}

	@Override
	public int GetDrawType() 
	{
		return m_drawType;
	}

	@Override
	public int GetIndicesOffset() 
	{
		return m_indicesDrawOffset;
	}

	@Override
	public Resource GetResource() 
	{
		return m_resource;
	}

	@Override
	public boolean ShouldRender() 
	{
		return m_render;
	}
	
	@Override
	public RenderType GetType()
	{
		return RenderType.LINE;
	}
	
	public Vector2f GetLineStart()
	{
		return m_lineStart;
	}
	
	public Vector2f GetLineEnd()
	{
		return m_lineEnd;
	}	
	
	public int GetVertexCount()
	{
		return m_vertexCount;
	}
	
	public int GetLineWidth()
	{
		return m_width;
	}
	
	public Vector3f GetColor()
	{
		return m_color;
	}

	@Override
	public boolean UsingParent() 
	{
		return m_usingParent;
	}

	@Override
	public void SetUsingParent(boolean UsingParent) 
	{
		m_usingParent = UsingParent;
	}

	@Override
	public GameComponent GetGameComponentParent() 
	{
		return m_parent;
	}

	@Override
	public void SetParent(GameComponent Parent) 
	{
		m_parent = Parent;
	}
}
