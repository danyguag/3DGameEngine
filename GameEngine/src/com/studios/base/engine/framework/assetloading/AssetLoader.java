package com.studios.base.engine.framework.assetloading;

import com.studios.base.engine.framework.containers.util.EngineMap;
import com.studios.base.engine.rendering.data.Normal;
import com.studios.base.engine.rendering.data.TextureCoordinate;
import com.studios.base.engine.rendering.data.Vertex3f;
import com.studios.base.engine.rendering.loaders.ObjModel;
import com.studios.base.engine.rendering.model.Model;
import com.studios.base.engine.rendering.model.TexturedModel;
import com.studios.base.engine.rendering.textures.Texture;

public class AssetLoader
{
	public EngineMap<Integer, TexturedModel> TexturedModels;
	public EngineMap<Integer, Texture> Textures;
	
	public AssetLoader()
	{
		TexturedModels = new EngineMap<Integer, TexturedModel>();
		Textures = new EngineMap<Integer, Texture>();
	}

	public TexturedModel CreateModel(Integer ID, String ModelPath, String TexturePath)
	{
		TexturedModel NewModel = null;
		if (TexturedModels.containsValue(ID))
			NewModel = TexturedModels.get(ID);
		else
		{
			NewModel = new TexturedModel(ObjModel.LoadObjModel(ModelPath), CreateTexture(TexturePath));
			TexturedModels.Put(ID, NewModel);
		}
		
		return NewModel;
	}

	public TexturedModel CreateModel(Integer ID, Model MadeModel, String TexturePath)
	{
		TexturedModel NewModel = null;
		if (TexturedModels.containsValue(ID))
			NewModel = TexturedModels.get(ID);
		else
		{
			NewModel = new TexturedModel(MadeModel, CreateTexture(TexturePath));
			TexturedModels.Put(ID, NewModel);
		}
		
		return NewModel;
	}

	
	public Model CreateModel(Integer ID, Vertex3f[] Vertices, TextureCoordinate[] textureCoords, Normal[] Normals, int[] Indices)
	{
		return new Model(Vertices, textureCoords, Normals, Indices);
	}
	
	public Texture CreateTexture(String TexturePath)
	{
		Texture NewTexture = new Texture(TexturePath);
		Textures.Put(NewTexture.GetTextureID(), NewTexture);
		return NewTexture;
	}
	
	public TexturedModel GetTexturedModel(String Name)
	{
		return TexturedModels.get(Name);
	}
	
	public void AddAsset(String LibraryName)
	{
		
	}
}
