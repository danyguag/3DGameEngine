package com.studios.base.engine.framework.math;

public class Vector4f implements Vector<Vector4f>
{
	private float m_x;
	private float m_y;
	private float m_z;
	private float m_w;
	
	public Vector4f(float x, float y, float z, float w)
	{
		this.m_x = x;
		this.m_y = y;
		this.m_z = z;
		this.m_w = w;
	}

	public float Length()
	{
		return (float)Math.sqrt(m_x * m_x + m_y * m_y + m_z * m_z + m_w * m_w);
	}

	public float Dot(Vector4f r)
	{
		return m_x * r.GetX() + m_y * r.GetY() + m_z * r.GetZ() + m_w * r.GetW();
	}
	
	public Vector4f Normalized()
	{
		float length = Length();
		
		return new Vector4f(m_x / length, m_y / length, m_z / length, m_w / length);
	}

	public Vector4f Lerp(Vector4f dest, float lerpFactor)
	{
		return dest.Subtract(this).Multiply(lerpFactor).Add(this);
	}
	
	public Vector4f PlusEqual(Vector4f r)
	{
		return new Vector4f(m_x += r.GetX(), m_y += r.GetY(), m_z += r.GetZ(), m_w += r.GetW());
	}
	
	public Vector4f PlusEqual(float r)
	{
		return new Vector4f(m_x += r, m_y += r, m_z += r, m_w += r);
	}
	
	public Vector4f MinusEqual(Vector4f r)
	{
		return new Vector4f(m_x -= r.GetX(), m_y -= r.GetY(), m_z -= r.GetZ(), m_w -= r.GetW());
	}
	
	public Vector4f MinusEqual(float r)
	{
		return new Vector4f(m_x -= r, m_y -= r, m_z -= r, m_w -= r);
	}
	
	public Vector4f MultiplyEqual(Vector4f r)
	{
		return new Vector4f(m_x *= r.GetX(), m_y *= r.GetY(), m_z *= r.GetZ(), m_w *= r.GetW());
	}
	
	public Vector4f MultiplyEqual(float r)
	{
		return new Vector4f(m_x *= r, m_y *= r, m_z *= r, m_w *= r);
	}
	
	public Vector4f DivideEqual(Vector4f r)
	{
		return new Vector4f(m_x /= r.GetX(), m_y /= r.GetY(), m_z /= r.GetZ(), m_w /= r.GetW());
	}
	
	public Vector4f DivideEqual(float r)
	{
		return new Vector4f(m_x /= r, m_y /= r, m_z /= r, m_w /= r);
	}
	
	public Vector4f Add(Vector4f r)
	{
		return new Vector4f(m_x + r.GetX(), m_y + r.GetY(), m_z + r.GetZ(), m_w + r.GetW());
	}
	
	public Vector4f Add(float r)
	{
		return new Vector4f(m_x + r, m_y + r, m_z + r, m_w + r);
	}
	
	public Vector4f Subtract(Vector4f r)
	{
		return new Vector4f(m_x - r.GetX(), m_y - r.GetY(), m_z - r.GetZ(), m_w - r.GetW());
	}
	
	public Vector4f Subtract(float r)
	{
		return new Vector4f(m_x - r, m_y - r, m_z - r, m_w - r);
	}
	
	public Vector4f Multiply(Vector4f r)
	{
		return new Vector4f(m_x * r.GetX(), m_y * r.GetY(), m_z * r.GetZ(), m_w * r.GetW());
	}
	
	public Vector4f Multiply(float r)
	{
		return new Vector4f(m_x * r, m_y * r, m_z * r, m_w * r);
	}
	
	public Vector4f Divide(Vector4f r)
	{
		return new Vector4f(m_x / r.GetX(), m_y / r.GetY(), m_z / r.GetZ(), m_w / r.GetW());
	}
	
	public Vector4f Divide(float r)
	{
		return new Vector4f(m_x / r, m_y / r, m_z / r, m_w / r);
	}
	
	public Vector4f AbsoluteValue()
	{
		return new Vector4f(Math.abs(m_x), Math.abs(m_y), Math.abs(m_z), Math.abs(m_w));
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
	
	public Vector3f GetXYZ() { return new Vector3f(m_x, m_y, m_z); }
	public Vector3f GetXZY() { return new Vector3f(m_x, m_z, m_y); }
	
	public Vector3f GetZYX() { return new Vector3f(m_z, m_y, m_x); }
	public Vector3f GetZXY() { return new Vector3f(m_z, m_x, m_y); }
	
	public Vector3f GetYXZ() { return new Vector3f(m_y, m_x, m_z); }
	public Vector3f GetYZX() { return new Vector3f(m_y, m_z, m_x); }
	
	public Vector4f Set(float x, float y, float z) { this.m_x = x; this.m_y = y; this.m_z = z; return this; }
	public Vector4f Set(Vector4f r) { Set(r.GetX(), r.GetY(), r.GetZ()); return this; }

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
	
	public float GetW()
	{
		return m_w;
	}

	public void SetW(float w)
	{
		this.m_w = w;
	}

	public boolean equals(Vector4f r)
	{
		return m_x == r.GetX() && m_y == r.GetY() && m_z == r.GetZ();
	}
}
