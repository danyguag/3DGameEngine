package com.studios.base.engine.core;

import com.studios.base.engine.framework.chat.ChatClientHandler;
import com.studios.base.engine.framework.components.JSON;
import com.studios.base.engine.framework.containers.util.EngineList;
import com.studios.base.engine.framework.scenegraph.GameObject;
import com.studios.base.engine.net.client.ClientBootstrap;
import com.studios.base.engine.rendering.RenderOptions;
import com.studios.base.engine.rendering.lighting.Light;
import com.studios.base.engine.script.EngineScript;

public abstract class LogicCore 
{
	private EngineList<Light> m_lights;
	protected CoreEngine m_coreEngine;
	public String CurrentGameStateName;

	public volatile ClientBootstrap ChatClient;
	public volatile ChatClientHandler ChatHandler;
	
	public void AddLight(Light nLight)
	{
		GetLights().Add(nLight);
	}
	
	public void AddChildToCurrentState(GameObject Child)
	{
		m_coreEngine.GetGameStateManager().GetCurrentGameState().AddGameObject(Child);
	}

	public void AddJSON(JSON File)
	{
		m_coreEngine.GetGameStateManager().GetCurrentGameState().GetStateGameObject().AddJSON(File);
	}
	
	public void SetCoreEngine(CoreEngine nCoreEngine)
	{
		m_coreEngine = nCoreEngine;
	}

	public void AddScript(EngineScript Script, Class<?> Interface)
	{
		m_coreEngine.GetGameStateManager().GetCurrentGameState().GetStateGameObject().AddScript(Script, Interface);
	}
	
	public EngineList<Light> GetLights()
	{
		if (m_lights == null)
			m_lights = new EngineList<Light>();
		return m_lights;
	}
	
	public abstract void Init();
	public abstract void CleanUp();
	
	public abstract RenderOptions GetOptions();
}

