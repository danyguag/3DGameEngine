package com.studios.base.engine.engine_ui;

import java.awt.Canvas;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.studios.base.engine.core.CoreEngine;
import com.studios.base.engine.core.CoresManager;
import com.studios.base.engine.framework.containers.util.EngineList;
import com.studios.base.engine.framework.containers.util.EngineTree;
import com.studios.base.engine.framework.debugging.DebugManager;
import com.studios.base.engine.framework.scenegraph.GameObject;
import com.studios.base.engine.framework.state.GameState;

public class EngineWindow extends JFrame 
{
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private Canvas m_gameFrame;

	private CoreEngine m_game;
	private Canvas Back;
	
	private Container m_gameObjectDisplay;
	private GameState m_currentState;
	private EngineList<JLabel> m_gameObjectLabels = new EngineList<JLabel>();
	
	public EngineWindow() 
	{
		m_gameFrame = new EngineCanvas(1200, 800);
		m_gameFrame.setBounds(360, 5, 1200, 800);
		setTitle("Knights' of the Lord's Realm Engine");
		setSize(1938, 1098);
		contentPane = new JPanel();

		setResizable(true);
		try 
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) 
		{
			DebugManager.PrintException(e);
		}
		contentPane.setLayout(null);		
		
		
		InitBaseLayout();
		
		InitMenuBar();
		InitScene();
	}
	
	private void InitMenuBar()
	{
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent nMouseEvent) 
			{
				Save();
			}
		});
		mnFile.add(mntmSave);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmAddJsonFile = new JMenuItem("Add JSON File");
		mnEdit.add(mntmAddJsonFile);
		
		JMenuItem mntmEditJsonFile = new JMenuItem("Edit JSON File");
		mnEdit.add(mntmEditJsonFile);
		
		JMenuItem mntmRemoveJsonFile = new JMenuItem("Remove JSON File");
		mnEdit.add(mntmRemoveJsonFile);
	}
	
	private void InitBaseLayout()
	{
		m_gameFrame = new Canvas();
		m_gameFrame.setBounds(10, -13, 1200, 800);
		m_gameFrame.setPreferredSize(new Dimension(1200, 800));
		m_gameFrame.setFocusable(true);
		contentPane.add(m_gameFrame);
		
		JButton btnExit = new JButton("Exit");
		btnExit.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent arg0)
			{
				CoresManager.GameRunning = false;
				m_game.interrupt();
				
				DebugManager.ExitWithoutError(100);
			}
		});
		btnExit.setBounds(1697, 0, 223, 75);
		contentPane.add(btnExit);
		
		JSeparator MainButtonsSep = new JSeparator();
		MainButtonsSep.setBounds(1206, 76, 735, 11);
		contentPane.add(MainButtonsSep);
		
		JButton btnSave = new JButton("Save");
		btnSave.setBounds(1474, 0, 223, 75);
		contentPane.add(btnSave);
		
//		JLabel lblScenegraph = DefaultComponentFactory.getInstance().createTitle("SceneGraph");
//		lblScenegraph.setFont(new Font("Times New Roman", Font.PLAIN, 33));
//		lblScenegraph.setBounds(1616, 76, 164, 39);
//		contentPane.add(lblScenegraph);
		
		m_gameObjectDisplay = new Container();
		m_gameObjectDisplay.setBounds(1474, 117, 436, 454);
		contentPane.add(m_gameObjectDisplay);
		m_gameObjectDisplay.setLayout(null);
		
		JButton btnGameobjects = new JButton("GameObjects");
		btnGameobjects.setBounds(320, 0, 111, 25);
		m_gameObjectDisplay.add(btnGameobjects);
		
		JLabel lblRoot = new JLabel("Root");
		lblRoot.setToolTipText("If you click then you will see all of the GameObjects that are currently in the CurrentGameState.");
		lblRoot.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				if (e.getClickCount() > 0)
					SetUpGameObjectTree();
			}
		});
		lblRoot.setBounds(12, 4, 68, 25);
		m_gameObjectDisplay.add(lblRoot);
		
		JSeparator top_separator = new JSeparator();
		top_separator.setBounds(0, 0, 445, 9);
		m_gameObjectDisplay.add(top_separator);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(0, 445, 434, 9);
		m_gameObjectDisplay.add(separator);
		
		JSeparator left_separator = new JSeparator();
		left_separator.setBounds(0, 0, 12, 445);
		m_gameObjectDisplay.add(left_separator);
		left_separator.setOrientation(SwingConstants.VERTICAL);
		
		JSeparator right_separator = new JSeparator();
		right_separator.setOrientation(SwingConstants.VERTICAL);
		right_separator.setBounds(435, 0, 12, 445);
		m_gameObjectDisplay.add(right_separator);
	}
	
	public void InitScene()
	{
		setVisible(true);
		
		setContentPane(contentPane);
	}
	
	public void Save()
	{
		
	}
	
	private void SetUpGameObjectTree()
	{
		for (JLabel GameObjectLabel : m_gameObjectLabels)
		{
			for (int i = 0; i < m_gameObjectDisplay.getComponentCount(); i++)
			{
				if (m_gameObjectDisplay.getComponents()[i] == GameObjectLabel)
					m_gameObjectDisplay.remove(GameObjectLabel);
			}
		}
		
		EngineTree<GameObject> Objects = GetCurrentGameState().GetStateGameObject().GetChildren();
		int YPos = 28;
		
		for (EngineTree<GameObject> Branch : Objects.GetTree())
		{
			JLabel GameObject = new JLabel(Branch.GetSelf().GetName());
			DebugManager.Log(getClass().getSimpleName(), Branch.GetSelf().GetName());
			GameObject.setBounds(55, YPos, 68, 25);
			m_gameObjectDisplay.add(GameObject);
			YPos += 24;
			m_gameObjectDisplay.add(GameObject);
		}
	}
	
	public Canvas GetGameFrame()
	{
		return m_gameFrame;
	}
	
	public void SetGame(CoreEngine Game)
	{
		m_game = Game;
	}
	
	public void StartGame()
	{
		m_game.start();
	}
	
	public Canvas GetBackGroundCanvas()
	{
		return Back;
	}
	
	public JPanel GetContentPane()
	{
		return contentPane;
	}
	
	public GameState GetCurrentGameState()
	{
		return m_currentState;
	}
	
	public void SetGameState(GameState State)
	{	
		m_currentState = State;
		SetUpGameObjectTree();
	}
}
