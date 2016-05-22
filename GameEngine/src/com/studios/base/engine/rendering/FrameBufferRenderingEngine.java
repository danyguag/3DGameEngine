package com.studios.base.engine.rendering;

import static org.lwjgl.opengl.EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_RENDERBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindFramebufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindRenderbufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glCheckFramebufferStatusEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glFramebufferRenderbufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glFramebufferTexture2DEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glGenFramebuffersEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glGenRenderbuffersEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glRenderbufferStorageEXT;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glViewport;

import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GLContext;

import com.studios.base.engine.framework.debugging.DebugManager;
import com.studios.base.engine.rendering.enums.ClearType;

public class FrameBufferRenderingEngine 
{
	private int m_frameBufferObject;
	private int m_frameBufferTextureObject;
	private int m_depthRenderBufferID;
	private int m_width;
	private int m_height;	
	
	private FrameBufferRenderer m_rendering;
	
	public FrameBufferRenderingEngine(int FBOWidth, int FBOHeight, FrameBufferRenderer Rendering) 
	{
		if (!GLContext.getCapabilities().GL_EXT_framebuffer_object)
		{
			DebugManager.Log(getClass().getSimpleName(), "You do not have high enough opengl support for this game");
			DebugManager.ExitWithError();
		}
		
		m_frameBufferObject = glGenFramebuffersEXT();
		m_frameBufferObject = glGenTextures();
		m_depthRenderBufferID = glGenRenderbuffersEXT(); 
		
		Bind();
		glViewport(0, 0, FBOWidth, FBOHeight);
		
		glBindTexture(GL_TEXTURE_2D, m_frameBufferObject);                                   
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);               
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 512, 512, 0,GL_RGBA, GL_INT, (java.nio.ByteBuffer) null);  
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT,GL_COLOR_ATTACHMENT0_EXT,GL_TEXTURE_2D, m_frameBufferObject, 0); 
	     
		glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, m_depthRenderBufferID);                
		glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL14.GL_DEPTH_COMPONENT24, 512, 512); 
		glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT,GL_DEPTH_ATTACHMENT_EXT,GL_RENDERBUFFER_EXT, m_depthRenderBufferID); 
        
		m_width = FBOWidth;
		m_height = FBOHeight;
		
		glClearColor(0, 0, 0, 0);
		m_rendering = Rendering;
	}

	public void Init()
	{
		m_rendering.Init();
	}
	
	public int Render() 
	{
		Bind();
		Prepare(m_rendering.GetClearTypes());
		
		m_rendering.Render();
		
		UnBind();
		
		return m_frameBufferTextureObject;
	}

	private void Prepare(ClearType... Types) 
	{
		int[] Clears = new int[Types.length];
		
		int ClearPosition = 0;
		for (ClearType Type : Types)
		{
			if (Type == ClearType.COLOR)
				Clears[ClearPosition] = GL_COLOR_BUFFER_BIT;
			else if (Type == ClearType.DEPTH)
				Clears[ClearPosition] = GL_DEPTH_BUFFER_BIT;
			else if (Type == ClearType.STENCIL)
				Clears[ClearPosition] = GL_STENCIL_BUFFER_BIT;
			++ClearPosition;
		}
		
		if (ClearPosition == 1)
			glClear(Clears[0]);
		else if (ClearPosition == 2)
			glClear(Clears[0] | Clears[1]);
		else if (ClearPosition == 3)
			glClear(Clears[0] | Clears[1] | Clears[2]);
	}
	
	public int GetBufferTextureID()
	{
		return m_frameBufferTextureObject;
	}
	
	public int GetBufferHeight()
	{
		return m_height;
	}
	
	public void Bind()
	{
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, m_frameBufferObject);
	}
	
	public void UnBind()
	{
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}
	
	public FrameBufferRenderer GetRendering()
	{
		return m_rendering;
	}
	
	public int GetBufferWidth()
	{
		return m_width;
	}
	
	public void CheckFrameCurrentFrameBuffer()
	{
		int Framebuffer = glCheckFramebufferStatusEXT(GL_FRAMEBUFFER_EXT); 
		switch (Framebuffer) 
		{
		    case GL_FRAMEBUFFER_COMPLETE_EXT:
		        break;
		    case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
		        throw new RuntimeException("FrameBuffer: " + m_frameBufferObject 
		        		+ ", has caused a GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT exception");
		    case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
		        throw new RuntimeException("FrameBuffer: " + m_frameBufferObject
		                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT exception");
		    case GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT:
		        throw new RuntimeException("FrameBuffer: " + m_frameBufferObject
		                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_EXT exception");
		    case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
		        throw new RuntimeException("FrameBuffer: " + m_frameBufferObject
		                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT exception");
		    case GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
		        throw new RuntimeException("FrameBuffer: " + m_frameBufferObject
		                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT exception");
		    case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
		        throw new RuntimeException("FrameBuffer: " + m_frameBufferObject
		                + ", has caused a GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT exception");
		    default:
		        throw new RuntimeException("Unexpected reply from glCheckFramebufferStatusEXT: " + m_frameBufferObject);
		}

	}
}
