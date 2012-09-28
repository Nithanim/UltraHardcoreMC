package me.nithanim.UltraHardcoreMC.listeners;

import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;
import me.nithanim.UltraHardcoreMC.tasks.DelayedSyncTask;
import me.nithanim.UltraHardcoreMC.tasks.runnables.PlayerlistUpdaterRunnable;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;



public class PlayerlistListener extends SpecificWorldsListener {
	

	public PlayerlistListener(UltraHardcoreMC plugin)
	{
		super(plugin);
	}
	
	
	private void updatePlayerlist(Player p)
	{
		updatePlayerlist(p, null);
	}
	
	private void updatePlayerlist(Player p, org.bukkit.event.Event e)
	{
		if(isEnabledWorld(p.getWorld().getName()))
			new DelayedSyncTask(plugin, new PlayerlistUpdaterRunnable(p, e), 0);
	}
	
	
	
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		if(!isEnabledWorld(event.getPlayer().getWorld().getName())) return;
		
		updatePlayerlist(event.getPlayer());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerDamage(EntityDamageEvent event)
	{
		Entity e = event.getEntity();
		if(e instanceof Player)
		{
			if(!isEnabledWorld(e.getWorld().getName())) return;
			
			Player p = (Player)e;
			updatePlayerlist(p);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onGamemodeChange(PlayerGameModeChangeEvent event)
	{
		if(!isEnabledWorld(event.getPlayer().getWorld().getName())) return;
		updatePlayerlist(event.getPlayer());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onHealthRegen(EntityRegainHealthEvent event)
	{
		Entity e = event.getEntity();
		if(e instanceof Player)
		{
			if(!isEnabledWorld(e.getWorld().getName())) return;
			
			Player p = (Player)e;
			updatePlayerlist(p);
		}
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		if(!isEnabledWorld(event.getPlayer().getWorld().getName())) return;
		
		updatePlayerlist(event.getPlayer());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent event)
	{
		updatePlayerlist(event.getPlayer(), event);
	}

}
