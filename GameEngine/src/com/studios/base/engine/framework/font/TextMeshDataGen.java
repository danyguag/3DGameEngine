package com.studios.base.engine.framework.font;

import java.io.File;

import com.studios.base.engine.core.CoreEngine;
import com.studios.base.engine.framework.containers.util.EngineList;
import com.studios.base.engine.rendering.data.TextureCoordinate;
import com.studios.base.engine.rendering.data.Vertex2f;

public class TextMeshDataGen 
{
	public static final double LINE_HEIGHT = 0.03f;
    public static final int SPACE_ASCII = 32;
 
    private MetaFontFile metaData;
 
    public TextMeshDataGen(CoreEngine Engine, File MetaFontFile) 
    {
        metaData = new MetaFontFile(Engine, MetaFontFile);
    }
 
    public TextMeshData CreateTextMesh(Text Text) 
    {
        EngineList<FontLine> lines = createStructure(Text);
        TextMeshData data = createQuadVertices(Text, lines);
        return data;
    }
 
    private EngineList<FontLine> createStructure(Text text)
    {
        char[] chars = text.GetTextString().toCharArray();
        EngineList<FontLine> lines = new EngineList<FontLine>();
        FontLine currentLine = new FontLine(metaData.GetSpaceWidth(), text.GetFontSize(), text.GetMaxLineSize());
        FontWord currentWord = new FontWord(text.GetFontSize());
        for (char c : chars) 
        {
            int ascii = (int) c;
            if (ascii == SPACE_ASCII) 
            {
                boolean Added = currentLine.AttemptToAddWord(currentWord);
                if (!Added) 
                {
                    lines.Add(currentLine);
                    currentLine = new FontLine(metaData.GetSpaceWidth(), text.GetFontSize(), text.GetMaxLineSize());
                    currentLine.AttemptToAddWord(currentWord);
                }
                currentWord = new FontWord(text.GetFontSize());
                continue;
            }
            FontCharacter character = metaData.GetCharacter(ascii);
            currentWord.AddCharacter(character);
        }
        CompleteStructure(lines, currentLine, currentWord, text);
        return lines;
    }
 
    private void CompleteStructure(EngineList<FontLine> Lines, FontLine CurrentLine, FontWord CurrentWord, Text Text) 
    {
        boolean Added = CurrentLine.AttemptToAddWord(CurrentWord);
        if (!Added) {
            Lines.Add(CurrentLine);
            CurrentLine = new FontLine(metaData.GetSpaceWidth(), Text.GetFontSize(), Text.GetMaxLineSize());
            CurrentLine.AttemptToAddWord(CurrentWord);
        }
        Lines.Add(CurrentLine);
    }
 
    private TextMeshData createQuadVertices(Text Text, EngineList<FontLine> Lines) 
    {
        Text.SetNumberOfLines(Lines.size());
        double curserX = 0f;
        double curserY = 0f;
        EngineList<Vertex2f> Vertices = new EngineList<Vertex2f>();
        EngineList<TextureCoordinate> TextureCoords = new EngineList<TextureCoordinate>();
        for (FontLine Line : Lines) 
        {
            if (Text.IsCentered()) 
            {
                curserX = (Line.GetMaxLength() - Line.GetLineLength()) / 2;
            }
            for (FontWord word : Line.GetWords()) 
            {
                for (FontCharacter letter : word.GetCharacters()) 
                {
                    AddVerticesForCharacter(curserX, curserY, letter, Text.GetFontSize(), Vertices);
                    AddTexCoords(TextureCoords, letter.GetXTextureCoord(), letter.GetYTextureCoord(),
                            letter.GetXMaxTextureCoord(), letter.GetYMaxTextureCoord());
                    curserX += letter.GetXAdvance() * Text.GetFontSize();
                }
                curserX += metaData.GetSpaceWidth() * Text.GetFontSize();
            }
            curserX = 0;
            curserY += LINE_HEIGHT * Text.GetFontSize();
        }       
        return new TextMeshData(ListToVertices(Vertices), ListToTextureCoordinates(TextureCoords));
    }
 
    private void AddVerticesForCharacter(double CurserX, double CurserY, FontCharacter Character, double FontSize, EngineList<Vertex2f> Vertices) 
    {
        double x = CurserX + (Character.GetXOffset() * FontSize);
        double y = CurserY + (Character.GetYOffset() * FontSize);
        double maxX = x + (Character.GetSizeX() * FontSize);
        double maxY = y + (Character.GetSizeY() * FontSize);
        double properX = (2 * x) - 1;
        double properY = (-2 * y) + 1;
        double properMaxX = (2 * maxX) - 1;
        double properMaxY = (-2 * maxY) + 1;
        AddVertices(Vertices, properX, properY, properMaxX, properMaxY);
    }
 
    private static void AddVertices(EngineList<Vertex2f> Vertices, double x, double y, double maxX, double maxY) 
    {
    	Vertices.Add(new Vertex2f((float) x, (float) y));
    	Vertices.Add(new Vertex2f((float) x, (float) maxY));
    	Vertices.Add(new Vertex2f((float) maxX, (float) maxY));
    	Vertices.Add(new Vertex2f((float) maxX, (float) maxY));
    	Vertices.Add(new Vertex2f((float) maxX, (float) y));
    	Vertices.Add(new Vertex2f((float) x, (float) y));
    }
 
    private static void AddTexCoords(EngineList<TextureCoordinate> texCoords, double x, double y, double maxX, double maxY) 
    {
        texCoords.Add(new TextureCoordinate((float) x, (float) y));
        texCoords.Add(new TextureCoordinate((float) x, (float) maxY));
        texCoords.Add(new TextureCoordinate((float) maxX, (float) maxY));
        texCoords.Add(new TextureCoordinate((float) maxX, (float) maxY));
        texCoords.Add(new TextureCoordinate((float) maxX, (float) y));
        texCoords.Add(new TextureCoordinate((float) x, (float) y));
    }
 
     
    private static Vertex2f[] ListToVertices(EngineList<Vertex2f> Vertices) 
    {
        Vertex2f[] Result = new Vertex2f[Vertices.size()];
        for (int i = 0; i < Vertices.size(); i++) 
        {
        	Result[i] = Vertices.get(i);
        }
        return Result;
    }
    
    private static TextureCoordinate[] ListToTextureCoordinates(EngineList<TextureCoordinate> TexCoords) 
    {
        TextureCoordinate[] Result = new TextureCoordinate[TexCoords.size()];
        for (int i = 0; i < TexCoords.size(); i++) 
        {
        	Result[i] = TexCoords.get(i);
        }
        return Result;
    }
}
