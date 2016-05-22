package com.studios.base.engine.framework.components;

import com.studios.base.engine.framework.containers.util.EngineList;
import com.studios.base.engine.framework.containers.util.EngineMap;
import com.studios.base.engine.framework.font.Font;
import com.studios.base.engine.framework.font.Message;
import com.studios.base.engine.framework.font.Text;
import com.studios.base.engine.framework.font.TextMeshData;
import com.studios.base.engine.framework.math.Vector2f;
import com.studios.base.engine.framework.math.Vector3f;
import com.studios.base.engine.framework.resourceManagement.FontResource;
import com.studios.base.engine.framework.threading.Snapshot;
import com.studios.base.engine.framework.transform.TransformType;
import com.studios.base.engine.rendering.shader.Shader;

public class TextComponent extends GameComponent
{
	protected final Font m_font;
	protected final float m_maxLineLength;
	protected final boolean m_isCentered;
	protected EngineMap<Message, Text> m_texts;
	protected boolean m_render = true;
	
	public TextComponent(Font nFont, float MaxLineLength, boolean Centered)
	{
		super.Name = "TextComponent";
		m_texts = new EngineMap<Message, Text>();
		m_font = nFont;
		m_maxLineLength = MaxLineLength;
		m_isCentered = Centered;
	}
	
	@Override 
	public void Init()
	{ 
		GetGraphics().AddTextComponent(this);
	}
	
	@Override
	public void Update(Snapshot CurrentGameShot)
	{
		
	}
	
	@Override
	public void Render(Shader nShader)
	{
		if (m_render)
		{
			for (Text nText : m_texts.GetValues())
			{
				GetGraphics().DrawString(nShader, nText);
			}
		}
	}
	
	public Message AddMessage(Message nMessage)
	{
		Text DrawingText = new Text(nMessage.GetMessage(), m_font, nMessage.GetPosition(), m_maxLineLength, nMessage.GetFontSize(), m_isCentered);
        TextMeshData data = m_font.LoadText(DrawingText);
        FontResource Resource = new FontResource();
        Resource.Init(data.GetVertexPositions(), data.GetTextureCoords());
        DrawingText.SetResource(Resource);
		DrawingText.SetParent(this);
		DrawingText.SetColor(nMessage.GetColor().GetX(), nMessage.GetColor().GetY(), nMessage.GetColor().GetZ());
		m_texts.Put(nMessage, DrawingText);
		
		return nMessage;
	}

	public Message AddMessage(String StringedMessage, Vector2f Position, Vector3f Color, float FontSize)
	{
		Message nMessage = new Message(StringedMessage, Position, Color, FontSize);
		Text DrawingText = new Text(nMessage.GetMessage(), m_font, nMessage.GetPosition(), m_maxLineLength, nMessage.GetFontSize(), m_isCentered);
        TextMeshData data = m_font.LoadText(DrawingText);
        FontResource Resource = new FontResource();
        Resource.Init(data.GetVertexPositions(), data.GetTextureCoords());
        DrawingText.SetResource(Resource);
		DrawingText.SetParent(this);
		DrawingText.SetColor(nMessage.GetColor().GetX(), nMessage.GetColor().GetY(), nMessage.GetColor().GetZ());
		m_texts.Put(nMessage, DrawingText);
		
		return nMessage;
	}
	
	public void Remove(Message Text)
	{
		if (Text != null && m_texts.containsKey(Text))
		{
			m_texts.remove(Text);
		}
	}

	public void RemoveAndDestroy(Message nMessage)
	{
		if (nMessage != null && m_texts.containsKey(nMessage))
		{
			Text Res = m_texts.get(nMessage);
			m_texts.remove(nMessage);
			Res.Destroy();
		}
	}
	
	public void SetRender(boolean Render)
	{
		m_render = Render;
	}
	
	public boolean IsRendering()
	{
		return m_render;
	}

	public void RemoveAll()
	{
		if (m_texts.size() > 0)
		{
			for (int i = 0; i < m_texts.size(); i++)
			{
				EngineList<Message> Messages = m_texts.GetKeys();
				m_texts.remove(m_texts.get(Messages.get(i)));
			}
		}
	}
	
	public Font GetFont()
	{
		return m_font;
	}
	
	public float GetMaxLineLength()
	{
		return m_maxLineLength;
	}
	
	public boolean IsCentered()
	{
		return m_isCentered;
	}
	
	@Override
	public TransformType GetTransformType() 
	{
		return TransformType.NEITHER;
	}
}
