package com.studios.base.engine.framework.components;

import com.studios.base.engine.core.CoresManager;
import com.studios.base.engine.framework.math.Vector3f;
import com.studios.base.engine.framework.threading.Snapshot;
import com.studios.base.engine.framework.transform.TransformType;
import com.studios.base.engine.rendering.data.Vertex2f;
import com.studios.base.engine.rendering.model.Line;
import com.studios.base.engine.rendering.shader.Shader;

public class LineComponent extends GameComponent
{
	private Line m_line;

	private Vertex2f m_start; 
	private Vertex2f m_end;
	private Vector3f m_color; 
	private int m_width;
	
	protected int m_lineID;
	
	public LineComponent(Vertex2f Start, Vertex2f End, Vector3f Color, int Width)
	{
		m_lineID = CoresManager.IDGen.Line.GetNextID();
		super.Name = "LineComponent" + m_lineID;
		m_start = Start;
		m_end = End;
		m_color = Color;
		m_width = Width;
	}
	
	@Override
	public void Init() 
	{
		m_line = GetGraphics().AddLine(m_lineID, this, m_start, m_end, m_color, m_width);
	}

	@Override
	public void Render(Shader nShader)
	{
		nShader.Bind();
		nShader.SetUniform("color", 
				GetGraphics().GetLineByIndex(m_lineID).GetColor());

	}
	
	@Override
	public void Update(Snapshot CurrentGameShot) 
	{
		
	}
	
	public int GetLineID()
	{
		return m_lineID;
	}
	
	public void TEMP_SET_LINE_ID(int ID)
	{
		m_lineID = ID;
	}
	
	public Line GetLine()
	{
		return m_line;
	}
	
	public void Destroy()
	{
		CoresManager.IDGen.Line.AddIDToRemovedList(m_lineID);		
	}
	
	@Override
    protected void finalize() throws Throwable 
    {
    	super.finalize();
    	Destroy();
    }

	@Override
	public TransformType GetTransformType() 
	{
		return TransformType.TRANSFROM_2F;
	}
}
