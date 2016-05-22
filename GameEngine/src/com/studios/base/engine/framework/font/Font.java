package com.studios.base.engine.framework.font;

import java.io.File;

import com.studios.base.engine.core.CoreEngine;
import com.studios.base.engine.core.CoresManager;
import com.studios.base.engine.rendering.textures.Texture;

public class Font 
{
    private TextMeshDataGen m_loader;
    private Texture m_texture;
    
    private Font(CoreEngine Engine, FontType PreMadeType)
    {
    	switch (PreMadeType)
    	{
    	case ARIAL:
    	{
    		m_loader = new TextMeshDataGen(Engine, new File("./data/font/arial.fnt"));
            m_texture = CoresManager.Asset.CreateTexture("font/arial.png");
    	}break;
    	case MYANMAR:
    	{
    		m_loader = new TextMeshDataGen(Engine,new File("./data/font/myanmar.fnt"));
            m_texture = CoresManager.Asset.CreateTexture("font/myanmar.png");
    	}break;
    	default:
    		throw new IllegalStateException("Should never get here(FOUND IN FONT.JAVA[Premade Constructor])");
    	}
    }
    
    public Font(CoreEngine Engine, String Texture, String FontDataFile) 
    {
        m_loader = new TextMeshDataGen(Engine, new File("./data/font/" + FontDataFile + ".fnt"));
        m_texture = CoresManager.Asset.CreateTexture("font/" + Texture);
    }
    
    public Texture GetFontTexture()
    {
    	return m_texture;
    }
    
    public TextMeshData LoadText(Text Text) 
    {
        return m_loader.CreateTextMesh(Text);
    }
    
    public static Font ARIAL(CoreEngine Engine) { return new Font(Engine, FontType.ARIAL); }
    public static Font MYANMAR(CoreEngine Engine) { return new Font(Engine, FontType.MYANMAR); }
}
