package com.studios.base.engine.framework.resourceManagement;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
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
import com.studios.base.engine.rendering.data.Normal;
import com.studios.base.engine.rendering.data.TextureCoordinate;
import com.studios.base.engine.rendering.data.Vertex2f;
import com.studios.base.engine.rendering.data.Vertex3f;

public class ModelResource implements Resource
{
    private int m_vaoID;
    private EngineList<Integer> buffers;
    private int m_vertexCount;
    
    public ModelResource()
    {
    	buffers = new EngineList<Integer>();
        m_vaoID = glGenVertexArrays();
        glBindVertexArray(m_vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);
//        glEnableVertexAttribArray(2);
    }
    
    public void Init(Vertex3f[] Vertices, TextureCoordinate[] textureCoordinates, Normal[] Normals, int[] indices)
    {
    	m_vertexCount = Vertices.length;
        StoreFloat3f(0, Vertices);
        StoreFloatTextureCoordinate(1, textureCoordinates);
        StoreFloatNormal(2, Normals);
        StoreInt(indices);
        glBindVertexArray(0);
    }
    
    public void StoreFloat2f(int attrib, Vertex2f[] data)
    {
    	int vbo = glGenBuffers();
    	glBindBuffer(GL_ARRAY_BUFFER, vbo);
    	buffers.Add(vbo);
        FloatBuffer buffer = Util.StoreInFloatBuffer2f(data);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attrib, 2, GL_FLOAT, false, 0,0);
    }
    
    public void StoreFloatTextureCoordinate(int attrib, TextureCoordinate[] data)
    {
    	int vbo = glGenBuffers();
    	glBindBuffer(GL_ARRAY_BUFFER, vbo);
    	buffers.Add(vbo);
        FloatBuffer buffer = Util.StoreInFloatBufferTextureCoordinate(data);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attrib, 2, GL_FLOAT, false, 0,0);
    }
    
    public void StoreFloat3f(int attrib, Vertex3f[] data)
    {
    	int vbo = glGenBuffers();
    	glBindBuffer(GL_ARRAY_BUFFER, vbo);
    	buffers.Add(vbo);
        FloatBuffer buffer = Util.StoreInFloatBuffer3f(data);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attrib, 3, GL_FLOAT, false, 0, 0);
    }
    
    public void StoreFloatNormal(int attrib, Normal[] data)
    {
    	int vbo = glGenBuffers();
    	glBindBuffer(GL_ARRAY_BUFFER, vbo);
    	buffers.Add(vbo);
        FloatBuffer buffer = Util.StoreInFloatBufferNormal(data);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        glVertexAttribPointer(attrib, 3, GL_FLOAT, false, 0, 0);
    }
    
    public void StoreInt(int[] indices)
    {
    	int ibo = glGenBuffers();
    	glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo);
    	buffers.Add(ibo);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, Util.StoreInIntBuffer(indices), GL_STATIC_DRAW);
    }
    
    public int GetVaoID()
    {
        return m_vaoID;
    }
    
    public EngineList<Integer> GetBuffers()
    {
    	return buffers;
    }
    
    @Override
	public int GetBufferByIndex(int Index) 
	{
		return buffers.Get(Index);
	}
    
    public void DeleteModel()
    {
        for (Integer Buffer : buffers)
        {
        	glDeleteBuffers(Buffer);
        }
        glDeleteVertexArrays(m_vaoID);
    }
    
    @Override
    public void finalize()
    {
        DeleteModel();
    }

	@Override
	public void SetVaoID() 
	{
		m_vaoID = glGenVertexArrays();
	}

	@Override
	public int GetVertexCount() 
	{
		return m_vertexCount;
	}
}
