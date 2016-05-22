package com.studios.base.engine.net.client;

import java.io.IOException;
import java.net.InetAddress;

import com.studios.base.engine.framework.debugging.DebugManager;
import com.studios.base.engine.net.Client;
import com.studios.base.engine.net.Listener;
import com.studios.base.engine.net.NetworkRegister;
import com.studios.base.engine.net.messaging.Packet;

public class ClientBootstrap 
{
	private Client m_client;
	private boolean m_handlerAdded;
	
	public ClientBootstrap(int TimeOut, String IpAddress, int UDPPort)
	{
		m_handlerAdded = false;
		m_client = new Client();
		m_client.Start();
		NetworkRegister.Register(m_client);
		try
		{
			m_client.Connect(TimeOut, InetAddress.getByName(IpAddress), UDPPort);
		}
		catch (IOException e) 
		{
			DebugManager.PrintException(e);
		}
	}
	
	public void Write(Packet nPacket)
	{
		if (!m_handlerAdded)
			DebugManager.Log(getClass().getSimpleName(), "WARNING HANDLER NOT ADDED YET");
		m_client.Write(nPacket);
	}
	
	public ClientBootstrap Handler(Listener PacketListener)
	{
		m_client.AddListener(PacketListener);
		m_handlerAdded = true;
		return this;
	}
	
	public Client GetClient()
	{
		return m_client;
	}
}
