package com.studios.base.engine.framework.ui;

import com.studios.base.engine.core.util.Util;
import com.studios.base.engine.framework.components.UIComponent;
import com.studios.base.engine.framework.math.Scale2f;
import com.studios.base.engine.framework.math.Vector2f;
import com.studios.base.engine.framework.threading.Snapshot;

public class ButtonComponent extends UIComponent
{
	private double m_minX = 0;
	private double m_minY = 0;
	private double m_maxX = 0;
	private double m_maxY = 0;
	
	private double m_textureHalfWidth;
	private double m_textureHalfHeight;
	
	public ButtonComponent(String TexturePath, Vector2f Position, Scale2f Scale)
	{
		super(TexturePath, Position, Scale);

	}

	@Override
	public void Init()
	{
		super.Init();
		
		Vector2f PixelScale = new Vector2f(GetCoreEngine().GetWindowEngine().GetWindow().GetWidth() * m_scale.GetX(), GetCoreEngine().GetWindowEngine().GetWindow().GetHeight() * m_scale.GetY());
		
		float PixelScaledWidth = GetUITexture().GetTexture().GetWidth() - PixelScale.GetX();
		float PixelScaledHeight = GetUITexture().GetTexture().GetHeight() - PixelScale.GetY();
		
		m_textureHalfWidth 	= Util.OpenglFloatX(GetCoreEngine(), PixelScaledWidth / 2);
		m_textureHalfHeight = Util.OpenglFloatY(GetCoreEngine(), PixelScaledHeight / 2);
		m_minX = (m_position.GetX() - m_textureHalfWidth);
		m_minY = (m_position.GetY() - m_textureHalfHeight); 
	}
	
	@Override
	public void Update(Snapshot CurrentGameShot)
	{
		if (!IsSelected(CurrentGameShot.GetMousePositionX(), CurrentGameShot.GetMousePositionY()))
		{
			m_scale.Set(m_orginalScale);                             
		}

		if (IsSelected(CurrentGameShot.GetMousePositionX(), CurrentGameShot.GetMousePositionY()))
		{
			m_scale.Set(m_orginalScale.GetX() + (m_orginalScale.GetX() / ((m_scale.GetX() <= .25f) ? 10 : 100)), m_orginalScale.GetY() + (m_orginalScale.GetY() / ((m_scale.GetY() <= .25f) ? 10 : 100)));
		}
	}
	
	public boolean IsSelected(double PositionX, double PositionY)
	{
		if (PositionX >= m_minX && PositionY >= m_minY && PositionX <= m_maxX && PositionY <= m_maxY)
		{			
			return true;
		}
		return false;
	}
	
	public boolean IsClicked(Snapshot CurrentGameShot)
	{
		if (!IsSelected(CurrentGameShot.GetMousePositionX(), CurrentGameShot.GetMousePositionY()))
			return false;
		
		if (IsSelected(CurrentGameShot.GetMousePositionX(), CurrentGameShot.GetMousePositionY()))
		{
//			if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1))
//			{
//				return true;
//			}
			/*
			 * TODO: LWJGL3 
			 */
		}
		
		return false;
	}
}
