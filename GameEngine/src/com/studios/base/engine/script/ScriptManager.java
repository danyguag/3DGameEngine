package com.studios.base.engine.script;

import javax.script.ScriptEngine;

import com.studios.base.engine.core.CoreEngine;
import com.studios.base.engine.framework.containers.util.EngineList;
import com.studios.base.engine.framework.graphics.Graphics;
import com.studios.base.engine.framework.scenegraph.GameObject;
import com.studios.base.engine.framework.threading.Snapshot;

public class ScriptManager 
{
	private ScriptEngine m_scriptEngine;
	private EngineList<EngineScript> m_scripts;
	private ScriptType m_type;
	
	public ScriptManager(ScriptType Type)
	{
		m_type = Type;
		switch (Type)
		{
		case JAVASCRIPT:
		{	
			m_scriptEngine = ScriptUtils.ScriptFactory.getEngineByName("nashorn");
		}break;
		default:
			throw new IllegalStateException("No " + Type.toString() + " Supported yet");
		}
		
		m_scripts = new EngineList<EngineScript>();
	}
	
	public void AddScript(EngineScript Script, Class<?> Interface)
	{
		Script.LoadScript(m_scriptEngine, Interface);
		m_scripts.Add(Script);
	}
	
	public void Start(CoreEngine Engine, GameObject Parent, Graphics Graphic)
	{
		for (EngineScript Script : m_scripts)
			Script.Start(Engine, Parent, Graphic);
	}
	
	public void Update(Snapshot CurrentGameShot)
	{
		for (EngineScript Script : m_scripts)
			Script.Update(CurrentGameShot);
	}
	
	public ScriptEngine GetScriptEngine()
	{
		return m_scriptEngine;
	}
	
	public ScriptType GetType()
	{
		return m_type;
	}
}
