package com.studios.base.engine.framework.transform;

import com.studios.base.engine.framework.math.Matrix4f;
import com.studios.base.engine.framework.math.Scale2f;
import com.studios.base.engine.framework.math.Vector2f;

public class Transform2f implements Transform<Scale2f, Vector2f, Transform2f>
{
	private Transform2f m_parent;
	private Matrix4f m_parentMatrix;
	
	private Vector2f m_position;
	private Vector2f m_rotation;
	private Scale2f m_scale;
	
	private Vector2f m_oldPosition;
	private Vector2f m_oldRotation;
	private Scale2f m_oldScale;
	
	public Transform2f()
	{
		m_parentMatrix = new Matrix4f().InitIdentity();

		m_position = new Vector2f(0, 0);
		m_rotation = new Vector2f(0, 0);
		m_scale = new Scale2f(1, 1);
		
		m_oldPosition = new Vector2f(0, 0);
		m_oldRotation = new Vector2f(0, 0);
		m_oldScale = new Scale2f(1, 1);

	}
	
	@Override
	public void Update() 
	{
		if (HasChanged())
		{
			m_oldPosition = new Vector2f(m_position.GetX(), m_position.GetY());
			m_oldRotation = new Vector2f(m_rotation.GetX(), m_rotation.GetY());
			m_oldScale = new Scale2f(m_scale.GetX(), m_scale.GetY());
		}
	}

	@Override
	public boolean HasChanged() 
	{
		if (m_oldPosition.Equals(m_position))
			return true;
		if (m_oldRotation.Equals(m_rotation))
			return true;
		if (m_oldScale.Equals(m_scale))
			return true;
		
		return false;
	}

	@Override
	public Matrix4f GetTransformationMatrix() 
	{
		return GetParentMatrix().Mul(GetTranslationMatrix().Mul(GetRotationMatrix().Mul(GetScaleMatrix())));
	}

	@Override
	public Matrix4f GetTranslationMatrix() 
	{
		return new Matrix4f().InitTranslation(m_position.GetX(), m_position.GetY(), 0);
	}

	@Override
	public Matrix4f GetRotationMatrix() 
	{
		return new Matrix4f().InitRotation(m_rotation.GetX(), m_rotation.GetY(), 0);
	}

	@Override
	public Matrix4f GetScaleMatrix() 
	{
		return new Matrix4f().InitScale(m_scale.GetX(), m_scale.GetY(), 1f);
	}

	@Override
	public Matrix4f GetParentMatrix() 
	{
		if (m_parent != null)
			m_parentMatrix = m_parent.GetParentMatrix();
		return m_parentMatrix;
	}
	
	@Override
	public Vector2f GetPosition() 
	{
		return m_position;
	}

	@Override
	public Vector2f GetRotation() 
	{
		return m_rotation;
	}

	@Override
	public Scale2f GetScale() 
	{
		return m_scale;
	}

	@Override
	public Vector2f GetOldPosition() 
	{
		return m_oldPosition;
	}

	@Override
	public Vector2f GetOldRotation() 
	{
		return m_oldRotation;
	}

	@Override
	public Scale2f GetOldScale() 
	{
		return m_oldScale;
	}

	@Override
	public Transform2f GetParent() 
	{
		return m_parent;
	}

	@Override
	public void SetParent(Transform2f Parent) 
	{
		m_parent = Parent;
	}
	
	@Override
	public void SetPosition(Vector2f Position) 
	{
		m_position = Position;
	}

	@Override
	public void SetRotation(Vector2f Rotation) 
	{
		m_rotation = Rotation;
	}

	@Override
	public void SetScale(Scale2f nScale) 
	{
		m_scale = nScale;
	}

	public void SetPositionX(float X)
	{
		m_position.SetX(X);
	}
	
	public void SetPositionY(float Y)
	{
		m_position.SetY(Y);
	}

	public void SetRotationX(float X)
	{
		m_rotation.SetX(X);
	}
	
	public void SetRotationY(float Y)
	{
		m_rotation.SetY(Y);
	}
	
	public void SetScaleX(float X)
	{
		m_scale.SetX(X);
	}
	
	public void SetScaleY(float Y)
	{
		m_scale.SetY(Y);
	}
}
