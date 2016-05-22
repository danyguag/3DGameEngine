package com.studios.base.main;

import com.studios.base.engine.core.CoreEngine;
import com.studios.base.engine.core.CoresManager;
import com.studios.base.engine.rendering.Dimension;

public class Main 
{
	
	public static void main(String[] args) 
	{
		CoresManager.Init();
		
		CoreEngine GameEngine = new CoreEngine(300, new GameLogic());
		GameEngine.CreateDisplay(new Dimension(1200, 800, false), "Game");
		GameEngine.start();
	}
}
