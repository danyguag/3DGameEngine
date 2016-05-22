package com.studios.base.engine.rendering.loaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.studios.base.engine.core.CoresManager;
import com.studios.base.engine.framework.containers.util.EngineList;
import com.studios.base.engine.rendering.data.Normal;
import com.studios.base.engine.rendering.data.TextureCoordinate;
import com.studios.base.engine.rendering.data.Vertex3f;
import com.studios.base.engine.rendering.model.Model;

public class ObjModel 
{
	public static Model LoadObjModel(String fileName)
    {
            FileReader fr = null;
            try 
            {
                    fr = new FileReader(new File("data/"+fileName + ".obj"));
            }
            catch (FileNotFoundException e) 
            {
            	e.printStackTrace();
                e.printStackTrace(CoresManager.LoggerStream);
            }
            BufferedReader Reader = new BufferedReader(fr);
            String line;
            EngineList<Vertex3f> Vertices = new EngineList<Vertex3f>();
            EngineList<TextureCoordinate> Textures = new EngineList<TextureCoordinate>();
            EngineList<Normal> Normals = new EngineList<Normal>();
            EngineList<Integer> Indices = new EngineList<Integer>();

            Vertex3f[] VerticesResult = null;
        	Normal[] NormalsResult = null;
        	TextureCoordinate[] TextureResult = null;
        	int[] IndicesResult = null;
           
            try
            {
                    while(true)
                    {
                            line = Reader.readLine();
                            String[] currentLine = line.split(" ");
                            if(line.startsWith("v "))
                            {
                                    Vertex3f vertex = new Vertex3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                                    Vertices.Add(vertex);
                            }
                            else if(line.startsWith("vt "))
                            {
                            		TextureCoordinate texture = new TextureCoordinate(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]));
                                    Textures.Add(texture);
                            }
                            else if(line.startsWith("vn "))
                            {
                                    Normal normal = new Normal(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3]));
                                    Normals.Add(normal);
                            }
                            else if(line.startsWith("f "))
                            {
                                    TextureResult = new TextureCoordinate[Vertices.size()];
                                    NormalsResult = new Normal[Vertices.size()];
                                    break;
                            }
                    }
                   
                    while(line!=null)
                    {
                            if(!line.startsWith("f "))
                            {
                                    line = Reader.readLine();
                                    continue;
                            }
                            String[] currentLine = line.split(" ");
                            String[] vertex1 = currentLine[1].split("/");
                            String[] vertex2 = currentLine[2].split("/");
                            String[] vertex3 = currentLine[3].split("/");
                           
                            processVertex(vertex1, Indices, Textures, Normals, TextureResult, NormalsResult);
                            processVertex(vertex2, Indices, Textures, Normals, TextureResult, NormalsResult);
                            processVertex(vertex3, Indices, Textures, Normals, TextureResult, NormalsResult);
                            line = Reader.readLine();
                    }
                    Reader.close();
            }catch(Exception e)
            {
                    e.printStackTrace();
            }
           
            VerticesResult = new Vertex3f[Vertices.size()];
            IndicesResult = new int[Indices.size()];
           
            int vertexPointer = 0;
            for(Vertex3f vertex : Vertices)
            {
            	VerticesResult[vertexPointer] = new Vertex3f(vertex.GetX(), vertex.GetY(), vertex.GetZ());
            	vertexPointer++;
            }
           
            for(int i=0;i<Indices.size();i++)
            {
            	IndicesResult[i] = Indices.get(i);
            }
            
            return new Model(VerticesResult, TextureResult, NormalsResult, IndicesResult);
    }

    private static void processVertex(String[] vertexData, EngineList<Integer> indices, EngineList<TextureCoordinate> textures, EngineList<Normal> normals, TextureCoordinate[] textureResult, Normal[] normalsResult)
    {
        int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
        indices.Add(currentVertexPointer);
        TextureCoordinate currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1);
        textureResult[currentVertexPointer] = new TextureCoordinate(currentTex.GetX(), 1 - currentTex.GetY());
        Normal currentNorm = normals.get(Integer.parseInt(vertexData[2])-1);
        normalsResult[currentVertexPointer] = currentNorm;
    }
}
