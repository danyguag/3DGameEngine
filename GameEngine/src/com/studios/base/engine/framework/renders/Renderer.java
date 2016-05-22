package com.studios.base.engine.framework.renders;

import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.util.Collection;

import org.lwjgl.opengl.GL11;

import com.studios.base.engine.framework.containers.util.EngineMap;
import com.studios.base.engine.rendering.shader.Shader;

public class Renderer<T extends BaseRenderable> 
{
	private EngineMap<Integer, T> m_renderables;
	private RenderType m_renderType;
	
	public Renderer()
	{
		m_renderables = new EngineMap<Integer, T>();
	}
	
	public void Render(Shader nShader)
	{
		Collection<T> Renderables = m_renderables.values();
		
		for (T Renderable : Renderables)
		{
			if (Renderable.ShouldRender())
			{
				if (Renderable.GetResource().GetVaoID() == 0)
					Renderable.GetResource().SetVaoID();
				glBindVertexArray(Renderable.GetResource().GetVaoID());
				Renderable.GetGameComponentParent().Render(nShader);
				
				GL11.glDrawElements(Renderable.GetDrawMode(), Renderable.GetVertexCount(), Renderable.GetDrawType(), Renderable.GetIndicesOffset());
				
				if (m_renderType == RenderType.LINE)
					GL11.glDrawArrays(Renderable.GetDrawMode(), 0, Renderable.GetVertexCount());
			}
		}
	}
	
	public EngineMap<Integer, T> GetRenderables()
	{
		return m_renderables;
	}

	public void AddRenderable(Integer Index, T Renderable) 
	{
		m_renderType = Renderable.GetType();
		m_renderables.Put(Index, Renderable);
	}
}
