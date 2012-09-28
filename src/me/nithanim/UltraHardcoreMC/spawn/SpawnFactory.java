package me.nithanim.UltraHardcoreMC.spawn;

import java.util.Iterator;
import java.util.Random;

import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;
import me.nithanim.util.Locationx;
import me.nithanim.util.Mathx;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.Configuration;
import org.bukkit.util.Vector;



public class SpawnFactory {
	
	private SpawnHandler spawnhandler;
	private int minSpawnDistance;
	private int	maxSpawnDistance;
	
	/** World to create spawns in */
	private World world;
	
	
	private static SpawnFactory instance; 
	
	private SpawnFactory()
	{
		UltraHardcoreMC plugin = UltraHardcoreMC.getPlugin();
		this.minSpawnDistance = plugin.getConfig().getInt("spawn.minSpawnDistance");
		this.spawnhandler = plugin.getSpawnHandler();
		
		maxSpawnDistance = plugin.getConfig().getInt("border.radius");
		
		world = plugin.getServer().getWorlds().get(0); //TODO make it ready for multiworld
	}
	
	public Spawn createNewSpawn() throws SpawnCreationException
	{
		return new Spawn(findValidLocation());
	}
	
	/**
	 * Searches for a SpawnPoint that doesn't intersects with another
	 * @throws Exception if it couldn't find an appropriate spawn pont is an amount of tries.
	 */
	private Location findValidLocation() throws SpawnCreationException 
	{
		Configuration config = UltraHardcoreMC.getPlugin().getConfig();
		Location loc = null;
		
		for(int i=0 ; i<200 ; i++)
		{
			
			loc = findPotentialSpawn(config);
			
			if(!checkSurroundings(loc))
			{
				loc = null;
				continue;
			}
			else
			{
				break;
			}
			
		}
		
		if(loc == null)
		{
			throw new SpawnCreationException("Unable to find a valid spawn location!");
		}
		return loc;
	}
	
	private boolean checkSurroundings(Location loc) throws SpawnCreationException
	{
		switch (loc.getBlock().getRelative(BlockFace.DOWN).getType())
		{
			case WATER:
			case STATIONARY_WATER:
			case LAVA:
			case STATIONARY_LAVA:
			case ICE:
				return false;		
			default:
				break;
		}
		
		return true;
	}
	
	/**
	 * Searches for a spawn that is not intersecting with others
	 * without any additional cheacks
	 * @throws SpawnCreationException 
	 */
	private Location findPotentialSpawn(Configuration config)// throws SpawnCreationException
	{
		
		Location midpoint;
		midpoint = world.getSpawnLocation();
		if(config.getBoolean("border.midpoint.enabled"))
		{
			Vector confvec = config.getVector("border.midpoint.location");
			if(confvec != null)
				midpoint = new Locationx(world, confvec);
		}
		
		
		Location loc;
		for (int i = 0; i < 300; i++) //do only a certain amount of tries until give up
		{
			//Border is a box (at least for now)
			loc = midpoint.clone();
			
			
			loc = loc.add(new Vector(getRandDist(), 0, getRandDist()));
			
			if (!isIntersecting(loc.toVector())) {
				//System.out.println("Spot found after " + i + " attepts:");
				
				//System.out.print("Distance Worldspawn, Spawn: " + distance2d(world.getSpawnLocation().toVector(), spawn.toVector()));
				
				loc.setY(loc.getWorld().getHighestBlockAt(loc).getY()); //Y fix for player teleporting to their spawn
				return loc;
			}
		}
		
		//else
		return null;
		//throw new SpawnCreationException("Unable to find a new appropriate spawnpoint. Stopped to prevent overloading.");
	}
	
	/**
	 * Does the given SpawnVector intersects with any previous defined one?
	 * @param vec Vector to test.
	 * @return True if it intersects.
	 */
	private boolean isIntersecting(Vector vec) {
		Iterator<Spawn> sp = spawnhandler.getSpawns().iterator();
		Spawn nextsp;
		Vector nextvec;
		
		while (sp.hasNext()) {
			nextsp = sp.next();
			nextvec = nextsp.getLocation().toVector();
			
			if (Mathx.distance2dSquared(nextvec, vec) <= minSpawnDistance * minSpawnDistance) {
				return true;
			}
		}
		return false;
	}
	
	private float getRandDist() {
		Random random = new Random();
		return (random.nextBoolean() ? 1 : -1) * random.nextInt(maxSpawnDistance);
	}
	
	
	public static SpawnFactory getInstance()
	{
		if(instance == null)
		{
			instance = new SpawnFactory();
		}
		return instance;
	}
	
	public static void destroyFactory()
	{
		instance = null;
	}
}

