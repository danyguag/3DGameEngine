package com.studios.base.engine.core.util;

public class UIDGenerator
{
	private static int currentID = 0;
	public static int GetNextID()
	{
		return GetID();
	}
	
	private static int GetID()
	{
//		for (IServerPeer Client : GameServer.Clients)
//		{
//			if (Client.GetUuid() == currentID)
//			{
//				for (IServerPeer NextClient : GameServer.Clients)
//				{
//					if (NextClient.GetUuid() == (currentID + 1))
//					{
//						currentID = GameServer.Clients.size() + 1;
//					}
//				}
//			}
//		}
		return currentID++;
	}
}