package me.nithanim.UltraHardcoreMC.listeners;

import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;

import org.bukkit.event.Listener;


public abstract class GeneralListener implements Listener {
	
	protected final UltraHardcoreMC plugin;
	
	
	public GeneralListener(UltraHardcoreMC plugin)
	{
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
}
