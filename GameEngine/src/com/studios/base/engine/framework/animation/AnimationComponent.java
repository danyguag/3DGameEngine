package com.studios.base.engine.framework.animation;

import java.util.List;

import com.studios.base.engine.framework.components.GameComponent;
import com.studios.base.engine.framework.threading.Snapshot;
import com.studios.base.engine.framework.transform.Transform3f;
import com.studios.base.engine.framework.transform.TransformType;
import com.studios.base.engine.rendering.data.Vertex3f;
import com.studios.base.engine.rendering.model.Model;
import com.studios.base.engine.rendering.shader.Shader;

public class AnimationComponent extends GameComponent
{	
	private List<Frame> m_bones;
	
	public AnimationComponent(Model model)
	{
		super.Name = "AnimationComponent";
		
		int CurrentFrame = 0;
		
		for (Vertex3f Vertex : model.GetVertices())
		{
			Bone bone = new Bone(Vertex, new Transform3f());
			Frame frame = new Frame (bone, CurrentFrame);
			m_bones.add(frame);
			CurrentFrame++;
		}
	}
	
	@Override
	public void Init() 
	{
		
	}

//	@Override
	public void Render(Shader shader) 
	{
		throw new IllegalStateException("Not changed to new way of updating");
//		int FramesPassed = 0;
//		
//		int TotalFrames = m_bones.size();
//		int CurrentFrameCount = 0;
//		for (Frame CurrentFrame : m_bones)
//		{
//			FramesPassed++;
//			CurrentFrame.Render(shader, CurrentFrameCount);
//			
//			if (CurrentFrameCount != TotalFrames)
//			{
//				CurrentFrameCount++;
//			}
//			else if (CurrentFrameCount == TotalFrames)
//			{
//				if (FramesPassed != TotalFrames)
//				{
//					TotalFrames = FramesPassed;
//				}
//			}
//		}
	}

	@Override
	public void Update(Snapshot CurrentGameState) 
	{
		
	}
	
	public List<Frame> GetBones()
	{
		return m_bones;
	}

	@Override
	public TransformType GetTransformType() 
	{
		return TransformType.TRANSFROM_3F;
	}
}
