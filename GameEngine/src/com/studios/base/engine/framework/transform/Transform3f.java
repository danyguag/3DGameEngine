package com.studios.base.engine.framework.transform;

import com.studios.base.engine.framework.math.Matrix4f;
import com.studios.base.engine.framework.math.Scale3f;
import com.studios.base.engine.framework.math.Vector3f;

public class Transform3f implements Transform<Scale3f, Vector3f, Transform3f>
{
    private Transform3f m_parent;
    private Matrix4f m_parentMatrix;

    private Vector3f m_position;
    private Vector3f m_rotation;
    private Scale3f m_scale;

	private Vector3f m_oldPosition;
    private Vector3f m_oldRotation;
    private Scale3f m_oldScale;

    public Transform3f()
    {
        m_parentMatrix  = new Matrix4f().InitIdentity();
        
        m_position      = new Vector3f(0,0,0);
        m_rotation      = new Vector3f(0,0,0);
        m_scale         = new Scale3f(1,1,1);
        
        m_oldPosition   = new Vector3f(0,0,0);
        m_oldRotation   = new Vector3f(0,0,0);
        m_oldScale      = new Scale3f(1,1,1);

    }

    @Override
    public void Update()
    {
        if (HasChanged())
        {
            m_oldPosition = m_position;
            m_oldRotation = m_rotation;
            m_oldScale = m_scale;
        }
    }

    public void Rotate(Vector3f axis, float angle)
    {
        //m_rotation.Rotate(axis, angle);
    }

    public boolean HasChanged()
    {
        if(m_parent != null && m_parent.HasChanged())
            return true;

        if(!m_position.equals(m_oldPosition))
            return true;

        if(!m_rotation.equals(m_oldRotation))
            return true;

        if(!m_scale.equals(m_oldScale))
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
		return new Matrix4f().InitTranslation(m_position.GetX(), m_position.GetY(), m_position.GetZ());
	}

	@Override
	public Matrix4f GetRotationMatrix() 
	{
		return new Matrix4f().InitRotation(m_rotation.GetX(), m_rotation.GetY(), m_rotation.GetZ());
	}

	@Override
	public Matrix4f GetScaleMatrix() 
	{
		return new Matrix4f().InitScale(m_scale.GetX(), m_scale.GetY(), m_scale.GetZ());
	}
	
    @Override
    public Matrix4f GetParentMatrix()
    {
        if (m_parent != null && m_parent.HasChanged())
            m_parentMatrix = m_parent.GetTransformationMatrix();
        return m_parentMatrix;
    }
	
	@Override
    public Vector3f GetPosition()
    {
        return m_position;
    }

    @Override
    public Vector3f GetRotation()
    {
        return m_rotation;
    }

    @Override
    public Scale3f GetScale()
    {
        return m_scale;
    }

	@Override
	public Scale3f GetOldScale()
	{
		return m_oldScale;
	}
	
	@Override
    public Vector3f GetOldPosition() 
    {
		return m_oldPosition;
	}
	
	@Override
	public Vector3f GetOldRotation() 
	{
		return m_oldRotation;
	}

	@Override
	public Transform3f GetParent() 
	{
		return m_parent;
	}

	@Override
    public void SetParent(Transform3f parent)
    {
        this.m_parent = parent;
    }
	
	@Override
    public void SetPosition(Vector3f position)
    {
        m_position = position;
    }

	@Override
    public void SetRotation(Vector3f rotation)
    {
        m_rotation = rotation;
    }

	@Override
    public void SetScale(Scale3f scale)
    {
        m_scale = scale;
    }
	
	public void SetPositionX(float X)
	{
		m_position.SetX(X);
	}
	
	public void SetPositionY(float Y)
	{
		m_position.SetY(Y);
	}
	
	public void SetPositionZ(float Z)
	{
		m_position.SetZ(Z);
	}
	
	public void SetRotationX(float X)
	{
		m_rotation.SetX(X);
	}
	
	public void SetRotationY(float Y)
	{
		m_rotation.SetY(Y);
	}
	
	public void SetRotationZ(float Z)
	{
		m_rotation.SetZ(Z);
	}
	
	public void SetScaleX(float X)
	{
		m_scale.SetX(X);
	}
	
	public void SetScaleY(float Y)
	{
		m_scale.SetY(Y);
	}
	
	public void SetScaleZ(float Z)
	{
		m_scale.SetZ(Z);
	}
}

