package me.nithanim.UltraHardcoreMC.populator;

import java.util.List;
import java.util.Random;

import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.Vector;



public class BorderBlockPopulator extends BlockPopulator {
	private UltraHardcoreMC plugin;
	
	private World world;
	private Vector spawnVec; //stored here to be able to cope with the nether
	private int radius;
	private int wallMat;
	
	
	public BorderBlockPopulator(UltraHardcoreMC plugin,  World world) {
		this.plugin = plugin;
		this.world = world;
		
		
		wallMat = plugin.getConfig().getInt("border.wall.material",20); //caching material for less cpu usage
		spawnVec = findWorldSpawn();
		radius = plugin.getConfig().getInt("border.radius",30);
		if(world.getEnvironment() == Environment.NETHER)
		{
			radius /= 8; //trim border because of walking longer distances in nether
		}
	}
	
	@Override
	public void populate(World wld, Random rnd, Chunk chunk) {
		spawnBorder(world, rnd, chunk);
	}
	
	private void spawnBorder(World world, Random rnd, Chunk chunk)
	{
		int cx = chunk.getX()*16;
		int cz = chunk.getZ()*16;
		
		int distx;
		int disty;
		
		for (int x=0 ; x<16 ; x++)
		{
			distx = Math.abs( (cx + x) - (spawnVec.getBlockX()) );
			
			for(int z=0 ; z<16 ; z++)
			{
				disty = Math.abs( (cz + z) - (spawnVec.getBlockZ()) );
				
				if(distx <= radius && disty <= radius)
				{
					if(distx == radius || disty == radius)
					{
						placeWall(new Vector(cx + x, 0, cz + z));
					}
				}
			}
		}
		world.getMaxHeight();
	}
	
	/**
	 * Spawns on block of wall at the given Location.
	 * @param loc Location (only x, z are used)
	 */
	private void placeWall(Vector vec)
	{
		for (int y=2 ; y<128 /*world.getMaxHeight()*/ ; y++)
		{
			Block b = world.getBlockAt(vec.getBlockX(), y, vec.getBlockZ());
			b.setTypeId(wallMat);
		}
	}
	
	
	/**
	 * Uses findWorldSpawnSource() for source.
	 * @return A spawn vector, sacled to nether
	 */
	private Vector findWorldSpawn()
	{
		Vector vec = findWorldSpawnSource();
		
		if(world.getEnvironment() == Environment.NETHER)//take care of nether
		{
			vec.divide(new Vector(8,0,8)); 
		}
		
		return vec;
	}
	
	/**
	 * Fetches the spawn vector from an appropriate source.<br />
	 * 
	 * If it is ...
	 * <ul>
	 * 	<li>... set in config, it is taken from config.</li>
	 * 	<li>... a normal world, it will take that spawn.</li>
	 * 	<li>... a nether, it will take that spawn.</li>
	 * </ul>
	 * @return Spawn as a Vector
	 */
	private Vector findWorldSpawnSource()
	{
		Vector confvec = getConfigVec();
		
		if(plugin.getConfig().getBoolean("border.midpoint.enabled") && confvec!=null) //if it should use AND the vector is ok
		{
			if (world.getEnvironment() == Environment.NETHER)
				return confvec.clone().divide(new Vector(8,0,8));
			else
				return confvec;
		}
		else
		{
			//take spawn of overworld for nether so that the borders match in (both) worlds
			if(world.getEnvironment() == Environment.NETHER) 
			{
				List<World> wlds = plugin.getServer().getWorlds(); //fetch all worlds
				for(World wld : wlds)
				{
					if(wld.getEnvironment() == Environment.NORMAL)
					{
						return wld.getSpawnLocation().toVector();
					}
				}
				//no normal world was found for nether, taking nether spawn then...
			}
	
			return world.getSpawnLocation().toVector();
		}
	}
	
	/**
	 * Fetches the midpont from config
	 * @return Vector on success, null if obstructed
	 */
	
	private Vector getConfigVec()
	{
		Vector vec = plugin.getConfig().getVector("border.midpoint.location");
		if(vec == null)
		{
			return null;
		}
		else
		{
			return vec;
		}
	}
}
