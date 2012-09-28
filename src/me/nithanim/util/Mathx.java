package me.nithanim.util;

import org.bukkit.util.Vector;


public class Mathx
{
	public static double distance2dSquared(Vector a, Vector b) 
	{
		double ax, ay, bx, by, x, y;
		
		ax = a.getX();
		ay = a.getY();
		bx = b.getX();
		by = b.getY();
		
		x = bx - ax;
		y = by - ay;
		
		return x * x + y * y;
	}
}
