package com.studios.base.engine.framework.terrain;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.studios.base.engine.framework.debugging.DebugManager;
import com.studios.base.engine.framework.math.Vector2f;
import com.studios.base.engine.rendering.data.Normal;
import com.studios.base.engine.rendering.data.TextureCoordinate;
import com.studios.base.engine.rendering.data.Vertex3f;
import com.studios.base.engine.rendering.model.Model;

public class Terrain extends Model
{
	private static final int SIZE = 800;
	private static final int VERTEX_COUNT = 128;
	private static final int MAX_HEIGHT = 20;
	private static final int MAX_PIXEL_COLOR = 255;
	
	private Vector2f m_position;
	
	public Terrain(float X, float Y) 
	{
		m_position = new Vector2f(X, Y);
		
		GenerateTerrain("textures/heightmap");
		m_resource.Init(m_vertices, m_textureCoords, m_normals, m_indices);
	}
	
	private void GenerateTerrain(String HeightMapLocation)
	{
		BufferedImage image = null;
		
		try
		{
			image = ImageIO.read(new File("./data/" + HeightMapLocation + ".png"));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		image.getHeight();
//		int VERTEX_COUNT = image.getHeight();
		int count = VERTEX_COUNT * VERTEX_COUNT;
		m_vertices = new Vertex3f[count];
		m_normals = new Normal[count];
		m_textureCoords = new TextureCoordinate[count];
		m_indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT*1)];
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++)
		{
			for(int j=0;j<VERTEX_COUNT;j++)
			{
				m_vertices[vertexPointer] = new Vertex3f((float)j/((float)VERTEX_COUNT - 1) * SIZE,
													0,
													(float)i/((float)VERTEX_COUNT - 1) * SIZE);
				m_normals[vertexPointer] = new Normal(0, 1, 0);
				m_textureCoords[vertexPointer] = new TextureCoordinate((float)j/((float)VERTEX_COUNT - 1),
												(float)i/((float)VERTEX_COUNT - 1));
				DebugManager.Log(getClass().getSimpleName(), "Vertex: " + m_vertices[vertexPointer].toString());
				DebugManager.Log(getClass().getSimpleName(), "Normal: " + m_normals[vertexPointer].toString());
				DebugManager.Log(getClass().getSimpleName(), "TextureCoordinates: " + m_textureCoords[vertexPointer].toString());
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++)
		{
			for(int gx=0;gx<VERTEX_COUNT-1;gx++)
			{
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				m_indices[pointer++] = topLeft;
				m_indices[pointer++] = bottomLeft;
				m_indices[pointer++] = topRight;
				m_indices[pointer++] = topRight;
				m_indices[pointer++] = bottomLeft;
				m_indices[pointer++] = bottomRight;
			}
		}
	}
	
	
	
	public float GetHeight(int x, int z, BufferedImage image)
	{
		if (x < 0 || x > image.getHeight() || z<0 || z > image.getHeight())
		{
			return 0;
		}
		float height = image.getRGB(x, z);
		height += MAX_PIXEL_COLOR / 2f;
		height /= MAX_PIXEL_COLOR / 2f;
		height *= MAX_HEIGHT;
		return height;
		
	}
	
	public Vector2f GetPosition()
	{
		return m_position;
	}
}
