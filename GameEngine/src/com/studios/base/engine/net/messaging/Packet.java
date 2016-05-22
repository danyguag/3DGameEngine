package com.studios.base.engine.net.messaging;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

public class Packet implements Serializable
{
	private static final long serialVersionUID = 5196826001017420737L;
	
	private String m_packetName;
	private HashMap<String, Object> m_packetData;
	private int m_slotsReserved;

	public Packet() {}
	
	public Packet(String Name, int Slots)
	{
		m_packetName = Name;
		m_slotsReserved = Slots;
		m_packetData = new HashMap<String, Object>();
	}
	
	public void AddEntry(String DataName, Object Data)
	{
		if (!m_packetData.containsValue(DataName) && m_packetData.size() < m_slotsReserved)
			m_packetData.put(DataName, Data);
		else
			Log(DataName + "(" + Data.getClass().getSimpleName() + ") has already been added");
	}
	
	public void RemoveEntry(String DataName)
	{
		if (m_packetData.containsValue(DataName))
			m_packetData.remove(DataName);
		else
			Log(DataName + " <- You cannot remove something that you have never added");
	}
	
	public Object GetEntry(String DataName)
	{
		Object Res = m_packetData.get(DataName);
		
		if (Res == null)
			throw new IllegalStateException(DataName + " <- has never been added");
		
		return Res;
	}
	
	public String GetPacketName()
	{
		return m_packetName;
	}
	
	public int GetSlotsReserved()
	{
		return m_slotsReserved;
	}
	
	public HashMap<String, Object> GetData()
	{
		return m_packetData;
	}
	
	public byte[] GetByteData()
	{
		byte[] Result;
		
		ByteArrayOutputStream BYTEStream = new ByteArrayOutputStream();
		try 
		{
			ObjectOutputStream OBJStream = new ObjectOutputStream(BYTEStream);
			OBJStream.writeObject(this);
			OBJStream.flush();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		Result = BYTEStream.toByteArray();
		
		return Result;
	}
	
	public void Log(Object Message)
	{
		System.out.println("[" + getClass().getSimpleName() + "]: " + Message);
	}
}
