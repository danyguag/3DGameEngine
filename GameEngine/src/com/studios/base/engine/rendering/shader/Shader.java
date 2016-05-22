package com.studios.base.engine.rendering.shader;

import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glBindAttribLocation;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUniform2f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.opengl.GL15;

import com.studios.base.engine.core.CoresManager;
import com.studios.base.engine.core.util.Util;
import com.studios.base.engine.framework.containers.util.EngineList;
import com.studios.base.engine.framework.containers.util.EngineMap;
import com.studios.base.engine.framework.debugging.DebugManager;
import com.studios.base.engine.framework.math.Matrix4f;
import com.studios.base.engine.framework.math.Vector2f;
import com.studios.base.engine.framework.math.Vector3f;

public class Shader
{
    private int m_programID;
	
	private EngineMap<ShaderType, String> m_shaders;
	private EngineList<Integer> m_shaderPointers;
	private EngineList<ShaderType> m_allShaders;
	private EngineMap<String, Integer> m_uniforms;
    
    private boolean m_initialized = false;
    private boolean m_attached = false;
    
    public Shader()
    {
    	m_shaders = new EngineMap<ShaderType, String>();
    	m_shaderPointers = new EngineList<Integer>();
    	m_allShaders = new EngineList<ShaderType>();
    	m_uniforms = new EngineMap<String, Integer>();  
    	
        m_programID = glCreateProgram();
        if (m_programID == 0)
        {
            DebugManager.Log("Shader", "The Shader Failed to Initialize");
        }        
    }
    
    public void BindAttribute(int attribNumber, String location)
    {
        glBindAttribLocation(m_programID, attribNumber, location);
    }
    
    public void AttachShaders()
    {
    	m_attached = true;
    	for (int ShaderPointer : m_shaderPointers)
    	{
    		glAttachShader(m_programID, ShaderPointer);
    	}
    	
    	if (!m_initialized)
    		InitializeShader();
    }
    
    public void AddVertexFragmentAndAttachAndInitShader(String FileNameAndPath)
    {
    	AddVertexShader(FileNameAndPath);
    	AddFragmentShader(FileNameAndPath);
    	AttachShaders();
    	InitializeShader();
    }
    
    public void AddVertexShader(String FileNameAndPath)
    {
    	ShaderType Type = ShaderType.GL_VERTEX_SHADER;
    	m_allShaders.Add(Type);
		String ShaderSourceCode = LoadShader(FileNameAndPath + ".vs", Type);
    	m_shaders.Put(Type, ShaderSourceCode);
    }
    
    public void AddFragmentShader(String FileNameAndPath)
    {
    	ShaderType Type = ShaderType.GL_FRAGMENT_SHADER;
    	m_allShaders.Add(Type);
    	String ShaderSourceCode = LoadShader(FileNameAndPath + ".fs", Type);
    	m_shaders.Put(Type, ShaderSourceCode);
    }
    
    public void AddGeometryShader(String FileNameAndPath)
    {
    	ShaderType Type = ShaderType.GL_GEOMETRY_SHADER;
    	m_allShaders.Add(Type);
    	String ShaderSourceCode = LoadShader(FileNameAndPath + "gs", Type);
    	m_shaders.Put(Type, ShaderSourceCode);
    }

    public void Bind()
    {
    	if (m_attached)
    	{
    		glUseProgram(m_programID);
    	}
    	else if (!m_attached)
    	{
    		m_attached = true;
    		AttachShaders();
    	}
    }
    
    public void UnBind()
    {
        glUseProgram(0);
    }

    public void CleanUp()
    {
    	for (int ShaderPointer : m_shaderPointers)
    	{
    		glDetachShader(m_programID, ShaderPointer); 
    		glDeleteShader(ShaderPointer);              
    	}
        glDeleteProgram(m_programID);
    }

