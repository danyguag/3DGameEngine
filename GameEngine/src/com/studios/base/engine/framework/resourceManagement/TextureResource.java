package com.studios.base.engine.framework.resourceManagement;

import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glDeleteTextures;

public class TextureResource
{
    private int m_textureID;

    public TextureResource()
    {
        m_textureID = glGenTextures();
    }

    @Override
    protected void finalize()
    {
        glDeleteTextures(m_textureID);
    }

    public int GetTextureID()
    {
        return m_textureID;
    }
}
