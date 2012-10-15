package me.nithanim.UltraHardcoreMC;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import me.nithanim.UltraHardcoreMC.listeners.SpecificWorldsListener;
import me.nithanim.UltraHardcoreMC.persist.HardcoreConfig;
import me.nithanim.UltraHardcoreMC.tasks.RepeatingSyncTask;
import me.nithanim.UltraHardcoreMC.tasks.runnables.AutosaveRunnable;
import me.nithanim.UltraHardcoreMC.tasks.runnables.CountdownRunnable;
import me.nithanim.UltraHardcoreMC.tasks.runnables.MarkRunnable;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;




public class HardcoreHandler {
	private UltraHardcoreMC plugin;
	private HardcoreConfig hardcoreconfig;
	
	@SuppressWarnings("unused")
	private RepeatingSyncTask autosaveTask;
	public RepeatingSyncTask countdownTask;
	private RepeatingSyncTask marksTask;

	
	public HardcoreHandler(UltraHardcoreMC plugin) 
	{
		this.plugin = plugin;
		hardcoreconfig = new HardcoreConfig(plugin);
		
		
		//init autosave for memory:
		int saveinterval = plugin.getConfig().getInt("saveinterval")   * 60 * 20;
		autosaveTask = new RepeatingSyncTask(plugin, new AutosaveRunnable(),  saveinterval, saveinterval);
	}
	
	/**
	 * Starts the Task for marks for the players
	 * @param firstdelay - time until the next the mark is reached
	 * @param repeateddelay - standard time between marks
	 */
	public void startMarksTask(int firstdelay, int repeateddelay)
	{
		marksTask = new RepeatingSyncTask(plugin, new MarkRunnable(), firstdelay, repeateddelay);
	}
	
	
	/**
	 * Starts countdown as requested by a gamemaster/console
	 * @param secUntilStart Seconds until game should start
	 * @return true on success
	 * @throws IllegalStateException 
	 */
	public boolean startCountdown(int secUntilStart)
	{
		if(getMemory().getInt("game.state") == Gamestate.NONE.ordinal())
		{
			getMemory().set("game.state", Gamestate.COUNTDOWN.ordinal());
			
			
			if(secUntilStart > 0)
			{
				//start the countdown
				countdownTask = new RepeatingSyncTask(plugin, new CountdownRunnable(secUntilStart), 0, 1*20);
			}
			else if(secUntilStart == 0)
			{
				//skip countdown and start now!
				startGame();
			}
			else
			{
				throw new IllegalArgumentException("Only positive integer allowed!");
			}
			return true;
		}
		else
		{
			throw new IllegalStateException("There is already a game going on!");
		}
		
	}
	
	/**
	 * Must not be called except from delayed task from startCountdown()!
	 * @return true on success
	 * @throws IllegalStateException 
	 */
	public boolean startGame()
	{
		if(getMemory().getInt("game.state") == Gamestate.COUNTDOWN.ordinal())
		{
			
			//set memory
			getMemory().set("game.state", Gamestate.RUNNING.ordinal());
			getMemory().set("mark.nr", 0);
			
			getMemory().set("mark.time", (int) (System.currentTimeMillis()/1000));
			
			
			//get the marks going
			Configuration plgconf = plugin.getConfig();
			int delay = plgconf.getInt("marks.delay")*60*20;
			
			if(plgconf.getBoolean("marks.enabled"))
			{
				startMarksTask(delay,delay);
			}
			
			//prepairing players

			
			setHardcorePlayersToNeutralState();
			
			//world
			World world = plugin.getServer().getWorlds().get(0);
			
			world.setTime(0);
			
			world.setThundering(false);
			world.setThunderDuration(0);
			
			world.setStorm(false);
			world.setWeatherDuration(0);
			
			
			//server message
			plugin.sayServer("Game starts now!");
			
			return true;
		}
		throw new IllegalStateException("Game must be in countdown mode!");
		//return false;
	}
	
