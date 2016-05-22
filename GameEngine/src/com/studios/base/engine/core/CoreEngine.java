package com.studios.base.engine.core;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.studios.base.engine.core.util.Time;
import com.studios.base.engine.framework.debugging.DebugManager;
import com.studios.base.engine.framework.game.Game;
import com.studios.base.engine.framework.state.GameStateManager;
import com.studios.base.engine.framework.threading.Snapshot;
import com.studios.base.engine.framework.threading.ThreadQueue;
import com.studios.base.engine.framework.threading.WorkerThread;
import com.studios.base.engine.physics.PhysicsEngine;
import com.studios.base.engine.rendering.Dimension;
import com.studios.base.engine.rendering.MainFrameBufferRenderingEngine;
import com.studios.base.engine.rendering.WindowEngine;

public class CoreEngine extends Thread implements Engine 
{
	private double 	m_frameCap;
	private LogicCore m_logic;
	private WindowEngine m_window;
	private MainFrameBufferRenderingEngine m_renderingEngine;
	private boolean m_cleanedUp;
	
	private GameStateManager m_gameStateManager;
	private ThreadQueue<WorkerThread> m_mainThreadQueue;
	
	private PhysicsEngine m_physicsisEngine;
	
	private Dimension m_windowDim;
	private String m_title;
	
	public CoreEngine(double framerate, LogicCore logic)
	{
		setPriority(MAX_PRIORITY);
		m_mainThreadQueue = new ThreadQueue<WorkerThread>();
		m_frameCap = 1.0f / framerate;
		m_gameStateManager = new GameStateManager();
		m_gameStateManager.SetCoreEngine(this);
		m_logic = logic;
		m_physicsisEngine = new PhysicsEngine();
		m_physicsisEngine.start();
		m_logic.SetCoreEngine(this);
	}

    public void CreateDisplay(Dimension WindowDim, String Title)
    {
    	m_windowDim = WindowDim;
    	m_title = Title;
    }
    
    @Override 
    public void run()
    {
    	m_cleanedUp = false;
    	
    	m_window = new WindowEngine(m_windowDim, m_title);
    	m_window.Create();
    	
    	int Frames = 0;
		int frameCounter = 0;
		
		final double frameTime = m_frameCap;
		
		long lastTime = Time.GetTime();
		double unprocessedTime = 0;

		m_renderingEngine = new MainFrameBufferRenderingEngine(m_window.GetWindow().GetWidth(), m_window.GetWindow().GetHeight());
		
		m_logic.Init();
		m_renderingEngine.Init(m_logic.GetOptions());
		
		m_gameStateManager.Init(m_renderingEngine);
		
		m_window.InitMainRenderingEngine(m_renderingEngine);
		
		while (!isInterrupted() && CoresManager.GameRunning && !m_cleanedUp)
		{
			boolean render = false;
			
			long startTime = Time.GetTime();
			long passedTime = startTime - lastTime;
			lastTime = startTime;
				
			unprocessedTime += passedTime / (double) Time.SECOND;
			frameCounter += passedTime;
				
			while (unprocessedTime > frameTime && !m_cleanedUp)
			{
				unprocessedTime -= frameTime;
				render = true;
				Snapshot UpdatedSnapshot = new Snapshot(this);
				UpdatedSnapshot.Init(m_gameStateManager.GetCurrentGameState().GetStateGameObject(), (float) unprocessedTime);

				if (Display.isCloseRequested())
					Stop();

				if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
				{
					CoresManager.GameRunning = false;
					interrupt();
				}
				
				m_gameStateManager.Update(UpdatedSnapshot);
				
				if (frameCounter >= Time.SECOND && !m_cleanedUp)
				{
					if (Game.showFrames)
						DebugManager.Log("FrameCount", Frames);
					Frames = 0;
					frameCounter = 0;
				}
			}

			if (render && !m_cleanedUp)
			{
				Render();
				Frames++;
			}
			else
			{
				try 
				{
					Thread.sleep(1);
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace(CoresManager.LoggerStream);
				}
			}
		}
		CleanUp();
	}
	
	@Override
	public void Stop()
	{
		interrupt();
	}
	
	@Override
	public void Update()
	{
	}

	@Override
	public void Render()
	{
		m_window.Render(m_gameStateManager, m_logic.GetLights());
	}

	@Override
	public void CleanUp()
	{
		m_logic.CleanUp();
		m_window.CleanUp();
		m_cleanedUp = true;
	}
	
	public void AddWorkerThread(WorkerThread Thread)
	{
		m_mainThreadQueue.AddThread(Thread);
	}
	
	public LogicCore GetLogicCore()
	{
		return m_logic;
	}
	
	public MainFrameBufferRenderingEngine GetFrameBufferObject()
	{
		return m_renderingEngine;
	}
	
	public GameStateManager GetGameStateManager()
	{
		return m_gameStateManager;
	}
	
	public ThreadQueue<WorkerThread> GetThreadQueue()
	{
		return m_mainThreadQueue;
	}

	public WindowEngine GetWindowEngine()
	{
		return m_window;
	}
	
	@Override
	public void Start() 
	{
		start();
	}
	
	public PhysicsEngine GetPhysicsEngine()
	{
		return m_physicsisEngine;
	}
}
