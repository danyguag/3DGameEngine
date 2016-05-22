package com.studios.base.engine.framework.debugging;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.glGetShaderi;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import com.studios.base.engine.core.CoresManager;
import com.studios.base.engine.framework.game.Game;

public class DebugManager
{	
	public static void CheckStatus(int id, String GL_ERROR, String Name, String response)
    {
        if (glGetShaderi(id, GL_COMPILE_STATUS) == GL_FALSE)
        {
        	DebugManager.Log("OPENGL", GL_ERROR);
        	DebugManager.Log(Name + " OpenGl", response);
            System.exit(-1);
        }
    }

    public static void ExitWithError()
    {
        System.exit(-1);
    }

    public static void ExitWithoutError(int SleepTime)
    {
    	try
    	{
    		Thread.sleep(SleepTime);
    	}
    	catch (InterruptedException e)
    	{
    		PrintException(e);
    	}
        System.exit(-1);
    }

    public static void PrintException(Exception e)
    {
    	if (CoresManager.LoggerStream != null)
    		CoresManager.LoggerStream.append(e.getLocalizedMessage());
    	e.printStackTrace();
    	ExitWithError();
    }
    
	public static void Log(String Name, Object text)
	{
		if (CoresManager.LoggerStream != null)
			CoresManager.LoggerStream.println("[" + Name + "] : " + text);
		if (Game.TEMP_BOOL_DEBUG)
			System.out.println("[" + Name + "] : " + text);
	}
	
	public static void MakeCurrentContext()
	{
		try
		{
			Display.makeCurrent();
		}
		catch (LWJGLException e)
		{
			PrintException(e);
		}
	}
	
	public static void ReleaseCurrentContext()
	{
		try
		{
			Display.releaseContext();
		}
		catch (LWJGLException e)
		{
			PrintException(e);
		}
	}
	
	public static boolean IsCurrent()
	{
		boolean Res = false;
		
		try
		{
			Res = Display.isCurrent();
		}
		catch (LWJGLException e)
		{
			PrintException(e);
		}
		
		return Res;
	}
}
