package me.nithanim.UltraHardcoreMC;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class PlayerlistHandler {
	
	private UltraHardcoreMC plugin;

	public PlayerlistHandler(UltraHardcoreMC plugin) {
		this.plugin = plugin;
		
		plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new HealthUpdaterTask(plugin), 0, 5*20);
	}
	
	
	
	private class HealthUpdaterTask implements Runnable
	{
		private UltraHardcoreMC plugin;
		private static final int NAME_CHAR_LIMIT = 16;
		private static final int RESERVED_COLOUR_LENGTH = 2;
		private static final int RESERVED_HEALTH_LENGTH = 2;
		
		public HealthUpdaterTask(UltraHardcoreMC plugin) {
			this.plugin = plugin;
		}


		@Override
		public void run() {
			Player[] players = plugin.getServer().getOnlinePlayers();
			
			StringBuilder row = new StringBuilder(); 
			
			for(Player p : players)
			{
				row.setLength(0);
				
				
				if(p.getGameMode() == GameMode.SURVIVAL) //for regular players
				{
					int health = p.getHealth();
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
					row.append("Sp:");
				}
				
				row.append(ChatColor.RESET);
				row.append(':');
				row.append(p.getName());
				
				
				if(row.length()>=NAME_CHAR_LIMIT)
				{
					row.setLength(NAME_CHAR_LIMIT);
				}
				
				p.setPlayerListName(row.toString());	
			}
		}
	}

}

