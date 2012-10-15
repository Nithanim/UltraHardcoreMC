package me.nithanim.UltraHardcoreMC.commands;


import java.util.List;

import me.nithanim.UltraHardcoreMC.Gamestate;
import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;
import me.nithanim.UltraHardcoreMC.listeners.SpecificWorldsListener;
import me.nithanim.UltraHardcoreMC.spawn.SpawnCreationException;
import me.nithanim.UltraHardcoreMC.tasks.RepeatingSyncTask;
import me.nithanim.UltraHardcoreMC.tasks.runnables.MapGeneratorRunnable;

import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;


public class MainCommandExecutor extends BasicCommandExecutor
{
	private final TeamCommandExecutor teamexec;
	
	public MainCommandExecutor(UltraHardcoreMC plugin) {
		super(plugin);
		teamexec = new TeamCommandExecutor(plugin);
	}

	/* ##############################
	 * ########## COMMANDS ##########
	 * ############################## */
	
	@Command(permissions={"ultrahardcoremc.gamemaster"}, help="Pregenerates all chunks within the border.", minArgs=0, args="<worldname>")
	public void generate(CommandSender sender, List<String> args)
	{
		if(plugin.mapGeneratorTask != null)
		{
			sender.sendMessage("There is already a generation task running!");
		}
		else
		{
			String name = null;
			for(World world : plugin.getServer().getWorlds())
			{
				name = world.getName();
				if(name.equals(args.get(0)))
				{
					if(SpecificWorldsListener.isEnabledWorld(name))
					{
						sender.sendMessage("Chunk generation started...");
						plugin.mapGeneratorTask = new RepeatingSyncTask(plugin, new MapGeneratorRunnable(plugin.getServer().getWorlds().get(0), plugin, sender), 20, 1);
						return;
					}
					else
					{
						sender.sendMessage("World is not a harcoreworld!");
						return;
					}
				}
			}
			sender.sendMessage("No world with the name \"" + args.get(0) + "\" found.");
		}
	}
	
	@Command(needsPlayer = true, help="Spawns yourself at your spawn position (creates one if not set yet)", minArgs=0, maxArgs=1, args="[\"new\"]  - if specified, a new point will be created for you.")
	public void spawn(CommandSender sender, List<String> args)
	{
		if(!plugin.getConfig().getBoolean("spawn.enabled"))
		{
			sender.sendMessage("This functionality is disabled.");
			return;
		}
		
		Configuration memory = plugin.getHandler().getMemory(); 
		
		if(memory.getInt("game.state") != Gamestate.NONE.ordinal())
		{
			sender.sendMessage("You can only use this command if no uhc is going on!");
			return;
		}
		
		
		
		boolean newspawn = false;
		if(args.size() == 1)
		{
			if(args.get(0).equalsIgnoreCase("new"))
			{
				newspawn = true;
			}
			else
			{
				sender.sendMessage("Only \"new\" is permitted as argument");
			}
		}
		
		try
		{
			if(plugin.getSpawnHandler().spawnPlayer((Player)sender, newspawn) )
			{
				sender.sendMessage("You are now at your spawn point.");
			}
		}
		catch (SpawnCreationException e)
		{
			sender.sendMessage(e.getMessage());
		}
		
	}
	
	@Command(needsPlayer = true, maxArgs=-1)
	public void team(CommandSender sender, List<String> args)
	{
		if(plugin.getConfig().getBoolean("spawn.teams.enabled"))
		{
			teamexec.onCommand(sender, args);
		}
		else
		{
			sender.sendMessage("This functionality is disabled.");
			return;
		}
		
	}
	
	@Command(permissions={"ultrahardcoremc.gamemaster"}, help="Starts a new UHC game.", minArgs = 1, args="<delay><s|m|h> - if 0 the game will immediately start. e.g. 5m, 1m, 30s, ...")
	public void start(CommandSender sender, List<String> args)
	{
		try
		{
			StringBuilder sb = new StringBuilder(args.get(0));
			int multiplier = 1;
			
			switch(sb.charAt(sb.length()-1)) //find out about timemeasurement
			{
				case 's':
					break;
				case 'm':
					multiplier = 60;
					break;
				case 'h':
					multiplier = 60 * 60;
					break;
				default:
					sender.sendMessage("You need to specify a valid timemeasurement!");
					return;
			}
			
			try
			{
				int timeInSec = Integer.parseInt(sb.substring(0, sb.length()-1)) * multiplier;
				
				if(!plugin.getHandler().startCountdown(timeInSec)) //no success needed because of server msg
				{
					sender.sendMessage("Unable to start game! Is is already running?");
				}
			}
			catch(NumberFormatException e)
			{
				sender.sendMessage("Unable to read time correctly");
			}
		}
		catch(Exception e)
		{
			if(e instanceof NullPointerException)
			{
				e.printStackTrace();
			}
			else
			{
				sender.sendMessage(e.getMessage());
			}
		}
	}
	
	@Command(permissions={"ultrahardcoremc.gamemaster"}, help="Pauses a UHC game.")
	public void pause(CommandSender sender, List<String> args)
	{
		if(plugin.getHandler().pauseGame())
		{
			//handled in method above
			//plugin.sayServer("Game paused!");
		}
		else
		{
			sender.sendMessage("Game couldn't be paused!");
		}
	}
	
	@Command(permissions={"ultrahardcoremc.gamemaster"}, help="Pauses a UHC game.")
	public void stop(CommandSender sender, List<String> args)
	{
		if(plugin.getHandler().stopGame())
		{
			plugin.sayServer("Game stopped!");
		}
		else
		{
			sender.sendMessage("Game couldn't be stopped!");
		}
	}
	
	
	@Command(permissions={"ultrahardcoremc.gamemaster"}, help="Resumes a previously paused game.")
	public void resume(CommandSender sender, List<String> args)
	{
		if(!plugin.getHandler().resumeGame()) //no success needed because of server msg
		{
			sender.sendMessage("Game couldn't be resumed!");
		}
	}
	
}

