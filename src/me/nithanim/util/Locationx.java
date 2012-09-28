package me.nithanim.util;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class Locationx extends Location {

	public Locationx(World world, Vector vector)
	{
		super(world, vector.getX(), vector.getY(), vector.getZ());
	}
	
	public Locationx(World world, double x, double y, double z)
	{
		super(world, x, y, z);
	}
	
	public Locationx(World world, double x, double y, double z, float yaw, float pitch)
	{
		super(world, x, y, z, yaw, pitch);
	}
}
