package com.studios.base.engine.net;

import java.util.HashMap;

import com.studios.base.engine.net.messaging.Packet;

public class NetworkRegister
{
	public static void Register(EndPoint Con)
	{
		Con.GetKryo().register(Packet.class);
		Con.GetKryo().register(HashMap.class);
//		Con.getKryo().register(String.class);
	}
}
