package com.studios.base.engine.framework.state;

import com.studios.base.engine.core.CoreEngine;
import com.studios.base.engine.framework.containers.util.EngineList;
import com.studios.base.engine.framework.threading.Snapshot;
import com.studios.base.engine.rendering.MainFrameBufferRenderingEngine;
import com.studios.base.engine.rendering.shader.Shader;

public class MainGameState 
{
	private EngineList<GameState> m_children;

	public MainGameState()
	{
		m_children = new EngineList<GameState>();
	}
	
	public void Init(MainFrameBufferRenderingEngine nRenderingEngine)
	{
		for (GameState Child : m_children)
			Child.Init(nRenderingEngine);
	}
	
	public void Render3D(int CurrentRenderPass, Shader ForwardRenderShader)
	{
		for (GameState Child : m_children)
			Child.Render3D(CurrentRenderPass, ForwardRenderShader);
	}
	
	public void Render2D(Shader LineShader, Shader UIShader, Shader FontShader)
	{
		for (GameState Child : m_children)
			Child.Render2D(LineShader, UIShader, FontShader);
	}
	
	public void Update(Snapshot CurrentGameShot)
	{
		for (GameState Child : m_children)
			Child.Update(CurrentGameShot);
	}
	
	public void SetCoreEngine(CoreEngine nCoreEngine)
	{		
		for (GameState Child : m_children)
			Child.SetCoreEngine(nCoreEngine);
	}
	
	public void AddChildGameState(GameState Child)
	{
		m_children.Add(Child);
	}
	
	public EngineList<GameState> GetChildrenGameState()
	{
		return m_children;
	}
}
