package com.studios.base.engine.framework.components;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.studios.base.engine.core.CoresManager;
import com.studios.base.engine.framework.containers.util.EngineList;
import com.studios.base.engine.framework.debugging.DebugManager;

public class JSON 
{
	JSONArray Object;
	private JSONObject m_obj;
	
	public JSON(String FileNameAndLocation)
	{
		m_obj = new JSONObject(LoadFile(FileNameAndLocation));
	   
    }

    public EngineList<String> GetAllObjects()
    {
        EngineList<String> Result = new EngineList<String>();

        for (String nObject : m_obj.keySet())
        {
            Result.Add(nObject);
        }
        return Result;
    }
	
	public Object Get(String Name)
	{
		return m_obj.get(Name);
	}
	
    private String LoadFile(String FileName)
    {
        StringBuilder Source = new StringBuilder();
        try
        {
			BufferedReader ShaderReader = new BufferedReader(
					new FileReader("./data/json/" + FileName + ".json"));
            String line;
            while ((line = ShaderReader.readLine()) != null)
            {
            	Source.append(line).append("\n");

            }
            ShaderReader.close();
        }
        catch (IOException e)
        {
            DebugManager.Log("JSON", "JSON file: {" + "./data/" 
            				+ FileName + ".json} could not be found.");
            e.printStackTrace(CoresManager.LoggerStream);
            DebugManager.ExitWithError();
        }
        return Source.toString();
    }
}
