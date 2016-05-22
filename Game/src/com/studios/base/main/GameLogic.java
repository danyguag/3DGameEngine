package com.studios.base.main;

import com.studios.base.engine.core.LogicCore;
import com.studios.base.engine.framework.chat.ChatClientHandler;
import com.studios.base.engine.framework.chat.ChatComponent;
import com.studios.base.engine.framework.chat.ServerChatComponent;
import com.studios.base.engine.framework.components.JSON;
import com.studios.base.engine.framework.font.Font;
import com.studios.base.engine.framework.scenegraph.GameObject;
import com.studios.base.engine.net.client.ClientBootstrap;
import com.studios.base.engine.rendering.RenderOptions;
import com.studios.base.engine.script.EngineScriptMethods;
import com.studios.base.engine.script.JSScript;

public class GameLogic extends LogicCore 
{

	@Override
	public RenderOptions GetOptions() 
	{
		RenderOptions Render = new RenderOptions(m_coreEngine.GetFrameBufferObject().GetDefaultUIShader(), m_coreEngine.GetFrameBufferObject().GetDefaultLineShader(), m_coreEngine.GetFrameBufferObject().GetDefaultFontShader());
		Render.SetAmbient(.15f, .15f, .15f);
		
		return Render;
	}

	@Override
	public void Init() 
	{
//		AddScript(new JSScript("JSONScript"));
		AddJSON(new JSON("main_objects"));	
//		AddJSON(new JSON("main_ui"));
		
		GameObject CameraGameObejct = m_coreEngine.GetGameStateManager().GetCurrentGameState().GetStateGameObject().GetChildByName("Camera");
		GameObject PlayerGameObejct = m_coreEngine.GetGameStateManager().GetCurrentGameState().GetStateGameObject().GetChildByName("Player");
		
		PlayerGameObejct.AddScript(new JSScript("PlayerMovementScript"), EngineScriptMethods.class);
		CameraGameObejct.AddScript(new JSScript("CameraMovementScript"), EngineScriptMethods.class);
		
		ChatClient = new ClientBootstrap(6000, "192.168.0.3", 40505);
		ChatHandler = new ChatClientHandler();
		ChatClient.Handler(ChatHandler);

		
		
		GameObject obj = new GameObject("Chat");
		ServerChatComponent ServerChat = new ServerChatComponent(Font.ARIAL(m_coreEngine), 1f, false);
		ChatComponent Chat = new ChatComponent("danyguag", Font.ARIAL(m_coreEngine), 1f);
		obj.AddComponent(Chat);
		obj.AddScript(new JSScript("ChatClientScript"), EngineScriptMethods.class);
		
		obj.AddComponent(ServerChat);
		AddChildToCurrentState(obj);
	}

	@Override
	public void CleanUp() 
	{
	}
}
