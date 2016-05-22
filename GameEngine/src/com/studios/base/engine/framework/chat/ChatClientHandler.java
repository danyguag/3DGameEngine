package com.studios.base.engine.framework.chat;

import com.studios.base.engine.framework.containers.util.EngineList;
import com.studios.base.engine.framework.debugging.DebugManager;
import com.studios.base.engine.framework.font.Message;
import com.studios.base.engine.framework.math.Vector3f;
import com.studios.base.engine.net.Connection;
import com.studios.base.engine.net.Listener;
import com.studios.base.engine.net.messaging.Packet;

public class ChatClientHandler extends Listener
{
	private EngineList<Message> m_messages;
	public boolean HashChanged = false;;
	
	public ChatClientHandler()
	{
		m_messages = new EngineList<Message>();
	}
	
	@Override
	public void Connected(Connection connection) 
	{
		Packet ConnectionPacket = new Packet("connection", 1);
		ConnectionPacket.AddEntry("player_name", "danyguag");
		connection.Write(ConnectionPacket);
	}

	@Override
	public void Disconnected(Connection connection) 
	{
		super.Disconnected(connection);
	}

	@Override
	public void Idle(Connection connection) 
	{
		// TODO Auto-generated method stub
		super.Idle(connection);
	}

	@Override
	public void MessageReceived(Connection connection, Object object) 
	{
		if (object instanceof Packet)
		{
			Packet Data = (Packet) object;
			if (Data.GetPacketName().equals("chat_message"))
			{
				m_messages.Add(new Message((String) Data.GetEntry("player_name") + ": " + (String) Data.GetEntry("chat_message_string"), ServerChatComponent.FIRST_POSITION, new Vector3f(1, 1, 1), 1));
				HashChanged = true;
				DebugManager.Log(getClass().getSimpleName(), (String) Data.GetEntry("chat_message_string"));
			}
				//				DebugManager.Log(getClass().getSimpleName(), (String) Data.GetEntry("chat_message"));
			
			DebugManager.Log(getClass().getSimpleName(), "You received a packet: " + Data.GetPacketName());
		}
	}
	
	public EngineList<Message> GetMessages()
	{
		return m_messages;
	}
}
