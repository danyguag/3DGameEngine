var ChatComponent = Java.type('com.studios.base.engine.framework.chat.ChatComponent');
var ServerChatComponent = Java.type('com.studios.base.engine.framework.chat.ServerChatComponent');
var StringBuilder = Java.type('java.lang.StringBuilder');
var FontMessage = Java.type('com.studios.base.engine.framework.font.Message');
var FontText = Java.type('com.studios.base.engine.framework.font.Text');
var Vector2f = Java.type('com.studios.base.engine.framework.math.Vector2f');
var Vector3f = Java.type('com.studios.base.engine.framework.math.Vector3f');
var ClientBootstrap = Java.type('com.studios.base.engine.net.client.ClientBootstrap');
var Packet = Java.type('com.studios.base.engine.net.messaging.Packet');

var INSIDE_START;
var m_line;
var m_oldLine;
var m_start;
var m_newPosition;
var m_insideChatCount;
var m_enteringIntoChat;
var m_outSideOfChatCount;
var m_beenInChatCount;
var m_chatClient;
var m_chatComponent;

var Start = function(CoreEngine, Parent, Graphics)
{
	This = Parent;
	if (This == null)
		DebugManager.Log('ChatClientScript', "The Parent of this Script is NULL");

	m_chatComponent = This.GetComponent(ChatComponent.class);

	INSIDE_START = 5;
	m_line = new StringBuilder();
	m_start = null;
	m_oldLine = new StringBuilder();
	m_newPosition = new Vector2f(.01, .26)
	m_insideChatCount = 0;
	m_outSideOfChatCount = 0;
	m_beenInChatCount = 0;
	m_chatClient = CoreEngine.GetLogicCore().ChatClient;
};

