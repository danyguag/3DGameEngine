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
import com.studios.base.engine.rendering.data.Vertex2f;

public class LineResource implements Resource
{
    private int m_vaoID;
    private EngineList<Integer> buffers;
    private int m_vertexCount;
    
    public LineResource()
    {
    	buffers = new EngineList<Integer>();
        m_vaoID = glGenVertexArrays();
        glBindVertexArray(m_vaoID);
        glEnableVertexAttribArray(0);
    }
    
    public void Init(Vertex2f[] data)
    {
    	m_vertexCount = data.length;
        StoreFloat2f(0, data);
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
		return buffers.get(Index);
	}
    
    public void DeleteModel()
    {
        for (int i = 0; i < buffers.size(); i++)
        {
        	glDeleteBuffers(i);
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
