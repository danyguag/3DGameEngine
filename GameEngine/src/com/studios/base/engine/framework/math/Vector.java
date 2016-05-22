package com.studios.base.engine.framework.math;

public interface Vector<V extends Vector<V>> 
{
	V Add(float Other);
	V Add(V Other);
	V Subtract(float Other);
	V Subtract(V Other);
	V Divide(float Other);
	V Divide(V Other);
	V Multiply(float Other);
	V Multiply(V Other);

	V PlusEqual(float Other);
	V PlusEqual(V Other);
	V MinusEqual(float Other);
	V MinusEqual(V Other);
	V DivideEqual(float Other);
	V DivideEqual(V Other);
	V MultiplyEqual(float Other);
	V MultiplyEqual(V Other);
	
	V Set(V nVector);
}
