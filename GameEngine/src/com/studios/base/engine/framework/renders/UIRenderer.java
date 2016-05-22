package com.studios.base.engine.framework.renders;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_EQUAL;
import static org.lwjgl.opengl.GL11.GL_LESS;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDepthFunc;
import static org.lwjgl.opengl.GL11.glDepthMask;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import com.studios.base.engine.framework.resourceManagement.UIModelInitializer;
import com.studios.base.engine.rendering.model.UITexture;
import com.studios.base.engine.rendering.shader.Shader;

public class UIRenderer extends Renderer<UITexture>
{
	private final UIModelInitializer m_uiModel;
	
	public UIRenderer()
	{
		m_uiModel = new UIModelInitializer();
	}
	
	@Override
	public void Render(Shader UIShader)
	{
		glBindVertexArray(m_uiModel.GetVaoID());
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glDepthMask(false);
		glDepthFunc(GL_EQUAL);
		glDisable(GL_DEPTH_TEST);
		for (UITexture ui : GetRenderables().values())
		{
			if (ui.ShouldRender())
			{
				ui.GetGameComponentParent().Render(UIShader);
				glDrawArrays(ui.GetDrawMode(), 0, ui.GetVertexCount());
			}
		}
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
		glDepthMask(true);
		glDisable(GL_BLEND);
	}
}
