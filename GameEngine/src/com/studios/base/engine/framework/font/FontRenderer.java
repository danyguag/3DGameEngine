package com.studios.base.engine.framework.font;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import com.studios.base.engine.framework.containers.util.EngineList;
import com.studios.base.engine.framework.containers.util.EngineMap;
import com.studios.base.engine.framework.renders.BaseRenderable;
import com.studios.base.engine.framework.renders.Renderer;
import com.studios.base.engine.rendering.shader.Shader;

public class FontRenderer extends Renderer<BaseRenderable>
{
    public void Render(Shader FontShader, EngineMap<Font, EngineList<Text>> texts)
    {
    	FontShader.Bind();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glDisable(GL_DEPTH_TEST);

        for(Font Font : texts.keySet())
        {
            Font.GetFontTexture().Bind(0);
            for(Text Text : texts.get(Font))
            {
            	if (Text.ShouldRender())
            	{
                    glBindVertexArray(Text.GetResource().GetVaoID());
                    FontShader.SetUniform("color", Text.GetColor());
                    FontShader.SetUniform("transform", Text.GetPosition());
                    glDrawArrays(GL_TRIANGLES, 0, Text.GetVertexCount());
            	}
            }
        }
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);    
    }
    
    public void RenderText(Shader FontShader, Text nText)
    {
    	if (nText.ShouldRender())
    	{
    		FontShader.Bind();
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glDisable(GL_DEPTH_TEST);
            nText.GetFont().GetFontTexture().Bind(0);
            glBindVertexArray(nText.
            		GetResource().
            		GetVaoID());
            FontShader.SetUniform("color", nText.GetColor());
            FontShader.SetUniform("transform", nText.GetPosition());
            glDrawArrays(GL_TRIANGLES, 0, nText.GetVertexCount());
            glDisable(GL_BLEND);
            glEnable(GL_DEPTH_TEST);
    	}
    }
}
