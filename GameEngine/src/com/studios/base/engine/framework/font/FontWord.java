package com.studios.base.engine.framework.font;

import com.studios.base.engine.framework.containers.util.EngineList;

public class FontWord 
{
	private EngineList<FontCharacter> m_characters = new EngineList<FontCharacter>();
    private double m_width = 0;
    private double m_fontSize;
    
    public FontWord(double FontSize)
    {
        m_fontSize = FontSize;
    }

    public void AddCharacter(FontCharacter nCharacter)
    {
        m_characters.add(nCharacter);
        m_width += 
        		nCharacter.GetXAdvance() * 
        		m_fontSize;
    }
     
    protected EngineList<FontCharacter> GetCharacters()
    {
        return m_characters;
    }
    
    public double GetWordWidth()
    {
        return m_width;
    }

}
