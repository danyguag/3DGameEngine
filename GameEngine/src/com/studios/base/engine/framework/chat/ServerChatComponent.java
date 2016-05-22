package com.studios.base.engine.framework.chat;

import com.studios.base.engine.framework.components.TextComponent;
import com.studios.base.engine.framework.containers.util.EngineMap;
import com.studios.base.engine.framework.font.Font;
import com.studios.base.engine.framework.font.Message;
import com.studios.base.engine.framework.font.Text;
import com.studios.base.engine.framework.math.Vector2f;
import com.studios.base.engine.framework.threading.Snapshot;

public class ServerChatComponent extends TextComponent
{
	public static final Vector2f FIRST_POSITION = new Vector2f(.01f, .21f);
	public static final Vector2f SECOND_POSITION = new Vector2f(.01f, .18f);
	public static final Vector2f THIRD_POSITION = new Vector2f(.01f, .15f);
	public static final Vector2f FOURTH_POSITION = new Vector2f(.01f, .12f);
	public static final Vector2f FIFTH_POSITION = new Vector2f(.01f, .09f);
	public static final Vector2f SIXTH_POSITION = new Vector2f(.01f, .06f);
	public static final Vector2f SEVENTH_POSITION = new Vector2f(.01f, .03f);
	public static final Vector2f EIGHTH_POSITION = new Vector2f(.01f, 0f);
	
	public ServerChatComponent(Font nFont, float MaxLineLength, boolean Centered) 
	{
		super(nFont, MaxLineLength, Centered);
	}
	
	@Override
	public void Update(Snapshot CurrentGameShot)
	{
		super.Update(CurrentGameShot);
		if (CurrentGameShot.GetCoreEngine().GetLogicCore().ChatHandler.HashChanged && CurrentGameShot.GetCoreEngine().GetLogicCore().ChatHandler.GetMessages().size() > 0)
		{
			GetCoreEngine().GetLogicCore().ChatHandler.HashChanged = false;
			AddMessage(0, GetCoreEngine().GetLogicCore().ChatHandler.GetMessages().get(0));
			GetCoreEngine().GetLogicCore().ChatHandler.GetMessages().clear();
		}
	}
	
	public void AddMessage(int PASS_TEST, Message nMessage)
	{
		for (Text ChatMessage : m_texts.GetValues())
		{
			boolean NotSet = true;
			if (ChatMessage.GetPosition().Equals(FIRST_POSITION))
			{
				NotSet = false;
				ChatMessage.SetPosition(SECOND_POSITION);
			}
			if (ChatMessage.GetPosition().Equals(SECOND_POSITION) && NotSet)
			{
				NotSet = false;
				ChatMessage.SetPosition(THIRD_POSITION);
			}
			if (ChatMessage.GetPosition().Equals(THIRD_POSITION) && NotSet)
			{
				NotSet = false;
				ChatMessage.SetPosition(FOURTH_POSITION);
			}
			if (ChatMessage.GetPosition().Equals(FOURTH_POSITION) && NotSet)
			{
				NotSet = false;
				ChatMessage.SetPosition(FIFTH_POSITION);
			}
			if (ChatMessage.GetPosition().Equals(FIFTH_POSITION) && NotSet)
			{
				NotSet = false;
				ChatMessage.SetPosition(SIXTH_POSITION);
			}
			if (ChatMessage.GetPosition().Equals(SIXTH_POSITION) && NotSet)
			{
				NotSet = false;
				ChatMessage.SetPosition(SEVENTH_POSITION);
			}
			if (ChatMessage.GetPosition().Equals(SEVENTH_POSITION) && NotSet)
			{
				NotSet = false;
				ChatMessage.SetPosition(EIGHTH_POSITION);
			}
			if (ChatMessage.GetPosition().Equals(EIGHTH_POSITION) && NotSet)
			{
				NotSet = false;
				ChatMessage.SetRender(false);
			}
			
		}
		super.AddMessage(nMessage);
	}

	public EngineMap<Message, Text> GetTexts()
	{
		return m_texts;
	}
}
