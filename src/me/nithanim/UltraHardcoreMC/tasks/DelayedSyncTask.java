package me.nithanim.UltraHardcoreMC.tasks;

import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;

import org.bukkit.Bukkit;


/**
 * Represents a task in the bukkit taskscheduler
 * @author Konstantin
 *
 */
public class DelayedSyncTask {
	
	protected  int taskid;
	
	public DelayedSyncTask(int taskid)
	{
		this.taskid = taskid;
	}
	
	public DelayedSyncTask(UltraHardcoreMC plugin, Runnable runnable, int delay)
	{
		this(Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, runnable, delay));
	}
	
	public void cancleTask()
	{
		Bukkit.getScheduler().cancelTask(taskid);
	}
}
