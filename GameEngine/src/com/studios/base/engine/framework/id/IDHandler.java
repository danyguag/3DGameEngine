package com.studios.base.engine.framework.id;

public class IDHandler
{
	public LineIDGen Line;
	public TexturedModelIDGen Model;
	public UIModelIDGen UI;
	public DebugLineIDGen DebugLine;
	public TextureIDGen Texture;
	public EngineThreadIDGen Thread;
	
	public IDHandler()
	{
		Line  		= new LineIDGen();   
		Model 		= new TexturedModelIDGen();  
		UI    		= new UIModelIDGen();
		DebugLine 	= new DebugLineIDGen();
		Texture		= new TextureIDGen();
		Thread		= new EngineThreadIDGen();
	}
}
