package com.studios.base.engine.rendering.model;

import org.lwjgl.opengl.GL11;

import com.studios.base.engine.framework.components.GameComponent;
import com.studios.base.engine.framework.math.Scale2f;
import com.studios.base.engine.framework.math.Vector2f;
import com.studios.base.engine.framework.renders.BaseRenderable;
import com.studios.base.engine.framework.renders.RenderType;
import com.studios.base.engine.framework.resourceManagement.Resource;
import com.studios.base.engine.rendering.textures.Texture;

public class UITexture implements BaseRenderable
{
	private Texture m_texture;

	private Vector2f m_position;
	private Scale2f m_scale;
	
	private boolean m_render = true;
	private boolean m_usingParent = true;
	
	private GameComponent m_parent;
	
	public UITexture(String TexturePath, Vector2f Position, Scale2f Scale)
	{
		m_texture = new Texture(TexturePath);
		
		m_position = Position;
		m_scale = Scale;
	}

	public UITexture(Texture nTexture, Vector2f Position, Scale2f Scale)
	{
		m_texture = nTexture;
		
		m_position = Position;
		m_scale = Scale;
	}

	
	public Texture GetTexture()
	{
		return m_texture;
	}
	
	public Vector2f GetPosition()
	{
		return m_position;
	}
	
	public void SetPosition(float x, float y)
	{
		m_position.Set(x, y);
	}
	
	public Scale2f GetScale()
	{
		return m_scale;
	}

	@Override
	public boolean ShouldRender() 
	{
		return m_render;
	}

	@Override
	public void SetRender(boolean Render) 
	{
		m_render = Render;	
	}

	@Override
	public Resource GetResource() 
	{
		return null;
	}

	@Override
	public int GetVertexAttribArrayIndex() 
	{
		return 1;
	}

	@Override
	public int GetVertexCount() 
	{
		return 4;
	}

	@Override
	public int GetDrawMode() 
	{
		return GL11.GL_TRIANGLE_STRIP;
	}

	@Override
	public int GetDrawType() 
	{
		return GL11.GL_UNSIGNED_INT;
	}

	@Override
	public int GetIndicesOffset() 
	{
		return 0;
	}

	@Override
	public RenderType GetType() 
	{
		return RenderType.UI;
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
