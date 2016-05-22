package com.studios.base.engine.core.util;

public class OS
{
	private static String m_osName = null;
	
	private static String GetOSName()
	{
		if (m_osName == null)
		{
			m_osName = System.getProperty("os.name").toLowerCase();
		}
		return m_osName;
	}
	
	public static String GetPlatform()
	{
		String OSName = System.getProperty("os.name", "generic").toLowerCase();
	    if (OSName.startsWith("windows")) 
	    {
	      return "win32";
	    }
	    else if (OSName.startsWith("linux")) 
	    {
	      return "linux";
	    }
	    else if (OSName.startsWith("sunos")) 
	    {
	      return "solaris";
	    }
	    else if (OSName.startsWith("mac") || OSName.startsWith("darwin")) 
	    {
	      return "mac";
	    }
	    else return "generic";
	}
	
	public static boolean IsWindows()
	{
		return GetOSName().startsWith("windows ");
	}
	
	public static boolean IsLinux()
	{
		return GetOSName().startsWith("linux ");
	}
	
	public static boolean IsMac()
	{
		return GetOSName().startsWith("mac") || GetOSName().startsWith("darwin");
	}
}
