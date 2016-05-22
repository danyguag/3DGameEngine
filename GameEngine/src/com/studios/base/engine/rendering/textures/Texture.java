package com.studios.base.engine.rendering.textures;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_RGBA8;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import com.studios.base.engine.core.CoresManager;
import com.studios.base.engine.core.util.Util;
import com.studios.base.engine.framework.debugging.DebugManager;

public class Texture
{
    private int m_id;
    private BufferedImage m_image;
    private ByteBuffer m_buffer;
    
    private int Width;
    private int Height;

    private boolean m_bind = true;
    
    public Texture(String name)
    {
        m_id = glGenTextures();
        LoadTexture(name);
        
        Width = m_image.getWidth();
        Height = m_image.getHeight();
        
        glBindTexture(GL_TEXTURE_2D, m_id);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, Width, Height, 0, GL_RGBA, GL_UNSIGNED_BYTE, m_buffer);
    }
    
    public Texture(ByteBuffer buffer, BufferedImage image)
    {
    	m_id = glGenTextures();
    	
    	Width = image.getWidth();
        Height = image.getHeight();
    	
    	glBindTexture(GL_TEXTURE_2D, m_id);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);

        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, Width, Height, 0, GL_RGBA, GL_UNSIGNED_BYTE, m_buffer);
    }
    
    public void Bind(int TextureUnit)
    {
    	if (m_bind)
    	{
    		glActiveTexture(GL_TEXTURE0 + TextureUnit);
	        glBindTexture(GL_TEXTURE_2D, m_id);
        }
    }

    private void LoadTexture(String name)
    {
        try
        {
        	File ImageFile = new File("./data/" + name);
        	
        	if (!ImageFile.exists())
        		DebugManager.PrintException(new IllegalStateException("Cannot find the texture_file: " + ImageFile.getAbsolutePath()));
        	
            m_image = ImageIO.read(ImageFile);
            int[] pixels = m_image.getRGB(0, 0, m_image.getWidth(), m_image.getHeight(), null, 0, m_image.getWidth());

            m_buffer = Util.CreateByteBuffer(m_image.getHeight() * m_image.getWidth() * 4);
            boolean hasAlpha = m_image.getColorModel().hasAlpha();

            for(int y = 0; y < m_image.getHeight(); y++)
            {
                for(int x = 0; x < m_image.getWidth(); x++)
                {
                    int pixel = pixels[y * m_image.getWidth() + x];

                    m_buffer.put((byte)((pixel >> 16) & 0xFF));
                    m_buffer.put((byte)((pixel >> 8) & 0xFF));
                    m_buffer.put((byte)((pixel) & 0xFF));
                    if(hasAlpha)
                    	m_buffer.put((byte)((pixel >> 24) & 0xFF));
                    else
                    	m_buffer.put((byte)(0xFF));
                }
            }

            m_buffer.flip();
            
        }
        catch (IOException e)
        {
        	e.printStackTrace(CoresManager.LoggerStream);
        }
    }
    
    public int GetWidth()
    {
    	return Width;
    }
    
    public int GetHeight()
    {
    	return Height;
    }
    
    public ByteBuffer GetTextureData()
    {
    	return m_buffer;
    }
    
    public int GetTextureID()
    {
    	return m_id;
    }
    
    public void DeleteTexture()
    {
    	glDeleteTextures(m_id);
    }
    
    @Override
    protected void finalize()
    {
    	DeleteTexture();
    }
    
    public boolean GetRender()
    {
    	return m_bind;
    }
    
    public void SetBind(boolean Bind)
    {
    	m_bind = Bind;
    }
}
