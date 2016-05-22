package com.studios.base.engine.core.util;

public class Time
{
	private static double m_delta;
	public static long SECOND = 1000000000l;
	
	public static long GetTime()
	{
		return System.nanoTime();
	}
	
	public static double GetDelta()
	{
		return Time.m_delta;
	}
	
	public static void SetDelta(double delta)
	{
		Time.m_delta = delta;
	}
}
