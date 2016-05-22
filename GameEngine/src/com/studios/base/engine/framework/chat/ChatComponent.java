package com.studios.base.engine.framework.chat;

import com.studios.base.engine.framework.components.TextComponent;
import com.studios.base.engine.framework.font.Font;

public class ChatComponent extends TextComponent
{
	private String m_username;
	
	public ChatComponent(String Username, Font nFont, float MaxLineLength) 
	{
		super(nFont, MaxLineLength, false);
		m_username = Username;
	}
	
	public String GetUsername()
	{
		return m_username;
	}
	
	public void SetRender(boolean Render)
	{
		m_render = Render;
	}
}
