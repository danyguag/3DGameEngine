package com.studios.base.engine.framework.components;

import com.studios.base.engine.core.CoreEngine;
import com.studios.base.engine.framework.graphics.Graphics;
import com.studios.base.engine.framework.scenegraph.GameObject;
import com.studios.base.engine.framework.threading.Snapshot;
import com.studios.base.engine.framework.transform.Transform2f;
import com.studios.base.engine.framework.transform.Transform3f;
import com.studios.base.engine.framework.transform.TransformType;
import com.studios.base.engine.rendering.shader.Shader;

public abstract class GameComponent
{
	protected GameObject m_parent;
    protected String Name;

    public String GetName()
    {
    	return Name;
    }

    public Graphics GetGraphics()
    {
    	if (m_parent == null)
    		throw new IllegalStateException("Parent is null");
    	if (m_parent.GetGraphics() == null)
    		throw new IllegalStateException("Parent's Graphics is null");
    	return m_parent.GetGraphics();
    }	
    
    public GameObject GetGameObjectParent()
    {
    	return m_parent;
    }
    
    public CoreEngine GetCoreEngine()
    {
    	return m_parent.GetCoreEngine();
    }
    
    public Transform3f GetTransform3f()
    {
        return m_parent.GetTransform3f();
    }

    public Transform2f GetTransform2f()
    {
    	return m_parent.GetTransform2f();
    }
    
    public abstract void Init();
    public abstract void Render(Shader nShader);
    public abstract void Update(Snapshot CurrentGameState);
    public abstract TransformType GetTransformType();

    public void SetParent(GameObject parent)
    {
        m_parent = parent;
    }
}
