package com.studios.base.engine.net.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.studios.base.engine.net.Listener;
import com.studios.base.engine.net.NetworkRegister;
import com.studios.base.engine.net.Server;

public class ServerBootstrap 
{
	private Server m_server;
	
	public ServerBootstrap(String BindPoint, int UDP)
	{
		m_server = new Server();
		m_server.Start();
		NetworkRegister.Register(m_server);
		try 
		{
			m_server.Bind(new InetSocketAddress(BindPoint, UDP));
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public ServerBootstrap Handler(Listener PacketListener)
	{
		m_server.AddListener(PacketListener);
		return this;
	}
	
	public Server GetServer()
	{
		return m_server;
	}
}
