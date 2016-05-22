 package com.studios.base.engine.script;

import javax.script.ScriptEngine;

import com.studios.base.engine.core.CoreEngine;
import com.studios.base.engine.framework.graphics.Graphics;
import com.studios.base.engine.framework.scenegraph.GameObject;
import com.studios.base.engine.framework.threading.Snapshot;

public interface EngineScript 
{
	void LoadScript(ScriptEngine Engine, Class<?> Interface);
	void Start(CoreEngine Engine, GameObject Parent, Graphics Graphic);
	void Update(Snapshot CurrentGameShot);
	ScriptType GetType();
}
