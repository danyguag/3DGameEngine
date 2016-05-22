package com.studios.base.engine.rendering;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.PixelFormat;

import com.studios.base.engine.framework.containers.util.EngineList;
import com.studios.base.engine.framework.state.GameStateManager;
import com.studios.base.engine.rendering.lighting.Light;

public class WindowEngine 
{
	private Window m_window;
	private EngineList<FrameBufferRenderingEngine> m_frameBufferObjects;
	private MainFrameBufferRenderingEngine m_mainRenderingEngine;
	
	private PostRenderEffectEngine m_postRenderEngine;
	
	public WindowEngine(Dimension Window, String Title)
	{
		m_window = new Window(Window, Title);
		m_frameBufferObjects = new EngineList<FrameBufferRenderingEngine>();
		m_postRenderEngine = new PostRenderEffectEngine();
	}
	
	public void InitMainRenderingEngine(MainFrameBufferRenderingEngine MainRenderingEngine)
	{
		m_mainRenderingEngine = MainRenderingEngine;
	}
	
	public void Create()
	{
		m_window.Create(new PixelFormat(), 3, 2);
	}
	
	public void Render(GameStateManager State, EngineList<Light> Lights)
	{
//		if (!DebugManager.IsCurrent())
//			DebugManager.MakeCurrentContext();
		
		EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 0);
		
		
		
		glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, m_postRenderEngine.Render(m_mainRenderingEngine, m_frameBufferObjects, State, Lights));

		
		m_window.RenderWindow();
	}
	
	public void Destroy()
	{
		m_window.DestroyWindow();
	}

	public void CleanUp()
	{
		for (FrameBufferRenderingEngine RenderingEngine : m_frameBufferObjects)
		{
			RenderingEngine.GetRendering().CleanUp();
		}
		m_mainRenderingEngine.CleanUp();
	}
	
	public void AddFrameBuffer(FrameBufferRenderingEngine FrameBuffer)
	{
		m_frameBufferObjects.Add(FrameBuffer);
	}
	
	public void RemoveFrameBuffer(FrameBufferRenderingEngine FrameBuffer)
	{
		m_frameBufferObjects.Remove(FrameBuffer);
	}
	
	public EngineList<FrameBufferRenderingEngine> GetFrameBuffers()
	{
		return m_frameBufferObjects;
	}
	
	public Window GetWindow()
	{
		return m_window;
	}
}
