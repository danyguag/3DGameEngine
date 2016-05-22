package com.studios.base.engine.framework.threading;

import com.studios.base.engine.core.CoreEngine;
import com.studios.base.engine.core.util.Util;
import com.studios.base.engine.framework.containers.util.EngineTree;
import com.studios.base.engine.framework.scenegraph.GameObject;

public class Snapshot 
{
	private double m_mousePositionX;
	private double m_mousePositionY;
	private CoreEngine m_coreEngine;
	private GameObject m_playerObject;
	private GameObject m_rootObject;	
	private float m_delta;
	
	public Snapshot(CoreEngine Engine)
	{
		m_coreEngine = Engine;
		m_mousePositionX = Util.GetOpenGlMouseX(Engine);
		m_mousePositionY = Util.GetOpenGlMouseY(Engine);
	}
	
	public void Init(GameObject RootGameObject, float Delta)
	{
		m_rootObject = RootGameObject;
		GameObject PlayerObject = null;
		
		for (EngineTree<GameObject> Child : RootGameObject.GetChildren().GetTree())
			if (Child.GetSelf().GetName().equals("Player"))
				if (PlayerObject == null)
					PlayerObject = Child.GetSelf();
		
		m_playerObject = PlayerObject;
		m_delta = Delta;
	}
	
	public float GetDelta()
	{
		return m_delta;
	}
	
	public CoreEngine GetCoreEngine()
	{
		return m_coreEngine;
	}
	
	public GameObject GetPlayerObject()
	{
		return m_playerObject;
	}
	
	public double GetMousePositionX()
	{
		return m_mousePositionX;
	}
	
	public double GetMousePositionY()
	{
		return m_mousePositionY;
	}

	public GameObject GetRootObject()
	{
		return m_rootObject;		
	}
}
