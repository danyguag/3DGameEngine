package com.studios.base.engine.script;

public interface EngineScriptMethods 
{
	void Start(com.studios.base.engine.core.CoreEngine Engine,
			com.studios.base.engine.framework.scenegraph.GameObject Parent, com.studios.base.engine.framework.graphics.Graphics Graphic);
	void Update(com.studios.base.engine.framework.threading.Snapshot nSnapshot);
}
