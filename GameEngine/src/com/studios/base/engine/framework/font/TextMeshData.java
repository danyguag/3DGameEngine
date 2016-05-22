package com.studios.base.engine.framework.font;

import com.studios.base.engine.rendering.data.TextureCoordinate;
import com.studios.base.engine.rendering.data.Vertex2f;

public class TextMeshData 
{
	private Vertex2f[] m_vertexPositions;
    private TextureCoordinate[] m_textureCoords;
     
    public TextMeshData(Vertex2f[] VertexPositions, TextureCoordinate[] TextureCoords)
    {
        this.m_vertexPositions = VertexPositions;
        this.m_textureCoords = TextureCoords;
    }
 
    public Vertex2f[] GetVertexPositions() 
    {
        return m_vertexPositions;
    }
 
    public TextureCoordinate[] GetTextureCoords() 
    {
        return m_textureCoords;
    }
 
    public int GetVertexCount() 
    {
        return m_vertexPositions.length;
    }

}
