package com.studios.base.engine.framework.transform;

import com.studios.base.engine.framework.math.Matrix4f;
import com.studios.base.engine.framework.math.Scale;
import com.studios.base.engine.framework.math.Vector;

public interface Transform<S extends Scale, V extends Vector<V>, T extends Transform<S, V, T>>
{
	void Update();
	
	boolean HasChanged();
	
	public V GetPosition();
	public V GetRotation();
	public S GetScale();

	public void SetPosition(V Position);

	public void SetRotation(V Rotation);
	public void SetScale(S nScale);
	
	public V GetOldPosition();
	public V GetOldRotation();
	public S GetOldScale();
	
	public Matrix4f GetTransformationMatrix();
	public Matrix4f GetTranslationMatrix();
	public Matrix4f GetRotationMatrix();
	public Matrix4f GetScaleMatrix();
	
	public Matrix4f GetParentMatrix();
	
	public T GetParent();
	public void SetParent(T Parent);
}
