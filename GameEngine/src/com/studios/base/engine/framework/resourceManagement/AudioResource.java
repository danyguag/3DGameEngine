package com.studios.base.engine.framework.resourceManagement;

import static org.lwjgl.openal.AL10.*;

public class AudioResource 
{
	private int m_source;
	
	public AudioResource()
	{
		m_source = alGenSources();
	}
	
	public int GetSource()
	{
		return m_source;
	}
	
	@Override
	public void finalize()
	{
		alDeleteSources(m_source);
	}
}
