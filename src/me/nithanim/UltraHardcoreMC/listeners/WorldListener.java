package me.nithanim.UltraHardcoreMC.listeners;

import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;
import me.nithanim.UltraHardcoreMC.populator.*;

import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.WorldInitEvent;

/**
 * This class listens for world initialize events to make them hardcore worlds. It also makes them hardcore when this listener is instanciated.
 *
 */
public class WorldListener extends SpecificWorldsListener {
	
	
	public WorldListener(UltraHardcoreMC plugin) {
		super(plugin);
		
		//search though all worlds to mark specific as hardcore
		for(World w : plugin.getServer().getWorlds())
		{
			addHardcore(w);
		}
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onWorldInit(WorldInitEvent event) //also listen on events for new worlds to make them hardcore
	{
		addHardcore(event.getWorld());
		
		//generating spawn platform
		/*
		int r = 10;
		Location loc = world.getSpawnLocation();
		Block midpoint = world.getHighestBlockAt(loc);
		int y = midpoint.getY();
		
		for(int x = midpoint.getX() - r ; x < midpoint.getX() + r ; x++)
		{
			for(int z = midpoint.getZ() - r ; z < midpoint.getZ() + r ; z++)
			{
				world.getBlockAt(x, y, z).setType(Material.OBSIDIAN);
			}
		}*/
		
		
	}
	
	/**
	 * Adds hardcore to a specific world if it is allowed to.
	 * @param w World to make harder.
	 */
	private void addHardcore(World w)
	{
		if(SpecificWorldsListener.isEnabledWorld(w.getName()))
		{
			attachBorderBlockPopulator(w);
			
			if(plugin.getConfig().getBoolean("bonus.surfacechest.spawn"))
			{
				attachBonuschestBlockPopulator(w);
			}
			
			w.setDifficulty(Difficulty.HARD);
		}
	}
	
	
	
	private void attachBorderBlockPopulator(World world)
	{
		plugin.getLogger().info("Border creation for world " + world.getName() + " enabled!");
		world.getPopulators().add(new BorderBlockPopulator(plugin, world));
	}
	
	private void attachBonuschestBlockPopulator(World world)
	{
		world.getPopulators().add(new SurfaceChestBlockPopulator(plugin));
	}
}