    private void InitializeShader()
    {
    	if (!m_attached)
    		AttachShaders();
    	if (!m_initialized)
    	{
            glLinkProgram(m_programID);
            glValidateProgram(m_programID);
            m_initialized = true;
    	}
    }

    public void AddUniform(String UniformName)
    {
    	int Location = glGetUniformLocation(m_programID, UniformName);
    	m_uniforms.Put(UniformName, Location);
    }

    public void SetUniform(String UniformName, int value)
    {
    	int Location = m_uniforms.get(UniformName);
    	if (Location == -1)
    		throw new IllegalStateException("The Uniform has never been added" + UniformName);
        glUniform1i(m_uniforms.get(UniformName), value);
    }

    public void SetUniform(String UniformName, float value)
    {
    	int Location = m_uniforms.get(UniformName);
    	if (Location == -1)
    		throw new IllegalStateException("The Uniform has never been added" + UniformName);
        glUniform1f(Location, value);
    }

    public void SetUniform(String UniformName, Vector3f value)
    {
    	int Location = m_uniforms.get(UniformName);
    	if (Location == -1)
    		throw new IllegalStateException("The Uniform has never been added: " + UniformName);
        glUniform3f(Location, value.GetX(), value.GetY(), value.GetZ());
    }

    public void SetUniform(String UniformName, Vector2f value)
    {
    	int Location = m_uniforms.get(UniformName);
    	if (Location == -1)
    		throw new IllegalStateException("The Uniform has never been added: " + UniformName);
        glUniform2f(Location, value.GetX(), value.GetY());
    }
    
    public void SetUniform(String UniformName, boolean transpose, Matrix4f value)
    {
    	int Location = m_uniforms.get(UniformName);
    	if (Location == -1)
    		throw new IllegalStateException("The Uniform has never been added: " + UniformName);
    	
        glUniformMatrix4(Location, transpose, Util.StoreInMatrix4f(value));
    }

    public void SetUniform(String UniformName, boolean value)
    {
    	int Location = m_uniforms.get(UniformName);
    	if (Location == -1)
    		throw new IllegalStateException("The Uniform has never been added: " + UniformName);
        if (value)
            glUniform1i(Location,1);
        else
            glUniform1i(Location,0);
    }
    
    private String LoadShader(String FileName, ShaderType ShaderType)
    {
        StringBuilder ShaderCode = new StringBuilder();
        try
        {
			BufferedReader ShaderReader = new BufferedReader(
					new FileReader("./data/shaders/" + FileName));
            String line;
            while ((line = ShaderReader.readLine()) != null)
            {
            	ShaderCode.append(line).append("\n");

            }
            ShaderReader.close();
        }
        catch (IOException e)
        {
            DebugManager.Log("Shader", "Shader file: {" + "./data/shaders/" 
            				+ FileName + "} could not be found.");
            e.printStackTrace(CoresManager.LoggerStream);
            DebugManager.ExitWithError();
        }
        
        int GLShaderType = 0;
		switch (ShaderType)
		{
		case GL_VERTEX_SHADER :
		{	
			GLShaderType = GL_VERTEX_SHADER;
		}break;
		case GL_FRAGMENT_SHADER :
		{	
			GLShaderType = GL_FRAGMENT_SHADER;
		}break;    		
		case GL_GEOMETRY_SHADER :
		{	
			GLShaderType = GL_GEOMETRY_SHADER;
		}break;

		default :
			throw new IllegalStateException("ShaderType could not be found");
		}
		
      	int ShaderID = glCreateShader(GLShaderType);
    	m_shaderPointers.add(ShaderID);
        glShaderSource(ShaderID, ShaderCode);
        glCompileShader(ShaderID);
        DebugManager.CheckStatus(ShaderID, glGetShaderInfoLog(ShaderID, 500), "Shader", FileName + " Shader could not compile properly");

        
        return ShaderCode.toString();
    }

    @Override
    public void finalize()
    {
        GL15.glDeleteBuffers(m_programID);
    }
}
