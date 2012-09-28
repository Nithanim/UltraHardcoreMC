package me.nithanim.UltraHardcoreMC.tasks;

import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;

import org.bukkit.Bukkit;


/**
 * Represents a task in the bukkit taskscheduler
 *
 */
public class RepeatingSyncTask extends DelayedSyncTask {
	
	
	public RepeatingSyncTask(int taskid)
	{
		super(taskid);
	}
	
	public RepeatingSyncTask(UltraHardcoreMC plugin, Runnable runnable,	int delay, int period)
	{
		super(Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, runnable, delay, period));
	}

}
