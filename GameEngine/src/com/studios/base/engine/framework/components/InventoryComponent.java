package com.studios.base.engine.framework.components;

import org.lwjgl.input.Keyboard;

import com.studios.base.engine.framework.math.Scale2f;
import com.studios.base.engine.framework.math.Vector2f;
import com.studios.base.engine.framework.threading.Snapshot;

public class InventoryComponent extends UIComponent
{
	private boolean m_isOpen;
	private boolean m_closable;
	
	public InventoryComponent(String TexturePath, Vector2f Position, Scale2f Scale) 
	{
		super(TexturePath, Position, Scale);
		m_isOpen = true;
		m_closable = false;
	}

	@Override
	public void Update(Snapshot CurrentGameShot) 
	{
		
		if (Keyboard.isKeyDown(Keyboard.KEY_I))
		{
			m_isOpen = true;
			GetUITexture().SetRender(true);
		}
		
		if (m_isOpen)
		{
			if (!Keyboard.isKeyDown(Keyboard.KEY_I))
			{
				m_closable = true;
				try 
				{
					Thread.sleep(100);
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		if (m_closable)
		{
			if (Keyboard.isKeyDown(Keyboard.KEY_I))
			{
				GetUITexture().SetRender(false);
				m_isOpen = false;
			}
		}
	}
}
