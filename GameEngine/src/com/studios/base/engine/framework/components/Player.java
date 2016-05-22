package com.studios.base.engine.framework.components;

import com.studios.base.engine.framework.threading.Snapshot;
import com.studios.base.engine.framework.transform.TransformType;
import com.studios.base.engine.rendering.shader.Shader;

public class Player extends Entity
{
    public Player(String ModelPath, String TexturePath, String name)
    {
    	super(ModelPath, TexturePath);
        super.Name = name;
    }

    @Override
	public void Init() 
    {
    	super.Init();
	}

    @Override
    public void Render(Shader nShader)
    {
    	super.Render(nShader);
    }
    
    @Override
    public void Update(Snapshot CurrentGameShot)
    {	
    }
    
    @Override
    public TransformType GetTransformType()
    {
    	return TransformType.TRANSFROM_3F;
    }
}
