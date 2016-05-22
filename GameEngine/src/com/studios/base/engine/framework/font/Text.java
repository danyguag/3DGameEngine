package com.studios.base.engine.framework.font;

import org.lwjgl.opengl.GL11;

import com.studios.base.engine.framework.components.GameComponent;
import com.studios.base.engine.framework.components.TextComponent;
import com.studios.base.engine.framework.math.Vector2f;
import com.studios.base.engine.framework.math.Vector3f;
import com.studios.base.engine.framework.renders.BaseRenderable;
import com.studios.base.engine.framework.renders.RenderType;
import com.studios.base.engine.framework.resourceManagement.FontResource;
import com.studios.base.engine.framework.resourceManagement.Resource;

public class Text implements BaseRenderable
{
	private String m_textString;
    private float m_fontSize;
 
    private Vector3f colour = new Vector3f(0f, 0f, 0f);
 
    private Vector2f m_position;
    private float m_lineMaxSize;
    private int m_numberOfLines;
 
    private Font m_font;
    private FontResource m_resource;
    
    private boolean m_centerText = false;
    private boolean m_shouldRender;
    private TextComponent m_parentComp;
    
    public Text(String Text, Font Font, Vector2f Position, float MaxLineLength, float FontSize, boolean Centered) 
    {
    	m_shouldRender = true;
        m_textString = Text;
        m_fontSize = FontSize;
        m_font = Font;
        m_position = Position;
        m_lineMaxSize = MaxLineLength;
        m_centerText = Centered;
        m_resource = new FontResource();
    }
    
    public Font GetFont() 
    {
        return m_font;
    }
 
    public void SetColor(float R, float G, float B) 
    {
        colour.Set(R, G, B);
    }
 
    
    public Vector3f GetColor() 
    {
        return colour;
    }
 
    public int GetNumberOfLines() 
    {
        return m_numberOfLines;
    }
 
    public Vector2f GetPosition() 
    {
        return m_position;
    }

    public void SetResource(FontResource Resource)
    {
    	m_resource = Resource;
    }
    
    public void Destroy()
    {
    	m_resource.finalize();
    	try 
    	{
			super.finalize();
		}
    	catch (Throwable e) 
    	{
			e.printStackTrace();
		}
    }
    
    public float GetFontSize() 
    {
        return m_fontSize;
    }
    
    public void SetNumberOfLines(int Number) 
    {
        m_numberOfLines = Number;
    }
    
    public boolean IsCentered() 
    {
        return m_centerText;
    }
 
    public float GetMaxLineSize() 
    {
        return m_lineMaxSize;
    }
 
    public String GetTextString() 
    {
        return m_textString;
    }

    public void SetPosition(Vector2f Position)
    {
    	m_position = Position;
    }
    
	@Override
	public boolean ShouldRender() 
	{
		return m_shouldRender;
	}

	@Override
	public boolean UsingParent() 
	{
		return true;
	}

	@Override
	public void SetUsingParent(boolean UsingParent) 
	{
		throw new IllegalStateException("Should Never use SetUsingParent(boolean UsingParent) in Text.java");
	}

	@Override
	public void SetRender(boolean Render) 
	{
		m_shouldRender = Render;
	}

	@Override
	public Resource GetResource() 
	{
		return m_resource;
	}

	@Override
	public int GetVertexAttribArrayIndex() 
	{
		return 2;
	}

	@Override
	public int GetDrawMode() 
	{
		return GL11.GL_TRIANGLES;
	}

	@Override
	public int GetDrawType() 
	{
		return GL11.GL_UNSIGNED_INT;
	}

	@Override
	public int GetIndicesOffset() 
	{
		return 0;
	}

	@Override
	public RenderType GetType() 
	{
		return RenderType.FONT;
	}

	@Override
	public GameComponent GetGameComponentParent() 
	{
		return m_parentComp;
	}

	@Override
	public void SetParent(GameComponent Parent) 
	{
		m_parentComp = (TextComponent) Parent;
	}

	@Override
	public int GetVertexCount() 
	{
		return GetResource().GetVertexCount();
	}
}
