package com.studios.base.engine.framework.graphics;

import java.util.Collection;
import java.util.HashMap;

import com.studios.base.engine.core.CoresManager;
import com.studios.base.engine.framework.components.Entity;
import com.studios.base.engine.framework.components.GameComponent;
import com.studios.base.engine.framework.components.LineComponent;
import com.studios.base.engine.framework.components.TerrainComponent;
import com.studios.base.engine.framework.components.TextComponent;
import com.studios.base.engine.framework.components.UIComponent;
import com.studios.base.engine.framework.containers.util.EngineList;
import com.studios.base.engine.framework.containers.util.EngineMap;
import com.studios.base.engine.framework.font.Font;
import com.studios.base.engine.framework.font.FontRenderer;
import com.studios.base.engine.framework.font.Text;
import com.studios.base.engine.framework.font.TextMeshData;
import com.studios.base.engine.framework.math.Scale2f;
import com.studios.base.engine.framework.math.Vector2f;
import com.studios.base.engine.framework.math.Vector3f;
import com.studios.base.engine.framework.renders.Renderer;
import com.studios.base.engine.framework.renders.UIRenderer;
import com.studios.base.engine.framework.resourceManagement.FontResource;
import com.studios.base.engine.framework.terrain.Terrain;
import com.studios.base.engine.rendering.MainFrameBufferRenderingEngine;
import com.studios.base.engine.rendering.data.Normal;
import com.studios.base.engine.rendering.data.TextureCoordinate;
import com.studios.base.engine.rendering.data.Vertex2f;
import com.studios.base.engine.rendering.data.Vertex3f;
import com.studios.base.engine.rendering.model.Line;
import com.studios.base.engine.rendering.model.Model;
import com.studios.base.engine.rendering.model.TexturedModel;
import com.studios.base.engine.rendering.model.UITexture;
import com.studios.base.engine.rendering.shader.ComputeShader;
import com.studios.base.engine.rendering.shader.Shader;
import com.studios.base.engine.rendering.textures.Texture;

public class Graphics 
{
	private EngineMap<String, ComputeShader> 	m_computeShaders;
	private FontRenderer 						m_textRenderer;
	private EngineMap<Font, EngineList<Text>> 	m_texts;
	private Renderer<TexturedModel> 			m_modelRenderer;
	private Renderer<Line> 						m_lineRenderer;
	private UIRenderer 							m_uiRenderer;
	private EngineList<TextComponent> 			m_textComponents;
	
	private MainFrameBufferRenderingEngine m_renderingEngine;
	
	public Graphics()
	{
		m_computeShaders 	= new EngineMap<String, ComputeShader>();
		m_texts 			= new EngineMap<Font, EngineList<Text>>();
		m_modelRenderer 	= new Renderer<TexturedModel>();
		m_lineRenderer 		= new Renderer<Line>();
		m_uiRenderer		= new UIRenderer();
		m_textRenderer		= new FontRenderer();
		m_textComponents 	= new EngineList<TextComponent>();
	}
	
	public void Render3D(int CurrentRenderPass, Shader ForwardRenderShader)
	{
		if (m_modelRenderer.GetRenderables().Length() > 0)
			m_modelRenderer.Render(ForwardRenderShader);
	}
	
	public void Render2D(Shader LineShader, Shader UIShader, Shader FontShader)
	{
//		if (m_computeShaders.Length() > 0)
//			for (ComputeShader ComputeShader : m_computeShaders.GetValues())
//				ComputeShader.Bind();
		if (m_textComponents.Length() > 0)
			for (TextComponent Comp : m_textComponents)
				Comp.Render(FontShader);
		if (m_uiRenderer.GetRenderables().Length() > 0)
			m_uiRenderer.Render(UIShader);
		
		if (m_lineRenderer.GetRenderables().Length() > 0)
			m_lineRenderer.Render(LineShader);		
	}
	
	public void DrawString(Font nFont, String Message, Vector2f Position, Vector3f Color, float MaxLineLength, float FontSize, boolean Centered)
	{
		Text NewText = new Text(Message, nFont, Position, MaxLineLength, FontSize, Centered);
		NewText.SetColor(Color.GetX(), Color.GetY(), Color.GetZ());
		m_textRenderer.RenderText(m_renderingEngine.GetDefaultFontShader(), NewText);
	}
	
	public void DrawString(Text nText)
	{
		DrawString(m_renderingEngine.GetDefaultFontShader(), nText);
	}
	
	public void DrawString(Shader FontShader, Text nText)
	{
		m_textRenderer.RenderText(FontShader, nText);
	}
	
	
	public TexturedModel AddTexturedModel(Integer Index, Entity Parent, String ModelPath, String TexturePath)
	{
		TexturedModel model = CoresManager.Asset.CreateModel(Index, ModelPath, TexturePath);
		model.SetParent(Parent);
		m_modelRenderer.AddRenderable(Index, model);
		return model;
	}
	
