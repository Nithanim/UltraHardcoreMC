package me.nithanim.UltraHardcoreMC.commands;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;
import me.nithanim.UltraHardcoreMC.commands.commands.MainCommands;


public class CommandManager implements CommandExecutor
{
	@SuppressWarnings("unused")
	private UltraHardcoreMC plugin;
	
	/**
	 * Stores all registered commands. Key is the parent Method of a Map of submethods
	 */
	public Map<Method,Map<String,Method>> commands = new HashMap<Method, Map<String, Method>>();
	
	
	
	public CommandManager(UltraHardcoreMC plugin)
	{
		registerCommands(MainCommands.class, null);
	}
	
	
	
	public void registerCommands(Class<? extends CommandContainer> clazz, Method parent)
	{
		//retrieving a List of all commands
		List<Method> cmdlist = new ArrayList<Method>();
		for (Method m: clazz.getClass().getMethods()) //search in every method for a command annotation
		{
			Command ann = m.getAnnotation(Command.class);
			
			if(ann != null) //is a command method
			{
				cmdlist.add(m);
			}
		}
		
		//getting map
		Map<String, Method> cmdmap = commands.get(parent);
		if(cmdmap == null)
		{
			cmdmap = new HashMap<String, Method>();
			commands.put(parent, cmdmap);
		}
		
		//adding commands to it
		for (Method m: cmdlist)
		{
			//TODO
			cmdmap.put(null , m);
		}
	}



	@Override
	public boolean onCommand(CommandSender arg0, org.bukkit.command.Command arg1, String arg2, String[] arg3)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
