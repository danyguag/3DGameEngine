package com.studios.base.engine.framework.components;

import com.studios.base.engine.framework.game.Game;
import com.studios.base.engine.framework.math.Vector2f;
import com.studios.base.engine.framework.math.Vector3f;
import com.studios.base.engine.framework.threading.Snapshot;
import com.studios.base.engine.framework.transform.TransformType;
import com.studios.base.engine.rendering.shader.Shader;

public class TerrainComponent extends GameComponent
{
	private String m_textureName;
	
	private float X, Y;
	
	public TerrainComponent(String TexturePath, float X, float Y)
	{
		m_textureName = TexturePath;
		this.X = X;
		this.Y = Y;
	}
	
	@Override
	public void Init() 
	{
		GetGraphics().AddTexturedModel(5, this, GetGraphics().AddTerrain(5, this, new Vector2f(X, Y)), m_textureName);
		
		GetTransform3f().SetPosition(new Vector3f(X, 0, Y));
	}

	@Override
	public void Render(Shader nShader) 
	{
		nShader.Bind();
		nShader.SetUniform("transform", true, GetTransform3f().GetTransformationMatrix());
		nShader.SetUniform("projectionMatrix", false, 
				Game.camera.GetProjectionMatrix());
		nShader.SetUniform("viewMatrix", true, Game.camera.GetViewMatrix());
		GetGraphics().GetTexturedModelByID(5).GetTexture().Bind(0);
	}

	@Override
	public void Update(Snapshot CurrentGameState) 
	{
	}

	@Override
	public TransformType GetTransformType() 
	{
		return TransformType.TRANSFROM_3F;
	}
	
}
