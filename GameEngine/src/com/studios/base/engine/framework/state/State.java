package com.studios.base.engine.framework.state;

public abstract class State 
{
	public boolean Initialized = false;
	
	public abstract void Init(State LastState);
	public abstract void Update();
	public abstract void Destroy();
	
	public abstract String GetName();
}