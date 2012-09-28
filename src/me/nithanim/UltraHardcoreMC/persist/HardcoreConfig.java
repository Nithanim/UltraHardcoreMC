package me.nithanim.UltraHardcoreMC.persist;

import java.io.File;

import me.nithanim.UltraHardcoreMC.Gamestate;
import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;



public class HardcoreConfig extends MyConfigHandler{

	public HardcoreConfig(UltraHardcoreMC plugin) 
	{
		super(new File(plugin.getDataFolder(),"memory.yml"));
	}

	@Override
	public void setConfigDefaults() {
		getConfig().options().copyDefaults(true);
		getConfig().addDefault("game.state", Gamestate.NONE.ordinal());
	}
	
	
	
}
