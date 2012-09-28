package me.nithanim.UltraHardcoreMC.listeners;

import java.util.List;

import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;


public abstract class SpecificWorldsListener extends GeneralListener {
	
	private static List<String> worlds;
	
	
	public SpecificWorldsListener(UltraHardcoreMC plugin)
	{
		super(plugin);
	}
	
	public static boolean isEnabledWorld(String world)
	{
		for(String w : SpecificWorldsListener.worlds)
		{
			if(world.equals(w))
				return true;
		}
		return false;
	}
	
	public static List<String> getWorlds()
	{
		return worlds;
	}
	
	public static void setWorldsList(List<String> worlds)
	{
		SpecificWorldsListener.worlds = worlds;
		/*if(SpecificWorldsListener.worlds == null)
			SpecificWorldsListener.worlds = new ArrayList<String>(worlds.size());
		
		SpecificWorldsListener.worlds.clear();
		for(String w : worlds)
		{
			SpecificWorldsListener.worlds.add(w);
		}*/
	}

	public static void deleteWorldsList()
	{
		SpecificWorldsListener.worlds = null;
	}
}
