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
import static org.lwjgl.opengl.GL11.GL_BACK;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
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
import static org.lwjgl.opengl.GL11.glCullFace;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glViewport;

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GLContext;

import com.studios.base.engine.framework.containers.util.EngineList;
import com.studios.base.engine.framework.debugging.DebugManager;
import com.studios.base.engine.framework.state.GameStateManager;
import com.studios.base.engine.rendering.enums.ClearType;
import com.studios.base.engine.rendering.lighting.Light;
import com.studios.base.engine.rendering.shader.Shader;

public class MainFrameBufferRenderingEngine 
{
	private int m_frameBufferObject;
	private int m_frameBufferTextureObject;
	private int m_depthRenderBufferID;
	private int m_width;
	private int m_height;
	
	private Shader m_ambientShader;
	private Shader m_directionalShader;
	
	private Shader m_lineShader;
	private Shader m_uiShader;
	private Shader m_fontShader;
	
	private RenderOptions m_renderOptions;
	
	public MainFrameBufferRenderingEngine(int FBOWidth, int FBOHeight) 
	{
		if (!GLContext.getCapabilities().GL_EXT_framebuffer_object)
		{
			DebugManager.Log(getClass().getSimpleName(), "You do not have high enough opengl support for this game");
			DebugManager.ExitWithError();
		}
		
		m_frameBufferObject = glGenFramebuffersEXT();
		m_frameBufferObject = glGenTextures();
		m_depthRenderBufferID = glGenRenderbuffersEXT(); 
		
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, m_frameBufferObject);
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
		m_ambientShader = new Shader();
		m_ambientShader.AddVertexFragmentAndAttachAndInitShader("ForwardAmbientShader");
		m_directionalShader = new Shader();
		m_directionalShader.AddVertexFragmentAndAttachAndInitShader("ForwardDirectionalShader");
		m_lineShader = new Shader();
		m_lineShader.AddVertexFragmentAndAttachAndInitShader("line/LineShader");
		m_uiShader = new Shader();
		m_uiShader.AddVertexFragmentAndAttachAndInitShader("ui/UIShader");
		m_fontShader = new Shader();
		m_fontShader.AddVertexFragmentAndAttachAndInitShader("font/FontShader");
	}

	public void Init(RenderOptions nRenderOptions)
	{
		m_renderOptions = nRenderOptions;

		m_ambientShader.Bind();
		m_ambientShader.AddUniform("ambientLight");
		m_ambientShader.AddUniform("transform");
		m_ambientShader.AddUniform("projectionMatrix");
		m_ambientShader.AddUniform("viewMatrix");
		m_ambientShader.SetUniform("ambientLight", nRenderOptions.GetAmbientLight());

		
		m_directionalShader.Bind();
		m_directionalShader.AddUniform("transform");       
		m_directionalShader.AddUniform("projectionMatrix");
		m_directionalShader.AddUniform("viewMatrix");      
		m_directionalShader.AddUniform("lightDirection");
		m_directionalShader.AddUniform("lightColor");
		
		m_uiShader.Bind();
		m_uiShader.BindAttribute(0, "position");
		m_uiShader.AddUniform("transform");
		
		m_lineShader.Bind();
		m_lineShader.AddUniform("color");
		
		
		m_fontShader.Bind();
		m_fontShader.AddUniform("color");
		m_fontShader.AddUniform("transform");
		
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);

	}
	
	public int Render(GameStateManager GameState, EngineList<Light> Lights) 
	{
		Prepare(ClearType.COLOR, ClearType.DEPTH);

		int CurrentRenderPass = 0;
		int LightsLength = Lights.size();

		GameState.Render(CurrentRenderPass,
			m_ambientShader);
		++CurrentRenderPass;
		/*
		 * TODO: 1. Lighting
		 */
		
//		glEnable(GL_BLEND);
//		glBlendFunc(GL_ONE, GL_ONE);
//		glDepthMask(false);
//		glDepthFunc(GL_EQUAL);
//		
//		for (Light nLight : Lights)
//		{
//			switch (nLight.GetType())
//			{
//			case DIRECTIONAL:
//			{
//				m_directionalShader.Bind();
//				m_directionalShader.SetUniform("lightDirection", nLight.GetPosition());
//				m_directionalShader.SetUniform("lightColor", nLight.GetColor());
//			}break;
//			case POINT:
//			{
//				
//			}break;
//			case SPOT:
//			{
//				
//			}break;
//			default:
//				DebugManager.PrintException(new IllegalStateException("Should never reach here, RENDERINGENGINE.JAVA[RENDER()]"));
//			}
//			GameState.Render(++RenderPass, m_directionalShader, null, null, null);
//		}
//		
//		glDepthFunc(GL_LESS);
//		glDepthMask(true);
//		glDisable(GL_BLEND);
		GameState.Render2D(m_renderOptions.GetLineShader(), m_renderOptions.GetUIShader(), m_renderOptions.GetFontshader());
		
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
		
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

	public void CleanUp() 
	{
		m_ambientShader.CleanUp();
		m_lineShader.CleanUp();
		m_directionalShader.CleanUp();
		m_fontShader.CleanUp();
	}
	
	public Shader GetDefaultUIShader()
	{
		return m_uiShader;
	}
	
	public int GetBufferTextureID()
	{
		return m_frameBufferTextureObject;
	}
	
	public Shader GetDefaultLineShader()
	{
		return m_lineShader;
	}
	
	public Shader GetDefaultFontShader()
	{
		return m_fontShader;
	}
	
	public int GetBufferHeight()
	{
		return m_height;
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
