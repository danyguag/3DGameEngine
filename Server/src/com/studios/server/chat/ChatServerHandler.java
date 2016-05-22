package com.studios.server.chat;

import java.util.HashMap;

import com.studios.base.engine.framework.debugging.DebugManager;
import com.studios.base.engine.net.Connection;
import com.studios.base.engine.net.Listener;
import com.studios.base.engine.net.messaging.Packet;

import main.Main;

public class ChatServerHandler extends Listener
{
	public static HashMap<String, Connection> Clients = new HashMap<String, Connection>();
	public static HashMap<String, Packet> Packets = new HashMap<String, Packet>();
	
	@Override
	public void Connected(Connection connection) 
	{
		super.Connected(connection);
	}

	@Override
	public void Disconnected(Connection connection) 
	{
		super.Disconnected(connection);
	}
	
	@Override
	public void Idle(Connection connection)
	{
		super.Idle(connection);
	}
	
	@Override
	public void MessageReceived(Connection nConnection, Object object) 
	{
		DebugManager.Log(getClass().getSimpleName(), ((Packet)object).GetPacketName());
		
		if (object instanceof Packet)
		{
			Packet Data = (Packet) object;
			Packets.put(Data.GetPacketName(), Data);
			
			if (Data.GetPacketName().equals("connection"))
			{
				if (!Clients.containsValue(nConnection))
				{
					String Username = (String) Data.GetEntry("player_name");
					OnChannelConnect(nConnection, Username);
					Clients.put(Username, nConnection);
				}
			}
			else if (Data.GetPacketName().equals("disconnection"))
			{
				if (Clients.containsValue(nConnection))
				{
					Clients.remove((String) Data.GetEntry("player_name"), nConnection);
					Disconnected(nConnection);
				}
			}
			else if (Data.GetPacketName().equals("chat_message"))
			{
				OnChatMessageRecieved(nConnection, Data);
			}
			
//			System.out.println("Data received: " + Data.GetPacketName() + " from: " + nConnection.getRemoteAddressTCP());
		}
	}

	public void OnChannelConnect(Connection Peer, String Username) 
	{ 
		if (Clients.size() > 0)
		{
			for (Connection OtherPeer : Clients.values())
			{
				Packet packet = new Packet("player_connect", 1);
				packet.AddEntry("new_player_name", Username);
				packet.AddEntry("chat_message", "Has connected!");
				OtherPeer.Write(packet);
			}
		}
	}
	
	private void OnChatMessageRecieved(Connection PeerThatSent, Packet Packet)
	{
		DebugManager.Log(getClass().getSimpleName(), "You have received a chat packet");
		Main.ChatServer.GetServer().SendToAll(Packet);
	}
}