var Update = function(CurrentGameShot)
{
	if (m_enteringIntoChat)
	{
		if (m_chatComponent == null)
		{
			DebugManager.Log("ChatClientScript", "ChatComponent is NULL");
			return;
		}

		m_oldLine = new StringBuilder(m_line);
		var Append = true;
		var Render = true;

		if (Keyboard.next())
		{
			var KeyCode = Keyboard.getEventKey();
			
	        if (m_line.length() > 0 && m_insideChatCount > INSIDE_START && KeyCode == Keyboard.KEY_RETURN)
	        {
	        	var ChatPacket = new Packet("chat_message", 2);
	        	ChatPacket.AddEntry("player_name", m_chatComponent.GetUsername());
	        	ChatPacket.AddEntry("chat_message_string", m_line.toString());
	        	m_chatClient.Write(ChatPacket);
	        	m_chatComponent.SetRender(false);
	        	m_line.setLength(0);
	        	m_chatComponent.Remove(m_start);
	        	m_start = m_chatComponent.AddMessage(m_chatComponent.GetUsername() + ": ", m_newPosition, new Vector3f(1,1,1), 1);
	        	m_enteringIntoChat = false;
	        	m_insideChatCount = 0;

	        	var ServerChatComp = This.GetComponent(ServerChatComponent.class);

			    if (ServerChatComp.GetTexts().Length() > 0)
			    {
			    	for (var ServerChatIndex = 0; ServerChatIndex < ServerChatComp.GetTexts().Length(); ServerChatIndex++)
				    {
				    	ServerChatComp.GetTexts().GetValues().get(ServerChatIndex).SetRender(false);
				    }
			    }

	        	return;
	        }
	        	
	    	if (KeyCode == Keyboard.KEY_RETURN)
	        {
	        	++m_insideChatCount;
	        	return;
	        }
	        	
	        if (KeyCode == Keyboard.KEY_ESCAPE)
	        {
	        	m_chatComponent.Remove(m_start);
	        	m_chatComponent.SetRender(false);                	
	        	m_line.setLength(0);
	        	m_enteringIntoChat = false;
	        	return;
	        }
			
			if (KeyCode == Keyboard.KEY_TAB)
				return;
			// DebugManager.Log(getClass().getSimpleName(), "In ChatLoop: " + m_insideChatCount);
	        var KeyHit = Keyboard.getEventCharacter();
			
	        if (Keyboard.isKeyDown(Keyboard.KEY_BACK))
			{
				if (m_line.length() > 0)
				{
					Append = false;
					if (m_line.length() == 1)
					{
						m_line.delete(0, m_line.length());
						
						if (m_start != null)
							m_chatComponent.Remove(m_start);
						if (m_line.length() > 0)
						{
							m_line.setLength(0);
							m_start = m_chatComponent.AddMessage(m_chatComponent.GetUsername() + ": ", m_newPosition, new Vector3f(1, 1, 1), 1);
						}
						Render = false;
						++m_insideChatCount;
						return;
					}
					m_line.setLength(m_line.length() - 1);
					
					if (m_start != null)
						m_chatComponent.Remove(m_start);
					if (m_line.length() > 0)
					{
						m_start = m_chatComponent.AddMessage(m_chatComponent.GetUsername() + ": " + m_line.toString(), m_newPosition, new Vector3f(1, 1, 1), 1);
					}
				}
				++m_insideChatCount;
				return;
			}
			
			if (KeyCode == Keyboard.KEY_BACK)
			{
				return;
			}
			
			if (KeyHit == '\0')
			{
				if (KeyCode != Keyboard.KEY_LSHIFT && KeyCode != Keyboard.KEY_RSHIFT && KeyCode != Keyboard.KEY_RETURN)
				{
					++m_insideChatCount;
	            	return;
	            }
			}
			
			if (m_insideChatCount == 0)
			{
				m_line.setLength(0);
				m_start = m_chatComponent.AddMessage(m_chatComponent.GetUsername() + ": ", m_newPosition, new Vector3f(1, 1, 1), 1);
				++m_insideChatCount;
				return;
			}
			
			if (m_insideChatCount > INSIDE_START && Append && KeyHit != '\0')
			{
				var LineLength = m_line.length();
				if (LineLength < 50)
				{
					m_line.append(KeyHit);
					if (Render)
					{
						Render = LineLength != m_line.length();
					}
				}
			}
			
			if (m_line.length() > 0 && Render && Append)
			{
				if (!m_line.toString().equals(m_oldLine.toString()))
				{
					if (m_start != null)
						m_chatComponent.RemoveAndDestroy(m_start);
					if (m_line.length() > 0)
					{
						m_start = m_chatComponent.AddMessage(m_chatComponent.GetUsername() + ":" + (m_line.toString().length() > 0 ? " " + m_line.toString() : " "), m_newPosition, new Vector3f(1, 1, 1), 1);
					}
				}
			}
			if (m_line.length() == 0 && Render && Append)
			{
				if (m_start != null)
					m_chatComponent.RemoveAndDestroy(m_start);
				if (m_line.length() > 0)
				{
					m_start = m_chatComponent.AddMessage(m_chatComponent.GetUsername() + ": ", m_newPosition, new Vector3f(1, 1, 1), 1);
				}
				
			}
		}
		++m_insideChatCount;
	}
	else 
	{
		if (Keyboard.next()){}
		
		m_enteringIntoChat = false;
		m_insideChatCount = 0;
		++m_outSideOfChatCount;
		if (Keyboard.isKeyDown(Keyboard.KEY_RETURN) && !m_enteringIntoChat && m_outSideOfChatCount > 1000)
		{
			m_outSideOfChatCount = 0;
			m_insideChatCount = 0;
			m_enteringIntoChat = true;
			m_chatComponent.SetRender(true);
			
			m_chatComponent.Remove(m_start);
	    	m_start = m_chatComponent.AddMessage(m_chatComponent.GetUsername() + ": ", m_newPosition, new Vector3f(1,1,1), 1);
		
			var ServerChatComp = This.GetComponent(ServerChatComponent.class);

		    if (ServerChatComp.GetTexts().Length() > 0)
		    {
		    	for (var ServerChatIndex = 0; ServerChatIndex < ServerChatComp.GetTexts().Length(); ServerChatIndex++)
			    {
			    	ServerChatComp.GetTexts().GetValues().get(ServerChatIndex).SetRender(true);
			    }
		    }
		}
	}
};