package com.studios.base.engine.rendering.shader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.opengl.NVFragmentProgram;
import org.lwjgl.opengl.NVVertexProgram;
import org.lwjgl.opengl.NVProgram;

import com.studios.base.engine.framework.debugging.DebugManager;

public class NVShader 
{
	private int m_program;
	private int m_vertexShaderProgram;
	private int m_fragmentShaderProgram;
	
	public NVShader(String ShaderFileName)
	{
		m_program = NVProgram.glGenProgramsNV();
		m_vertexShaderProgram = NVVertexProgram.glGenProgramsNV();
		m_fragmentShaderProgram = NVFragmentProgram.glGenProgramsNV();
	
		DebugManager.Log(getClass().getSimpleName(), NVProgram.glGetProgramiNV(m_program, NVProgram.GL_PROGRAM_TARGET_NV));
		
//		NVProgram.glLoadProgramNV(m_vertexShaderProgram, m_program, LoadShader(ShaderFileName + ".vs"));
//		NVProgram.glLoadProgramNV(m_fragmentShaderProgram, m_program, LoadShader(ShaderFileName + ".fs"));
		
		
	}
	
	public String LoadShader(String FileName)
	{
		StringBuilder ShaderCode = new StringBuilder();
		
		try
		{
			BufferedReader Reader = new BufferedReader(new FileReader(new File("./data/shaders/nv/" + FileName)));
			String Line;
			
			while ((Line = Reader.readLine()) != null)
			{
				ShaderCode.append(Line).append("\n");
			}
		}
		catch (IOException e)
		{
			DebugManager.PrintException(e);
		}
		
		return ShaderCode.toString();
	}
}
