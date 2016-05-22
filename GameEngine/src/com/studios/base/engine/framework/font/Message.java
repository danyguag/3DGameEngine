package com.studios.base.engine.framework.font;

import com.studios.base.engine.framework.math.Vector2f;
import com.studios.base.engine.framework.math.Vector3f;

public class Message 
{
	private String m_message;
	private Vector2f m_position;
	private Vector3f m_color;
	private float m_fontSize;
	
	public Message(String Message, Vector2f Position, Vector3f Color, float FontSize)
	{
		m_message = Message;
		m_position = Position;
		m_color = Color;
		m_fontSize = FontSize;
	}
	
	public float GetFontSize()
	{
		return m_fontSize;
	}
	
	public Vector2f GetPosition()
	{
		return m_position;
	}
	
	public Vector3f GetColor()
	{
		return m_color;
	}
	
	public String GetMessage()
	{
		return m_message;
	}
}
