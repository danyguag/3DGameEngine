package com.studios.base.engine.rendering.model;

import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;

import com.studios.base.engine.framework.components.GameComponent;
import com.studios.base.engine.framework.renders.BaseRenderable;
import com.studios.base.engine.framework.renders.RenderType;
import com.studios.base.engine.framework.resourceManagement.ModelResource;
import com.studios.base.engine.framework.resourceManagement.Resource;
import com.studios.base.engine.rendering.data.Normal;
import com.studios.base.engine.rendering.data.TextureCoordinate;
import com.studios.base.engine.rendering.data.Vertex3f;

public class Model implements BaseRenderable
{
	protected ModelResource m_resource;
	protected GameComponent m_parent;
	
	protected int m_vertexCount;
	protected Vertex3f[] m_vertices;
	protected Normal[] m_normals;
	protected int[] m_indices;
	protected TextureCoordinate[] m_textureCoords;

	protected boolean m_render = true;
	protected boolean m_usingParent = true;
	
	protected final int m_drawMode = GL_TRIANGLES;
	protected final int m_drawType = GL_UNSIGNED_INT;
	protected final int m_indicesDrawOffset = 0;
	protected final int m_vertexAttribArrayIndex = 2;

	public Model()
	{
		m_resource = new ModelResource();
	}
	
	public Model(Vertex3f[] Vertices, TextureCoordinate[] TexCoords, Normal[] Normals, int[] Indices)
	{
		m_resource = new ModelResource();
		m_vertices = Vertices;
		m_indices = Indices;
		m_textureCoords = TexCoords;
		m_normals = Normals;
		
		m_vertexCount = m_indices.length;
			
		m_resource.Init(m_vertices,m_textureCoords,m_normals,m_indices);
	}

	@Override
	public Resource GetResource()
	{
		return m_resource;
	}
	
	
	
	public int[] GetIndices()
	{
		return m_indices;
	}
	
	@Override
	public int GetVertexCount()
	{
		return m_vertexCount;
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
	public int GetVertexAttribArrayIndex()
	{
		return m_vertexAttribArrayIndex;
	}
	
	@Override
	public boolean ShouldRender() 
	{
		return m_render;
	}
	
	@Override()
	public void SetRender(boolean Render)
	{
		m_render = Render;
	}
	
	@Override
	public RenderType GetType()
	{
		return RenderType.MODEL;
	}
	
	public Normal[] GetNormals()
	{
		return m_normals;
	}
	
	public Vertex3f[] GetVertices()
	{
		return m_vertices;
	}
	
	public TextureCoordinate[] GetTextureCoordinates()
	{	
		return m_textureCoords;
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
