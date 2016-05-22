package com.studios.base.engine.framework.components;

import com.studios.base.engine.core.CoresManager;
import com.studios.base.engine.framework.game.Game;
import com.studios.base.engine.framework.threading.Snapshot;
import com.studios.base.engine.framework.transform.TransformType;
import com.studios.base.engine.rendering.model.TexturedModel;
import com.studios.base.engine.rendering.shader.Shader;

public class Entity extends GameComponent
{
	protected String m_modelPath;
	protected String m_texturePath;
	protected int m_entityID;
	
    public Entity(String ModelPath, String TexturePath)
    {
    	super.Name = "Entity";
    	m_entityID = CoresManager.IDGen.Model.GetNextID();
    	this.m_modelPath = ModelPath;
    	
    	this.m_texturePath = TexturePath;
    }

    @Override
    public void Init()
    {
    	GetGraphics().AddTexturedModel(m_entityID, this, m_modelPath, m_texturePath);    	
    }

    @Override
    public void Render(Shader nShader)
    {
		nShader.Bind();
		nShader.SetUniform("transform", true, GetTransform3f().GetTransformationMatrix());
		nShader.SetUniform("projectionMatrix", false, 
				Game.camera.GetProjectionMatrix());
		nShader.SetUniform("viewMatrix", true, Game.camera.GetViewMatrix());
		GetTexturedModel().GetTexture().Bind(0);
    }
    
    @Override
    public void Update(Snapshot CurrentGameState)
    {
    }
    
    public TexturedModel GetTexturedModel()
    {
    	return GetGraphics().GetTexturedModelByID(m_entityID);
    }
    
    public void Destroy()
    {
    	CoresManager.IDGen.Model.AddIDToRemovedList(m_entityID);    	
    }
    
    @Override
    protected void finalize() throws Throwable 
    {
    	super.finalize();
    	Destroy();
    }

	@Override
	public TransformType GetTransformType() 
	{
		return TransformType.TRANSFROM_3F;
	}
}
