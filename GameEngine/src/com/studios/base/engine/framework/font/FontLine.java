package com.studios.base.engine.framework.font;

import com.studios.base.engine.framework.containers.util.EngineList;

public class FontLine 
{
	private double m_maxLength;
    private double m_spaceSize;
 
    private EngineList<FontWord> m_words = new EngineList<FontWord>();
    private double m_currentLineLength = 0;
 
    public FontLine(double SpaceWidth, double FontSize, double MaxLength) 
    {
        m_spaceSize = SpaceWidth * FontSize;
        m_maxLength = MaxLength;
    }
 
    public boolean AttemptToAddWord(FontWord Word) 
    {
        double AdditionalLength = Word.GetWordWidth();
        AdditionalLength += !m_words.isEmpty() ? m_spaceSize : 0;
        if (m_currentLineLength + AdditionalLength <= m_maxLength) 
        {
            m_words.Add(Word);
            m_currentLineLength += AdditionalLength;
            return true;
        } 
        else 
        {
            return false;
        }
    }
 
    public double GetMaxLength() 
    {
        return m_maxLength;
    }
 
    public double GetLineLength() 
    {
        return m_currentLineLength;
    }

    public EngineList<FontWord> GetWords() 
    {
        return m_words;
    }

}
