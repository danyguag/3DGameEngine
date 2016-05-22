package com.studios.base.engine.rendering.model;

import com.studios.base.engine.framework.components.GameComponent;
import com.studios.base.engine.framework.renders.BaseRenderable;
import com.studios.base.engine.framework.renders.RenderType;
import com.studios.base.engine.framework.resourceManagement.Resource;
import com.studios.base.engine.rendering.textures.Texture;

public class TexturedModel implements BaseRenderable
{
	private Model m_model;
	private Texture m_texture;

	public TexturedModel(Model nModel, Texture nTexture)
	{
		m_model = nModel;
		m_texture = nTexture;
	}
	
	public Model GetModel()
	{
		return m_model;
	}
	
	public Texture GetTexture()
	{
		return m_texture;
	}
	
	@Override
	public boolean ShouldRender() 
	{
		return m_model.ShouldRender() && m_texture.GetRender();
	}

	@Override
	public void SetRender(boolean Render) 
	{
		m_model.SetRender(Render);
		m_texture.SetBind(Render);
	}

	@Override
	public Resource GetResource() 
	{ 
		return m_model.GetResource(); 
	}

	@Override
	public int GetVertexAttribArrayIndex() 
	{ 
		return m_model.GetVertexAttribArrayIndex();
	}

	@Override
	public int GetVertexCount() 
	{ 
		return m_model.GetVertexCount(); 
	}

	@Override
	public int GetDrawMode() 
	{ 
		return m_model.GetDrawMode();
	}

	@Override
	public int GetDrawType() 
	{ 
		return m_model.GetDrawType();
	}

	@Override
	public int GetIndicesOffset() 
	{ 
		return m_model.GetIndicesOffset(); 
	}

	@Override
	public RenderType GetType() 
	{
		return RenderType.TEXTUREDMODEL;
	}
	
	@Override
	public boolean UsingParent() 
	{
		return m_model.UsingParent();
	}

	@Override
	public void SetUsingParent(boolean UsingParent) 
	{
		m_model.SetUsingParent(UsingParent);
	}

	@Override
	public GameComponent GetGameComponentParent() 
	{
		return m_model.GetGameComponentParent();
	}

	@Override
	public void SetParent(GameComponent Parent) 
	{
		m_model.SetParent(Parent);
	}
}
