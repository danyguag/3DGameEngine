package com.studios.base.engine.framework.components;

import org.lwjgl.input.Keyboard;

import com.studios.base.engine.core.CoresManager;
import com.studios.base.engine.framework.game.Game;
import com.studios.base.engine.framework.math.Scale2f;
import com.studios.base.engine.framework.math.Vector2f;
import com.studios.base.engine.framework.threading.Snapshot;
import com.studios.base.engine.framework.transform.TransformType;
import com.studios.base.engine.rendering.model.UITexture;
import com.studios.base.engine.rendering.shader.Shader;

public class UIComponent extends GameComponent
{
	private String m_texturePath;
	protected final int m_uiID;
	
	protected Vector2f m_position;
	protected Scale2f m_scale;
	
	protected final Vector2f m_orginalPosition;
	protected final Vector2f m_orginalScale;
	
	public UIComponent(String TexturePath, Vector2f Position, Scale2f Scale)
	{
		super.Name = "UIComponent";
		m_texturePath = TexturePath;
		m_uiID = CoresManager.IDGen.UI.GetNextID();
		m_position = Position;
		m_scale = Scale;
		m_orginalPosition = Position;
		m_orginalScale = new Vector2f(Scale.GetX(), Scale.GetY());
	}
	
	@Override
	public void Init() 
	{
		UITexture uiTex = GetGraphics().AddUI(m_uiID,this, m_texturePath, m_position, m_scale);
		GetTransform2f().SetPosition(uiTex.GetPosition());
		GetTransform2f().SetScale(uiTex.GetScale());
	}
	
	@Override
	public void Render(Shader UIShader)
	{
		UIShader.Bind();
		UIShader.SetUniform("transform", true, GetTransform2f().GetTransformationMatrix());
		GetUITexture().GetTexture().Bind(0);
	}
	boolean StaysOpen = false;
	@Override
	public void Update(Snapshot CurrentGameShot) 
	{
		if (Game.TEMP_BOOL_DEBUG)
		{
			if (StaysOpen)
			{
				if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) && Keyboard.isKeyDown(Keyboard.KEY_I))
				{
					StaysOpen = false;
				}
			}
			
			if (!StaysOpen && !Keyboard.isKeyDown(Keyboard.KEY_I))
			{
				GetGraphics().GetUITextureByID(m_uiID).SetRender(false);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_I))
			{
				GetGraphics().GetUITextureByID(m_uiID).SetRender(true);
			}
			
			if (Keyboard.isKeyDown(Keyboard.KEY_INSERT) && Keyboard.isKeyDown(Keyboard.KEY_I))
			{
				StaysOpen = true;
			}
			
			if (StaysOpen)
			{
				GetGraphics().GetUITextureByID(m_uiID).SetRender(true);
			}
		}
	}
	
	public Vector2f GetPosition()
	{
		return m_position;
	}
	
	public Vector2f GetScale()
	{
		return m_scale;
	}
	
	public UITexture GetUITexture()
	{
		return GetGraphics().GetUITextureByID(m_uiID);
	}
	
	public void Destroy()
	{
		CoresManager.IDGen.UI.AddIDToRemovedList(m_uiID);
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
		return TransformType.TRANSFROM_2F;
	}
}
