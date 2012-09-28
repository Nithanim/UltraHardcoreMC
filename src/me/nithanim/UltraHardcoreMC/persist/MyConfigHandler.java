package me.nithanim.UltraHardcoreMC.persist;

import java.io.File;
import java.io.IOException;
import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


abstract class MyConfigHandler {
	protected final File configurationFile;
	protected FileConfiguration configuration;
	
	MyConfigHandler (File file)
	{
		this.configurationFile = file;
		refreshConfig();
		setConfigDefaults();
	}
	
	
	public FileConfiguration getConfig()
	{
		return configuration;
	}
	
	abstract public void setConfigDefaults();
	
	
	/**
	 * Deletes the existing Configuration completely and create a new one
	 * @return New Configuration
	 */
	public Configuration refreshConfig()
	{
		configuration = null;
		
		if(configurationFile.exists())
		{
			if(!configurationFile.delete())
			{
				UltraHardcoreMC.getPlugin().getLogger().severe("Unable to delete " + configurationFile.getName());
				return null;
			}
		}
		
		return configuration = YamlConfiguration.loadConfiguration(configurationFile);
	}
	
	
	/**
	 * Saves current memory to file.
	 */
	public void saveMemory()
	{
		try {
			getConfig().save(configurationFile);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
