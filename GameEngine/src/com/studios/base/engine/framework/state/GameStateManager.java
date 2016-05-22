package com.studios.base.engine.framework.state;

import com.studios.base.engine.core.CoreEngine;
import com.studios.base.engine.framework.threading.Snapshot;
import com.studios.base.engine.rendering.MainFrameBufferRenderingEngine;
import com.studios.base.engine.rendering.shader.Shader;

public class GameStateManager 
{
	private int m_gameState = 0;
	
	private GameState m_splashScreen;
	private GameState m_mainMenu;
	private GameState m_characterSelection;
	private MainGameState m_gamePlayerGameStateManager;
	
	private CoreEngine m_coreEngine;
	
	public GameStateManager()
	{
		m_splashScreen 	 	 		 = new GameState("SplashScreen");
		m_mainMenu 			 		 = new GameState("MainMenu");
		m_characterSelection 		 = new GameState("Character Selection");
		m_gamePlayerGameStateManager = new MainGameState();
	}
	
	public void Init(MainFrameBufferRenderingEngine nRenderingEngine)
	{
		m_splashScreen.Init(nRenderingEngine);
		m_mainMenu.Init(nRenderingEngine);
		m_characterSelection.Init(nRenderingEngine);
		m_gamePlayerGameStateManager.Init(nRenderingEngine);
	}
	
	public void Update(Snapshot CurrentGameShot)
	{
		if (m_gameState == 0)
		{
			m_splashScreen.Update(CurrentGameShot);
		}
		else if (m_gameState == 1)
		{
			m_mainMenu.Update(CurrentGameShot);
		}
		else if (m_gameState == 2)
		{
			m_characterSelection.Update(CurrentGameShot);
		}
		else if (m_gameState == 3)
		{
			m_gamePlayerGameStateManager.Update(CurrentGameShot);
		}
	}
	
	public void Render(int CurrentRenderPass, Shader ForwardRenderShader)
	{
		if (m_gameState == 0)
		{
			m_splashScreen.Render3D(CurrentRenderPass, ForwardRenderShader);
		}
		else if (m_gameState == 1)
		{
			m_mainMenu.Render3D(CurrentRenderPass, ForwardRenderShader);
		}
		else if (m_gameState == 2)
		{
			m_characterSelection.Render3D(CurrentRenderPass, ForwardRenderShader);
		}
		else if (m_gameState == 3)
		{
			m_gamePlayerGameStateManager.Render3D(CurrentRenderPass, ForwardRenderShader);
		}
	}

	public void Render2D(Shader LineShader, Shader UIShader, Shader FontShader)
	{
		if (m_gameState == 0)
		{
			m_splashScreen.Render2D(LineShader, UIShader, FontShader);
		}
		else if (m_gameState == 1)
		{
			m_mainMenu.Render2D(LineShader, UIShader, FontShader);
		}
		else if (m_gameState == 2)
		{
			m_characterSelection.Render2D(LineShader, UIShader, FontShader);
		}
		else if (m_gameState == 3)
		{
			m_gamePlayerGameStateManager.Render2D(LineShader, UIShader, FontShader);
		}
	}
	
	public int GetCurrentGameStateInt()
	{
		return m_gameState;
	}
	
	public int GetPreviousGameStateInt()
	{
		return GetCurrentGameStateInt() - 1;
	}
	
	public GameState GetSplashScreenGameState()
	{
		return m_splashScreen;
	}
	
	public GameState GetMainMenuGameState()
	{
		return m_mainMenu;
	}
	
	public GameState GetCharacterSelectionGameState()
	{
		return m_characterSelection;
	}
	
	public MainGameState GetGamePlayGameState()
	{
		return m_gamePlayerGameStateManager;
	}
	
	public CoreEngine GetCoreEngine()
	{
		return m_coreEngine;
	}
	
	public void SetCoreEngine(CoreEngine nCoreEngine)
	{
		m_coreEngine = nCoreEngine;
		m_splashScreen.SetCoreEngine(nCoreEngine);
		m_mainMenu.SetCoreEngine(nCoreEngine);
		m_characterSelection.SetCoreEngine(nCoreEngine);
		m_gamePlayerGameStateManager.SetCoreEngine(nCoreEngine);
		
	}
	
	public GameState GetCurrentGameState()
	{
		if (m_gameState == 0)
			return m_splashScreen;
		else if (m_gameState == 1)
			return m_mainMenu;
		else if (m_gameState == 2)
			return m_characterSelection;
		else if (m_gameState == 3)
		{
			/*
			 * TODO: Create a GetCurrentGameState for the MainGameState
			 */
			return m_gamePlayerGameStateManager.GetChildrenGameState().get(0);
		}
		else 
			return null;
	}
}
