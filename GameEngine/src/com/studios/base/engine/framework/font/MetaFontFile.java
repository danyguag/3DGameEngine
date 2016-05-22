package com.studios.base.engine.framework.font;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.studios.base.engine.core.CoreEngine;
import com.studios.base.engine.framework.containers.util.EngineMap;

public class MetaFontFile
{
	private static final int PAD_TOP = 0;
    private static final int PAD_LEFT = 1;
    private static final int PAD_BOTTOM = 2;
    private static final int PAD_RIGHT = 3;
 
    private static final int DESIRED_PADDING = 3;
 
    private static final String SPLITTER = " ";
    private static final String NUMBER_SEPARATOR = ",";
 
    private double m_aspectRatio;
 
    private double m_verticalPerPixelSize;
    private double m_horizontalPerPixelSize;
    private double m_spaceWidth;
    private int[] m_padding;
    private int m_paddingWidth;
    private int m_paddingHeight;
 
    private EngineMap<Integer, FontCharacter> m_metaData = new EngineMap<Integer, FontCharacter>();
 
    private BufferedReader m_reader;
    private EngineMap<String, String> m_values = new EngineMap<String, String>();
 
    protected MetaFontFile(CoreEngine Engine, File file) 
    {
        m_aspectRatio = (double) Engine.GetWindowEngine().GetWindow().GetWidth() / (double) Engine.GetWindowEngine().GetWindow().GetHeight();
        OpenFile(file);
        LoadPaddingData();
        LoadLineSizes();
        int imageWidth = getValueOfVariable("scaleW");
        LoadCharacterData(imageWidth);
        Close();
    }
 
    protected double GetSpaceWidth() 
    {
        return m_spaceWidth;
    }
 
    protected FontCharacter GetCharacter(int ascii) 
    {
        return m_metaData.get(ascii);
    }
 
    private boolean ProcessNextLine() 
    {
        m_values.clear();
        String line = null;
        try 
        {
            line = m_reader.readLine();
        }
        catch (IOException e1) 
        {
        }
        if (line == null) 
            return false;
        for (String part : line.split(SPLITTER)) 
        {
            String[] valuePairs = part.split("=");
            if (valuePairs.length == 2) 
                m_values.put(valuePairs[0], valuePairs[1]);
        }
        return true;
    }
 
    private int getValueOfVariable(String variable) 
    {
        return Integer.parseInt(m_values.get(variable));
    }
 
    private int[] getValuesOfVariable(String variable) 
    {
        String[] numbers = m_values.get(variable).split(NUMBER_SEPARATOR);
        int[] ActualValues = new int[numbers.length];
        for (int i = 0; i < ActualValues.length; i++) 
        {
            ActualValues[i] = Integer.parseInt(numbers[i]);
        }
        return ActualValues;
    }
 
    private void Close() 
    {
        try 
        {
            m_reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
 
    private void OpenFile(File file) 
    {
        try 
        {
            m_reader = new BufferedReader(new FileReader(file));
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            System.err.println("Couldn't read font meta file!");
        }
    }
    
    private void LoadPaddingData() 
    {
        ProcessNextLine();
        m_padding = getValuesOfVariable("padding");
        m_paddingWidth = m_padding[PAD_LEFT] + m_padding[PAD_RIGHT];
        m_paddingHeight = m_padding[PAD_TOP] + m_padding[PAD_BOTTOM];
    }
 
    private void LoadLineSizes() 
    {
        ProcessNextLine();
        int lineHeightPixels = getValueOfVariable("lineHeight") - m_paddingHeight;
        m_verticalPerPixelSize = TextMeshDataGen.LINE_HEIGHT / (double) lineHeightPixels;
        m_horizontalPerPixelSize = m_verticalPerPixelSize / m_aspectRatio;
    }
 
    private void LoadCharacterData(int imageWidth) 
    {
        ProcessNextLine();
        ProcessNextLine();
        while (ProcessNextLine())
        {
            FontCharacter c = LoadCharacter(imageWidth);
            if (c != null) {
                m_metaData.Put(c.GetId(), c);
            }
        }
    }

    private FontCharacter LoadCharacter(int imageSize)
    {
        int ID = getValueOfVariable("id");
        if (ID == TextMeshDataGen.SPACE_ASCII) 
        {
            this.m_spaceWidth = (getValueOfVariable("xadvance") - m_paddingWidth) * m_horizontalPerPixelSize;
            return null;
        }
        double XTex = ((double) getValueOfVariable("x") + (m_padding[PAD_LEFT] - DESIRED_PADDING)) / imageSize;
        double YTex = ((double) getValueOfVariable("y") + (m_padding[PAD_TOP] - DESIRED_PADDING)) / imageSize;
        int Width = getValueOfVariable("width") - (m_paddingWidth - (2 * DESIRED_PADDING));
        int Height = getValueOfVariable("height") - ((m_paddingHeight) - (2 * DESIRED_PADDING));
        double QuadWidth = Width * m_horizontalPerPixelSize;
        double QuadHeight = Height * m_verticalPerPixelSize;
        double XTexSize = (double) Width / imageSize;
        double YTexSize = (double) Height / imageSize;
        double XOff = (getValueOfVariable("xoffset") + m_padding[PAD_LEFT] - DESIRED_PADDING) * m_horizontalPerPixelSize;
        double YOff = (getValueOfVariable("yoffset") + (m_padding[PAD_TOP] - DESIRED_PADDING)) * m_verticalPerPixelSize;
        double XAdvance = (getValueOfVariable("xadvance") - m_paddingWidth) * m_horizontalPerPixelSize;
        return new FontCharacter(ID, XTex, YTex, XTexSize, YTexSize, XOff, YOff, QuadWidth, QuadHeight, XAdvance);
    }

}
