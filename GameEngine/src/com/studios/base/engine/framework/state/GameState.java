package com.studios.base.engine.framework.state;

import com.studios.base.engine.core.CoreEngine;
import com.studios.base.engine.framework.scenegraph.GameObject;
import com.studios.base.engine.framework.threading.Snapshot;
import com.studios.base.engine.rendering.MainFrameBufferRenderingEngine;
import com.studios.base.engine.rendering.shader.Shader;

public class GameState 
{
	private GameObject m_stateObject;
	
	public GameState(String StateName)
	{
		m_stateObject = new GameObject(StateName + " GameObject");
	}

	public void Init(MainFrameBufferRenderingEngine nRenderingEngine)
	{
		m_stateObject.InitAll(nRenderingEngine);
	}
	
	public void Render3D(int CurrentRenderPass, Shader ForwardRenderShader)
	{
		m_stateObject.RenderAll3D(CurrentRenderPass, ForwardRenderShader);
	}
	
	public void Render2D(Shader LineShader, Shader UIShader, Shader FontShader)
	{
		m_stateObject.RenderAll2D(LineShader, UIShader, FontShader);
	}
	
	public void Update(Snapshot CurrentGameShot)
	{
		m_stateObject.UpdateAll(CurrentGameShot);
	}
	
	public void AddGameObject(GameObject Child)
	{
		m_stateObject.AddChild(Child);
	}
	
	public GameObject GetStateGameObject()
	{
		return m_stateObject;
	}
	
	public void SetCoreEngine(CoreEngine nCoreEngine)
	{
		m_stateObject.SetCoreEngine(nCoreEngine);
	}
}
