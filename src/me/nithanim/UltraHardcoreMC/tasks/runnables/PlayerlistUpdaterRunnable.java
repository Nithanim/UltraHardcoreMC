package me.nithanim.UltraHardcoreMC.tasks.runnables;

import me.nithanim.UltraHardcoreMC.listeners.SpecificWorldsListener;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerTeleportEvent;


public class PlayerlistUpdaterRunnable implements Runnable {
	
	private Player	player;
	private Event event;
	
	private static final int NAME_CHAR_LIMIT = 16;
	
	public PlayerlistUpdaterRunnable(Player player, Event event)
	{
		this.player = player;
		this.event = event;
	}
	
	@Override
	public void run()
	{
		StringBuilder row = new StringBuilder(20);
		
		if(event != null && event instanceof PlayerTeleportEvent) //player teleports between worlds
		{
			if(!SpecificWorldsListener.isEnabledWorld( ((PlayerTeleportEvent)event).getTo().getWorld().getName()) )
			{
				player.setPlayerListName(player.getName());
				return;
			}
		}
		
		
		if(player.getGameMode() == GameMode.SURVIVAL) //for regular players with hearts
		{
			int health = player.getHealth();
			ChatColor color = ChatColor.GREEN;
			
			
			//get appropriate chatcolor
			if(health<=10)
				color = ChatColor.GOLD;
			if(health<6)
				color = ChatColor.RED;
			
			
			
			row.append(color);
			row.append(health);
			
		}
		else
		{
			row.append(ChatColor.BLUE);
			row.append("Sp");
		}
		
		row.append(ChatColor.RESET);
		row.append(':');
		row.append(player.getName());
		
		
		if(row.length()>=NAME_CHAR_LIMIT)
		{
			row.setLength(NAME_CHAR_LIMIT);
		}
		
		try
		{
			player.setPlayerListName(row.toString());
		}
		catch(java.lang.IllegalArgumentException e)
		{
			row.setCharAt(NAME_CHAR_LIMIT-1, '2');
			player.setPlayerListName(row.toString());
		}
	}
}
