package com.studios.base.engine.rendering;

import static org.lwjgl.opengl.GL11.glViewport;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;

public class Window 
{
	private Dimension m_dim;
	
	public Window(Dimension WindowDim, String Title) 
	{ 
		m_dim = WindowDim;

		Display.setTitle(Title);
		
		try 
		{
			Display.setDisplayMode(new DisplayMode(WindowDim.GetWidth(), WindowDim.GetHeight()));
		}
		catch (LWJGLException e) 
		{
			e.printStackTrace();
		}
		
		
	}

	public void Create(PixelFormat nPixelFormat, int OPENGL_MAX_VERSION, int OPENGL_MIN_VERSION)
	{
		try 
		{
			Display.create(nPixelFormat, new ContextAttribs(OPENGL_MAX_VERSION, OPENGL_MIN_VERSION).withForwardCompatible(true).withProfileCore(true));
		}
		catch (LWJGLException e) 
		{
			e.printStackTrace();
		}
		System.out.println(GLContext.getCapabilities().GL_NV_vertex_program);
		
        glViewport(0, 0, m_dim.GetWidth(), m_dim.GetHeight());
	}
	
	public void RenderWindow() 
	{
		Display.update();
	}
	
	public void DestroyWindow() 
	{
		Display.destroy();
	}

	public int GetWidth() 
	{
		return m_dim.GetWidth();
	}

	public int GetHeight() 
	{
		return m_dim.GetHeight();
	}
}
