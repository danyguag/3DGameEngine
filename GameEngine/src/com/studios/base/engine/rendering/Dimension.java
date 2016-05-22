package com.studios.base.engine.rendering;

public class Dimension 
{
	private int m_width;
	private int m_height;
	private boolean m_fullscreen;
	
	public Dimension()
	{
	}
	
	public Dimension(int Width, int Height, boolean Fullscreen)
	{
		m_width = Width;
		m_height = Height;
		m_fullscreen = Fullscreen;
	}
	
	public void SetWidth(int Width)
	{
		m_width = Width;
	}
	
	public void SetHeight(int Height)
	{
		m_height = Height;
	}
	
	public void Set(int Width, int Height)
	{
		SetWidth(Width);
		SetHeight(Height);
	}
	
	public void Set(boolean Fullscreen)
	{
		m_fullscreen = Fullscreen;
	}
	
	public int GetWidth()
	{
		return m_width;
	}
	
	public int GetHeight()
	{
		return m_height;
	}
	
	public boolean IsFullScreen()
	{
		return m_fullscreen;
	}
}
