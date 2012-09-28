package me.nithanim.UltraHardcoreMC.listeners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;



public class HardcoreListener extends SpecificWorldsListener {
	
	
	/**
	 * Stores all players who should be banned on quit
	 * null if disabled
	 */
	private List<String> playersToBan;
	
	public HardcoreListener(UltraHardcoreMC plugin) 
	{
		super(plugin);
        
        if(plugin.getConfig().getBoolean("respawn.banOnQuit"))
        {
        	playersToBan = new ArrayList<String>();
        }
    }
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onRegainHealth(EntityRegainHealthEvent event)
	{
		if(!isEnabledWorld(event.getEntity().getWorld().getName())) return;
		
		if(event.getRegainReason() == RegainReason.SATIATED)
		{
			event.setCancelled(true);
		}
	}

	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDeath(EntityDeathEvent event)
	{
		if(!isEnabledWorld(event.getEntity().getWorld().getName())) return;
		
		if(event.getEntityType() == EntityType.GHAST)
		{
			List<ItemStack> drops = event.getDrops();
			//drops.clear();
			Random rand = new Random();
			if(rand.nextFloat() < 0.7)
			{
				drops.add(new ItemStack(Material.GOLD_INGOT,1));
			}
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent event)
	{
		if(!isEnabledWorld(event.getEntity().getWorld().getName())) return;
		
		//make death-message more visible
		StringBuilder sb = new StringBuilder(event.getDeathMessage().length() + 2);
		
		sb.append(ChatColor.RED);
		sb.append(event.getDeathMessage());
		
		event.setDeathMessage(sb.toString());
		
		
		//banOnQuit
		if(playersToBan != null)
		{
			playersToBan.add(event.getEntity().getName());
		}
		
		//blot on death
		if(plugin.getConfig().getBoolean("bonus.boltOnDeath.enabled"))
		{
			Location loc = event.getEntity().getLocation();
			loc.getWorld().strikeLightningEffect(loc);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		Player p = event.getPlayer();
		
		if(!isEnabledWorld(p.getWorld().getName())) return;
		
		if(plugin.getConfig().getBoolean("respawn.gocreative"))
		{
			p.setGameMode(GameMode.CREATIVE);
		}
		
		
		String wldstr = plugin.getConfig().getString("respawn.tpworld");
		if(!wldstr.equals("false"))
		{
			World wld = plugin.getServer().getWorld(wldstr);
			if(wld != null)
			{
				p.teleport(wld.getSpawnLocation());
			}
			else
			{
				p.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
				plugin.getLogger().log(Level.WARNING, "Could not teleport hardcoreplayer to \"" + wldstr + "\": World not found!");
			}
		}
	}
	
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuitEvent(PlayerQuitEvent event)
	{
		if(playersToBan != null)
		{
			String eventpstr = event.getPlayer().getName();
			Iterator<String> it = playersToBan.iterator();
			
			while(it.hasNext())
			{
				String pstr = it.next();
				
				if(eventpstr.equals(pstr))
				{
					
					if(isEnabledWorld(event.getPlayer().getWorld().getName()))
					{
						plugin.getServer().getPlayer(eventpstr).setBanned(true);
					}
					it.remove();
				}
			}
		}
	}
}
