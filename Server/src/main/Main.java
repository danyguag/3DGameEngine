package main;

import com.studios.base.engine.core.CoresManager;
import com.studios.base.engine.net.server.ServerBootstrap;
import com.studios.server.chat.ChatServerHandler;

public class Main 
{
	public static ServerBootstrap ChatServer;
	
	public static void main(String[] args) 
	{
		CoresManager.InitLogger();
		
//		DBUtils.InitDatabases();
//		
//		Database PlayerBase = new Database("game_server_test");
//		PlayerBase.Connect();


		ChatServer = new ServerBootstrap("192.168.0.3", 40505).Handler(new ChatServerHandler());
	}
}
