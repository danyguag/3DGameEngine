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

import org.lwjgl.opengl.GL11;

import com.studios.base.engine.core.util.Util;
import com.studios.base.engine.framework.containers.util.EngineList;
import com.studios.base.engine.rendering.data.Vertex2f;

public class UIModelInitializer implements Resource 
{
    private int m_vaoID;
    private EngineList<Integer> m_buffers;
    private int m_vertexCount;
    
    public UIModelInitializer()
    {
        m_vaoID = glGenVertexArrays();
        m_buffers = new EngineList<Integer>();
        glBindVertexArray(m_vaoID);
        glEnableVertexAttribArray(0);
        
        Vertex2f[] Vertices = new Vertex2f[] 
        		{
        			new Vertex2f(-1, 1),
        			new Vertex2f(-1, -1),
        			new Vertex2f(1, 1),
        			new Vertex2f(1, -1)
        		};
        
        StoreFloat(Vertices);
    }

    public void StoreFloat(Vertex2f[] data)
    {
    	m_vertexCount = data.length;
        int VboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER,VboID);
        m_buffers.Add(VboID);
        glBufferData(GL_ARRAY_BUFFER, Util.StoreInFloatBuffer2f(data), GL_STATIC_DRAW);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
    }

    @Override
    public int GetVaoID()
    {
        return m_vaoID;
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
	public EngineList<Integer> GetBuffers() 
	{
		return m_buffers;
	}

	@Override
	public int GetBufferByIndex(int Index) 
	{
		return m_buffers.get(Index);
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
