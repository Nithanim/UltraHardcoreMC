package me.nithanim.UltraHardcoreMC.tasks.runnables;

import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;

import org.bukkit.Server;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;


public class MarkRunnable implements Runnable {
	
	private UltraHardcoreMC plugin;
	
	public MarkRunnable() {
		this.plugin = UltraHardcoreMC.getPlugin();
		
		Configuration memory = plugin.getHandler().getMemory();
		
		if (memory.getInt("mark.time", 0) == 0) {
			memory.set("mark.lasttime",	(int) (System.currentTimeMillis() / 1000));
		}
	}
	
	@Override
	public void run() {
		Server server = plugin.getServer();
		
		FileConfiguration config = plugin.getConfig();
		Configuration memory = plugin.getHandler().getMemory();
		
		int marknr = memory.getInt("mark.nr");
		int markdelay = config.getInt("marks.delay");
		
		memory.set("mark.nr", ++marknr);
		memory.set("mark.time", (int) (System.currentTimeMillis() / 1000));
		
		server.dispatchCommand(server.getConsoleSender(), "say Mark " + marknr + " (" + marknr * markdelay + "min)");
	}
	
}
