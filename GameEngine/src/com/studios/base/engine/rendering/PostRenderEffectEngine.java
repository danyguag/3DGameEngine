package com.studios.base.engine.rendering;

import com.studios.base.engine.framework.containers.util.EngineList;
import com.studios.base.engine.framework.state.GameStateManager;
import com.studios.base.engine.rendering.lighting.Light;

public class PostRenderEffectEngine
{
	public int Render(MainFrameBufferRenderingEngine MainRenderingEngine, EngineList<FrameBufferRenderingEngine> OtherFrameBuffers, GameStateManager State, EngineList<Light> Lights)
	{
		boolean RenderEffects = OtherFrameBuffers.size() > 0;
		
		if (!RenderEffects)
			return MainRenderingEngine.Render(State, Lights);
		return -1;
	}
}