	public TexturedModel AddTexturedModel(Integer Index, GameComponent Parent, Model MadeModel, String TexturePath)
	{
		TexturedModel model = CoresManager.Asset.CreateModel(Index, MadeModel, TexturePath);
		model.SetParent(Parent);
		m_modelRenderer.AddRenderable(Index, model);
		return model;
	}
	
	public Terrain AddTerrain(Integer Index, TerrainComponent Parent, Vector2f Position)
	{
		return new Terrain(Position.GetX(), Position.GetY());
	}
	
	public Model AddTexturedModel(Integer Index, Entity Parent, Vertex3f[] Vertices, TextureCoordinate[] TexCoords, Normal[] Normals, int[] Indices)
	{
		Model model = CoresManager.Asset.CreateModel(Index, Vertices, TexCoords, Normals, Indices);
		model.SetParent(Parent);
		return model;
	}

	public ComputeShader AddComputeShader(String ShaderPath, String ShaderName, int X, int Y, int Z)
	{
		ComputeShader shader = new ComputeShader(/*ShaderPath, X, Y, Z*/);
		m_computeShaders.put(ShaderName, shader);
		return shader;
	}
	
	public Line AddLine(Integer Index, LineComponent Parent, Vertex2f Start, Vertex2f End, Vector3f Color, int Width)
	{
		Line line = new Line(Start, End, Color, Width);
		line.SetParent(Parent);
		m_lineRenderer.AddRenderable(Index, line);
		return line;
	}
	
	public UITexture AddUI(Integer Index, UIComponent Parent, String TexturePath, Vector2f Position, Scale2f Scale)
	{
		UITexture NewUI = new UITexture(TexturePath, Position, Scale);
		NewUI.SetParent(Parent);
		m_uiRenderer.AddRenderable(Index, NewUI);
		return NewUI;
	}

	public UITexture AddUI(Integer Index, UIComponent Parent, Texture nTexture, Vector2f Position, Scale2f Scale)
	{
		UITexture NewUI = new UITexture(nTexture, Position, Scale);
		NewUI.SetParent(Parent);
		m_uiRenderer.AddRenderable(Index, NewUI);
		return NewUI;
	}

	
	public Line GetLineByIndex(Integer Index)
	{
		return m_lineRenderer.GetRenderables().get(Index);
	}
	
	public UITexture GetUITextureByID(Integer ID)
	{
		return m_uiRenderer.GetRenderables().get(ID);
	}
	
	public ComputeShader GetComputeShaderByName(String Name)
	{
		return m_computeShaders.get(Name);
	}
	
	public TexturedModel GetTexturedModelByID(Integer ID)
	{
		return m_modelRenderer.GetRenderables().get(ID);
	}
	
	public void AddText(Text nText)
	{
		Font font = nText.GetFont();
        TextMeshData data = font.LoadText(nText);
        FontResource Resource = new FontResource();
        Resource.Init(data.GetVertexPositions(), data.GetTextureCoords());
        nText.SetResource(Resource);
        EngineList<Text> textBatch = m_texts.get(font);
        if(textBatch == null)
        {
            textBatch = new EngineList<Text>();
            m_texts.Put(font, textBatch);
        }
        textBatch.Add(nText);
	}
	
	public void AddTextComponent(TextComponent Comp)
	{
		m_textComponents.Add(Comp);
	}
	
	public void ClearGraphic()
	{
		m_computeShaders.clear();
		m_modelRenderer.GetRenderables().clear();
		m_lineRenderer.GetRenderables().clear();
		m_uiRenderer.GetRenderables().clear();
	}
	
	@Override
	public void finalize()
	{
		ClearGraphic();
	}
	
	public HashMap<String, ComputeShader> GetComputeShaders() 
	{
		return m_computeShaders;
	}
	
	public UIRenderer GetUIRenderer()
	{
		return m_uiRenderer;
	}
	
	public void SetRenderingEngine(MainFrameBufferRenderingEngine nRenderingEngine)
	{
		m_renderingEngine = nRenderingEngine;
	}
	
	public MainFrameBufferRenderingEngine GetRenderingEngine()
	{
		if (m_renderingEngine == null)
			throw new IllegalStateException("The RenderingEngine has not been set in Graphic.java yet");
		return m_renderingEngine;
	}

	public EngineMap<Font, EngineList<Text>> GetTexts()
	{
		return m_texts;
	}
	
	public int Size()
	{
		int ResCount = 0;
		
		ResCount += m_computeShaders.size();
		ResCount += m_modelRenderer.GetRenderables().size(); 
		ResCount += m_lineRenderer.GetRenderables().size();  
		ResCount += m_uiRenderer.GetRenderables().size();    

		return ResCount;
	}
}
