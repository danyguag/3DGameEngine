package com.studios.base.engine.engine_ui;

import java.awt.Canvas;
import java.awt.Dimension;

public class EngineCanvas extends Canvas
{
	private static final long serialVersionUID = 1L;
	
	public EngineCanvas(int Width, int Height)
	{
		setPreferredSize(new Dimension(Width, Height));
	}
}
