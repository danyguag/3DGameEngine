package com.studios.base.engine.framework.resourceManagement;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.nio.FloatBuffer;

import com.studios.base.engine.core.util.Util;
import com.studios.base.engine.framework.containers.util.EngineList;
import com.studios.base.engine.rendering.data.TextureCoordinate;
import com.studios.base.engine.rendering.data.Vertex2f;

public class FontResource implements Resource
{
	private int m_vaoID;
	private EngineList<Integer> m_buffers;
	private int m_vertexCount;
	
	 public FontResource()
	 {
		 m_buffers = new EngineList<Integer>();
		 m_vaoID = glGenVertexArrays();
		 glBindVertexArray(m_vaoID);
         glEnableVertexAttribArray(0);
         glEnableVertexAttribArray(1);
	 }
	    
	 public FontResource Init(Vertex2f[] data, TextureCoordinate[] textureCoordinates)
	 {
		 m_vertexCount = data.length;
		 StoreFloat2f(0, data);
		 StoreFloatTexCoord(1, textureCoordinates);
		 glBindVertexArray(0);
		 return this;
	 }
	    
	 public void StoreFloat2f(int attrib, Vertex2f[] data)
	 {
		 int vbo = glGenBuffers();
		 glBindBuffer(GL_ARRAY_BUFFER, vbo);
		 m_buffers.add(vbo);
		 FloatBuffer buffer = Util.StoreInFloatBuffer2f(data);
		 glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		 glVertexAttribPointer(attrib, 2, GL_FLOAT, false, 0,0);
	 }
	 
	 public void StoreFloatTexCoord(int attrib, TextureCoordinate[] data)
	 {
		 int vbo = glGenBuffers();
		 glBindBuffer(GL_ARRAY_BUFFER, vbo);
		 m_buffers.add(vbo);
		 FloatBuffer buffer = Util.StoreInFloatBufferTextureCoordinate(data);
		 glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
		 glVertexAttribPointer(attrib, 2, GL_FLOAT, false, 0,0);
	 }
	    
	 public int GetVaoID()
	 {
		 return m_vaoID;
	 }
	    
	 public EngineList<Integer> GetBuffers()
	 {
		 return m_buffers;
	 }
	 
	 @Override
	 public void finalize()
	 {
		 for (Integer Buffer : m_buffers)
		 {
			 glDeleteBuffers(Buffer);
		 }
		 glDeleteVertexArrays(m_vaoID);
	 }

	@Override
	public void SetVaoID() 
	{
		m_vaoID = glGenVertexArrays();
	}

	@Override
	public int GetBufferByIndex(int Index) 
	{
		return m_buffers.get(Index);
	}

	@Override
	public int GetVertexCount() 
	{
		return m_vertexCount;
	}
}
