package com.studios.base.engine.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import com.studios.base.engine.framework.assetloading.AssetLoader;
import com.studios.base.engine.framework.id.IDHandler;
import com.studios.base.engine.net.NetworkRegister;
import com.studios.base.engine.physics.PhysicsEngine;

public class CoresManager 
{
	public volatile static PrintStream LoggerStream;
	public volatile static AssetLoader Asset;
	
	public volatile static PhysicsEngine Physics;
	
	public volatile static IDHandler IDGen;
	
	public static volatile boolean GameRunning = true;

	public static volatile NetworkRegister Networking;

	public static void InitLogger()
	{
		try 
		{
			LoggerStream = new PrintStream(new File("./data/Log.log"));
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void Init()
	{
//		Networking = new NetworkingMaster();
//		Networking.start();
		
		IDGen = new IDHandler();
		
		Physics = new PhysicsEngine();
//		Physics.start();
				
		Asset = new AssetLoader();
	}
}
