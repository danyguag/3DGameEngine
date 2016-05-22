package com.studios.base.engine.framework.math;

public class Vector3f implements Vector<Vector3f>
{
	private float m_x;
	private float m_y;
	private float m_z;
	
	public Vector3f(float x, float y, float z)
	{
		this.m_x = x;
		this.m_y = y;
		this.m_z = z;
	}

	public float Length()
	{
		return (float)Math.sqrt(m_x * m_x + m_y * m_y + m_z * m_z);
	}

	public float Max()
	{
		return Math.max(m_x, Math.max(m_y, m_z));
	}

	public float Dot(Vector3f r)
	{
		return m_x * r.GetX() + m_y * r.GetY() + m_z * r.GetZ();
	}
	
	public Vector3f Cross(Vector3f r)
	{
		float x_ = m_y * r.GetZ() - m_z * r.GetY();
		float y_ = m_z * r.GetX() - m_x * r.GetZ();
		float z_ = m_x * r.GetY() - m_y * r.GetX();
		
		return new Vector3f(x_, y_, z_);
	}
	
	public Vector3f Normalized()
	{
		float length = Length();
		
		return new Vector3f(m_x / length, m_y / length, m_z / length);
	}

	public Vector3f Rotate(Vector3f axis, float angle)
	{
		float sinAngle = (float)Math.sin(-angle);
		float cosAngle = (float)Math.cos(-angle);

		return this.Cross(axis.Multiply(sinAngle)).Add(           
				(this.Multiply(cosAngle)).Add(                     
						axis.Multiply(this.Dot(axis.Multiply(1 - cosAngle))))); 
	}

	public Vector3f Rotate(Quaternion rotation)
	{
		Quaternion conjugate = rotation.Conjugate();

		Quaternion w = rotation.Multiply(this).Multiply(conjugate);

		return new Vector3f(w.GetX(), w.GetY(), w.GetZ());
	}

	public Vector3f Lerp(Vector3f dest, float lerpFactor)
	{
		return dest.Subtract(this).Multiply(lerpFactor).Add(this);
	}
	
	public Vector3f PlusEqual(Vector3f r)
	{
		return new Vector3f(m_x += r.GetX(), m_y += r.GetY(), m_z += r.GetZ());
	}
	
	public Vector3f PlusEqual(float r)
	{
		return new Vector3f(m_x += r, m_y += r, m_z += r);
	}
	
	public Vector3f MinusEqual(Vector3f r)
	{
		return new Vector3f(m_x -= r.GetX(), m_y -= r.GetY(), m_z -= r.GetZ());
	}
	
	public Vector3f MinusEqual(float r)
	{
		return new Vector3f(m_x -= r, m_y -= r, m_z -= r);
	}
	
	public Vector3f MultiplyEqual(Vector3f r)
	{
		return new Vector3f(m_x *= r.GetX(), m_y *= r.GetY(), m_z *= r.GetZ());
	}
	
	public Vector3f MultiplyEqual(float r)
	{
		return new Vector3f(m_x *= r, m_y *= r, m_z *= r);
	}
	
	public Vector3f DivideEqual(Vector3f r)
	{
		return new Vector3f(m_x /= r.GetX(), m_y /= r.GetY(), m_z /= r.GetZ());
	}
	
	public Vector3f DivideEqual(float r)
	{
		return new Vector3f(m_x /= r, m_y /= r, m_z /= r);
	}
	
	public Vector3f Add(Vector3f r)
	{
		return new Vector3f(m_x + r.GetX(), m_y + r.GetY(), m_z + r.GetZ());
	}
	
	public Vector3f Add(float r)
	{
		return new Vector3f(m_x + r, m_y + r, m_z + r);
	}
	
	public Vector3f Subtract(Vector3f r)
	{
		return new Vector3f(m_x - r.GetX(), m_y - r.GetY(), m_z - r.GetZ());
	}
	
	public Vector3f Subtract(float r)
	{
		return new Vector3f(m_x - r, m_y - r, m_z - r);
	}
	
	public Vector3f Multiply(Vector3f r)
	{
		return new Vector3f(m_x * r.GetX(), m_y * r.GetY(), m_z * r.GetZ());
	}
	
	public Vector3f Multiply(float r)
	{
		return new Vector3f(m_x * r, m_y * r, m_z * r);
	}
	
	public Vector3f Divide(Vector3f r)
	{
		return new Vector3f(m_x / r.GetX(), m_y / r.GetY(), m_z / r.GetZ());
	}
	
	public Vector3f Divide(float r)
	{
		return new Vector3f(m_x / r, m_y / r, m_z / r);
	}
	
	public Vector3f AbsoluteValue()
	{
		return new Vector3f(Math.abs(m_x), Math.abs(m_y), Math.abs(m_z));
	}
	
	public String toString()
	{
		return "(" + m_x + " " + m_y + " " + m_z + ")";
	}

	public Vector2f GetXY() { return new Vector2f(m_x, m_y); }
	public Vector2f GetYZ() { return new Vector2f(m_y, m_z); }
	public Vector2f GetZX() { return new Vector2f(m_z, m_x); }

	public Vector2f GetYX() { return new Vector2f(m_y, m_x); }
	public Vector2f GetZY() { return new Vector2f(m_z, m_y); }
	public Vector2f GetXZ() { return new Vector2f(m_x, m_z); }

	public Vector3f Set(float x, float y, float z) { this.m_x = x; this.m_y = y; this.m_z = z; return this; }
	public Vector3f Set(Vector3f r) { Set(r.GetX(), r.GetY(), r.GetZ()); return this; }

	public float GetX()
	{
		return m_x;
	}

	public void SetX(float x)
	{
		this.m_x = x;
	}

	public float GetY()
	{
		return m_y;
	}

	public void SetY(float y)
	{
		this.m_y = y;
	}

	public float GetZ()
	{
		return m_z;
	}

	public void SetZ(float z)
	{
		this.m_z = z;
	}

	public boolean equals(Vector3f r)
	{
		return m_x == r.GetX() && m_y == r.GetY() && m_z == r.GetZ();
	}
}
