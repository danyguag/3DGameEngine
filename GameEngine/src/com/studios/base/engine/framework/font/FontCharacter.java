package com.studios.base.engine.framework.font;

public class FontCharacter 
{
	private int m_id;
    private double m_xTextureCoord;
    private double m_yTextureCoord;
    private double m_xMaxTextureCoord;
    private double m_yMaxTextureCoord;
    private double m_xOffset;
    private double m_yOffset;
    private double m_sizeX;
    private double m_sizeY;
    private double m_xAdvance;
    
    public FontCharacter(int ID, double XTextureCoord, double YTextureCoord, double XTexSize, double YTexSize,
            double XOffset, double YOffset, double SizeX, double SizeY, double XAdvance) 
    {
        this.m_id = ID;
        this.m_xTextureCoord = XTextureCoord;
        this.m_yTextureCoord = YTextureCoord;
        this.m_xOffset = XOffset;
        this.m_yOffset = YOffset;
        this.m_sizeX = SizeX;
        this.m_sizeY = SizeY;
        this.m_xMaxTextureCoord = XTexSize + XTextureCoord;
        this.m_yMaxTextureCoord = YTexSize + YTextureCoord;
        this.m_xAdvance = XAdvance;
    }
 
    public int GetId() 
    {
        return m_id;
    }
 
    public double GetXTextureCoord() 
    {
        return m_xTextureCoord;
    }
 
    public double GetYTextureCoord() 
    {
        return m_yTextureCoord;
    }
 
    public double GetXMaxTextureCoord() 
    {
        return m_xMaxTextureCoord;
    }
 
    public double GetYMaxTextureCoord() 
    {
        return m_yMaxTextureCoord;
    }
 
    public double GetXOffset() 
    {
        return m_xOffset;
    }
 
    public double GetYOffset() 
    {
        return m_yOffset;
    }
 
    public double GetSizeX() 
    {
        return m_sizeX;
    }
 
    public double GetSizeY() 
    {
        return m_sizeY;
    }
 
    public double GetXAdvance() 
    {
        return m_xAdvance;
    }
}