	/**
	 * Pauses either a running game or stops a countdown.
	 * @return true if game was successfully paused
	 * @throws IllegalStateException if game cannot be pause because of the state
	 */
	public boolean pauseGame()
	{
		if(getMemory().getInt("game.state") == Gamestate.RUNNING.ordinal())
		{
			//set internal flags and save all times to be able to resume at the (exact) point
			getMemory().set("game.state", Gamestate.PAUSED.ordinal());
			getMemory().set("game.pausedrealtime",(int)(System.currentTimeMillis()/1000));
			getMemory().set("game.pausedgametime", plugin.getServer().getWorlds().get(0).getTime());
			
			//stop marks timer
			if(marksTask != null)
			{
				marksTask.cancleTask();
			}
			
			return true;
		}
		else if(getMemory().getInt("game.state") == Gamestate.COUNTDOWN.ordinal())
		{
			getMemory().set("game.state", Gamestate.NONE.ordinal());
			//plugin.getServer().getScheduler().cancelTask(countdownTask);
			countdownTask.cancleTask();
			plugin.sayServer("Countdown stopped!");
			return true;
		}
		//throw new IllegalStateException("Game must be running in order to be able to be paused!");
		return false;
	}
	
	
	/**
	 * Resumes a previous paused game
	 * @return true on success / false on failure
	 */
	public boolean resumeGame()
	{
		if(getMemory().getInt("game.state") == Gamestate.PAUSED.ordinal())
		{
			Configuration conf = plugin.getConfig();
			
			//set internal flags
			getMemory().set("game.state", Gamestate.RUNNING.ordinal());
			
			
			//System.out.print(timeToNextMark);
			if(conf.getBoolean("marks.enabled"))
			{
				//calculate remeaning time to next mark
				int markdelay = conf.getInt("marks.delay")*60;
				int elapsedTimeSinceLastMark = getMemory().getInt("game.pausedrealtime")-getMemory().getInt("mark.time");
				int timeToNextMark = markdelay-elapsedTimeSinceLastMark;
				
				
				
				startMarksTask(timeToNextMark*20, markdelay*20);
				
				
				
				int marknr = getMemory().getInt("mark.nr") + 1;
				
				plugin.sayServer("Game resumed!");
				
				StringBuffer msg = new StringBuffer(33);
				msg.append("Next mark is ").append(marknr).append(" (").append(marknr*(markdelay/60)).append("min) in about ").append( (int) (timeToNextMark/60)).append('m');
				plugin.sayServer(msg.toString());
			}
			
			return true;
		}
		return false;
	}
	
	/**
	 * Completely stops a game and deletes the memory.
	 * @return true if successful
	 */
	public boolean stopGame()
	{
		if(this.pauseGame())
		{
			hardcoreconfig.refreshConfig(); //deletes memory
			return true;
		}
		return false;
	}
	
	
	/**
	 * Tries to restore a game after a servercrash.
	 * Must only be called on startup!
	 * @return true on success
	 */
	public boolean restoreMemoryAfterShutdown() 
	{
		if(getMemory().getInt("game.state") == Gamestate.RUNNING.ordinal()) //Server crashed?
		{
			plugin.getLogger().log(Level.WARNING, "Did the server crash? Trying recovery...");
			
			getMemory().set("game.state", Gamestate.PAUSED.ordinal()); //pause it for a manual resume
			
			//find out last savepoint while ingame 
			int lastKnownTime = -1;
			
			
			/* game was running while autosave (may be newer (5min) than last mark(30min), if server crashed)
			 * to be able to recover as much already played time as possible
			 */
			if(getMemory().getInt("lastautosave.gamestate") == Gamestate.RUNNING.ordinal()) 
			{
				//if the game was running,
				lastKnownTime = Math.max(getMemory().getInt("mark.time"),getMemory().getInt("lastautosave.time")); //find out the nearest time to crash point
				//it's either the time of the last mark or the last autosave
			}
			else
			{
				//don't trust autosave time because it also saves, if game is not running, like in this case!
				lastKnownTime = getMemory().getInt("mark.time");
			}
			
			
			if(lastKnownTime < 0)
			{
				plugin.getLogger().log(Level.SEVERE, "Unable to Recover from crash!");
				plugin.getServer().getPluginManager().disablePlugin(plugin);
				return false;
			}
			else
			{
				getMemory().set("game.pausedrealtime", lastKnownTime); //setting paused time to last known time
				plugin.getLogger().info("Recovery successful! Game was paused an is ready to resume!");
			}
			
		}
		return true;
	}
	
	
	private void setHardcorePlayersToNeutralState()
	{
		if(!plugin.getConfig().getBoolean("spawn.enabled"))
			return;
		
		boolean teleSpawn = plugin.getConfig().getBoolean("spawn.autoTele");
		boolean clearInv = plugin.getConfig().getBoolean("spawn.clearInventory");
		
		List<String> worlds = SpecificWorldsListener.getWorlds();
		List<Player> players = new ArrayList<Player>();
		
		for(String w : worlds)
		{
			World world = plugin.getServer().getWorld(w);
			players.addAll(world.getPlayers());
		}
		
		
		for(Player p : players)
		{
			p.setExhaustion(0);
			p.setFoodLevel(20);
			p.setHealth(20);
			p.setFireTicks(0);
			
			if(p.getGameMode() != GameMode.SURVIVAL)
				p.setGameMode(GameMode.SURVIVAL);
			
			if(clearInv)
				p.getInventory().clear();			
			
			if(teleSpawn)
				plugin.getSpawnHandler().spawnPlayer(p);
		}
	}
	
	public Configuration getMemory()
	{
		return hardcoreconfig.getConfig();
	}
	
	public void saveMemory()
	{
		hardcoreconfig.saveMemory();
	}
	
	public UltraHardcoreMC getPlugin()
	{
		return plugin;
	}
}
