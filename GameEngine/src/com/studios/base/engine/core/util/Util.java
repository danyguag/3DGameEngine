package com.studios.base.engine.core.util;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL30;

import com.studios.base.engine.core.CoreEngine;
import com.studios.base.engine.framework.math.Matrix4f;
import com.studios.base.engine.rendering.data.Normal;
import com.studios.base.engine.rendering.data.TextureCoordinate;
import com.studios.base.engine.rendering.data.Vertex2f;
import com.studios.base.engine.rendering.data.Vertex3f;

public class Util
{
	private static int GroupID = 1;
	public static int GetNextRenderGoupID() { return GroupID++;  }
	
	public static double GetOpenGlMouseX(CoreEngine Engine)
	{
		return OpenglFloatX(Engine, Mouse.getX());
	}
	
	public static double GetOpenGlMouseY(CoreEngine Engine)
	{
		return OpenglFloatY(Engine, Mouse.getY());
	}
	
	public static double OpenglFloatX(CoreEngine Engine, double x)
	{
		return (-1.0 + 2.0 * x / Engine.GetWindowEngine().GetWindow().GetWidth());
	}
	
	public static double OpenglFloatY(CoreEngine Engine, double y)
	{
		return -((1.0 - 2.0 * y / Engine.GetWindowEngine().GetWindow().GetHeight()));
	}
	
    public static FloatBuffer CreateFloatBuffer(int size)
    {
        return BufferUtils.createFloatBuffer(size);
    }
    
    public static LongBuffer CreateLongBuffer(int size)
    {
        return BufferUtils.createLongBuffer(size);
    }

    public static IntBuffer CreateIntBuffer(int size)
    {
        return BufferUtils.createIntBuffer(size);
    }

    public static ByteBuffer CreateByteBuffer(int size)
    {
        return BufferUtils.createByteBuffer(size);
    }

    public static FloatBuffer StoreInMatrix4f(Matrix4f Value)
    {
    	FloatBuffer ResultBuffer = CreateFloatBuffer(16);
    	for (int i = 0; i < 4; i++)
    		for (int j = 0; j < 4; j++)
    			ResultBuffer.put(Value.Get(i, j));
    	ResultBuffer.flip();
    	return ResultBuffer;
    }
    
    public static FloatBuffer StoreInFloatBuffer3f(Vertex3f[] data)
    {
        FloatBuffer buffer = CreateFloatBuffer(data.length * 3);
        
        for (Vertex3f Vertex : data)
        {
        	buffer.put(Vertex.GetX());
        	buffer.put(Vertex.GetY());
        	buffer.put(Vertex.GetZ());
        }
        
        buffer.flip();
        return buffer;
    }
    
    public static FloatBuffer StoreInFloatBufferNormal(Normal[] data)
    {
        FloatBuffer buffer = CreateFloatBuffer(data.length * 3);
        
        for (Normal Normal : data)
        {
        	buffer.put(Normal.GetX());
        	buffer.put(Normal.GetY());
        	buffer.put(Normal.GetZ());
        }
        
        buffer.flip();
        return buffer;
    }
    
    public static FloatBuffer StoreInFloatBuffer2f(Vertex2f[] data)
    {
    	FloatBuffer buffer = CreateFloatBuffer(data.length * 2);
        
        for (Vertex2f Vertex : data)
        {
        	buffer.put(Vertex.GetX());
        	buffer.put(Vertex.GetY());
        }
        
        buffer.flip();
        return buffer;
    }
    
    public static FloatBuffer StoreInFloatBufferTextureCoordinate(TextureCoordinate[] data)
    {
    	FloatBuffer buffer = CreateFloatBuffer(data.length * 2);
        
        for (TextureCoordinate Vertex : data)
        {
        	buffer.put(Vertex.GetX());
        	buffer.put(Vertex.GetY());
        }
        
        buffer.flip();
        return buffer;
    }

    public static IntBuffer StoreInIntBuffer(int[] data)
    {
        IntBuffer buffer = CreateIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public static ByteBuffer StoreInByteBuffer(byte[] data, int coordinateSize)
    {
        ByteBuffer buffer = CreateByteBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
    
    public static void VAOINIT(int Pointer)
    {
    	Pointer = GL30.glGenVertexArrays();
    }
}
