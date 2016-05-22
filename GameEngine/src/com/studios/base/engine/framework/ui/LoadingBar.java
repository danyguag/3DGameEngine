package com.studios.base.engine.framework.ui;

import com.studios.base.engine.framework.components.UIComponent;
import com.studios.base.engine.framework.debugging.DebugManager;
import com.studios.base.engine.framework.math.Scale2f;
import com.studios.base.engine.framework.math.Vector2f;
import com.studios.base.engine.framework.threading.Snapshot;
import com.studios.base.engine.framework.ui.loading.LoadingTask;
import com.studios.base.engine.rendering.textures.Texture;

public class LoadingBar extends UIComponent 
{
	private double m_percent;
//	private Texture m_texture;
	private LoadingTask m_task;
	
	public LoadingBar(Texture nTexture, Vector2f Position, Scale2f Scale, LoadingTask Task) 
	{
		super("", Position, Scale);
//		m_texture = nTexture;
		m_percent = 0;
		m_task = Task;
	}

	@Override
	public void Init() 
	{
		super.Init();
	}
	
	private boolean told = false;
	
	@Override
	public void Update(Snapshot CurrentGameState) 
	{
		if (LoadingComplete())
		{
			if (!told)
			{
				DebugManager.Log("LoadingBar", "LoadingBar is complete");
				told = true;
			}
			m_percent = 100;
		}
		else
		{
			m_percent = m_task.Update(m_percent);
			m_scale.SetX((float) (m_percent / 200));
		}
		
	}
	
	public boolean LoadingComplete()
	{
		return m_percent >= 100;
	}
}
