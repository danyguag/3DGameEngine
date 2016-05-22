package com.studios.base.engine.script;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.script.Invocable;
import javax.script.ScriptEngine;

import com.studios.base.engine.core.CoreEngine;
import com.studios.base.engine.core.CoresManager;
import com.studios.base.engine.framework.debugging.DebugManager;
import com.studios.base.engine.framework.graphics.Graphics;
import com.studios.base.engine.framework.scenegraph.GameObject;
import com.studios.base.engine.framework.threading.Snapshot;

public class JSScript implements EngineScript
{
	private static final String IMPORTS = 	"var DebugManager = Java.type('com.studios.base.engine.framework.debugging.DebugManager');\n" +
											"var Keyboard = Java.type('org.lwjgl.input.Keyboard');\n" +
											"var This;\n";
	
	private String m_file;
	private EngineScriptMethods m_scriptMethods;
	private boolean m_loaded;
	
	public JSScript(String FileName)
	{
		m_loaded = false;
		m_file = "./data/scripts/js/" + FileName + ".js";
	}
	
	@Override
	public void LoadScript(ScriptEngine Engine, Class<?> Interface)
	{
		try 
		{
			Engine.eval(IMPORTS + LoadScriptToString(m_file));
			Invocable Methods = (Invocable) Engine;
			
			m_scriptMethods = Methods.getInterface(EngineScriptMethods.class);
		}
		catch (Exception e) 
		{
			DebugManager.PrintException(e);
		}		
		m_loaded = true;
	}

	@Override
	public void Start(CoreEngine Engine, GameObject Parent, Graphics Graphic) 
	{
		if (m_loaded)
			m_scriptMethods.Start(Engine, Parent, Graphic);
	}

	@Override
	public void Update(Snapshot CurrentGameShot) 
	{
		if (m_loaded)
			m_scriptMethods.Update(CurrentGameShot);
	}
	
	@Override
	public ScriptType GetType()
	{
		return ScriptType.JAVASCRIPT;
	}
	
	private static String LoadScriptToString(String FileName)
	{
        StringBuilder ScriptCode = new StringBuilder();
        try
        {
			BufferedReader ScriptReader = new BufferedReader(
					new FileReader(FileName));
            String line;
            while ((line = ScriptReader.readLine()) != null)
            {
            	ScriptCode.append(line).append("\n");

            }
            ScriptReader.close();
        }
        catch (IOException e)
        {
            DebugManager.Log("Script", "Script file: {" + "./data/scripts/js/" 
            				+ FileName + "} could not be found.");
            e.printStackTrace(CoresManager.LoggerStream);
            DebugManager.ExitWithError();
        }
		return ScriptCode.toString();
	}
}
