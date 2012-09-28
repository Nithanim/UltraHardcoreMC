package me.nithanim.UltraHardcoreMC.commands;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public abstract class BasicCommandExecutor
{
	protected final UltraHardcoreMC plugin;

	
	public BasicCommandExecutor(UltraHardcoreMC plugin) {
		this.plugin = plugin;
	}
	
	/* ##########################
	 * ##### HANDLER ITSELF #####
	 * ########################## */
	
	public boolean onCommand(CommandSender sender, List<String> args)
	{
		if(args.size() >= 1) //a work was given
		{
			String firstArg = args.get(0).toLowerCase(); //convert to lowercase for method names
			
			for (Method m: getCommandList()) //search on every method a command annotation
			{
				Command a = m.getAnnotation(Command.class);
				
				if(m.getName().equals(firstArg)) //is the method is responsible for issued command?
				{
					
					if(hasValidArgs(a, args)) //provided the sender enough arguments? This is only here because of CommandException
					{
						try
						{
							if(hasPermission(sender, a, args)) //should maybe swap place with hasValidArgs()
							{
								try {
									args.remove(0); //already processed first arg
									m.invoke(this, sender, args); //run!
								}
								catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
						catch (CommandException e)
						{
							sender.sendMessage(e.getMessage());
						}
					}
					else
					{
						sendHelp(sender, a);
					}	
					return true;
				}
				
			}
			sender.sendMessage(new StringBuilder(40).append("Subcommand \"").append(args.get(0)).append("\" was not found!").toString());
		}
		else
		{
			help(sender, args);
		}
		return true;
	}
	
	private boolean hasPermission(CommandSender sender, Command ann, List<String> args) throws CommandException
	{
		if(hasBukkitPermission(sender, ann)) //has the sender permissions?
		{
			if(ann.needsPlayer())
			{
				if(! (sender instanceof Player))
				{
					throw new CommandException("You need to be a player to use this command!");
				}
			}
			
			if(hasValidArgs(ann, args)) //provided the sender enough arguments? This is only here because of CommandException
			{
				return true;
			}
			else
			{
				throw new CommandException(ann.help());
			}
		}
		else
		{
			throw new CommandException("You don't have permissions to use this command!");
		}
	}
	
	private boolean hasBukkitPermission(CommandSender sender, Command ann) {
		
		if(ann.permissions().length == 0) //no permissions needed
		{
			return true;
		}
		
		if(!(sender instanceof Player)) //assume it came from console
		{
			return true;
		}
		
		Player p = (Player) sender;
		for(String perm : ann.permissions()) //check for bukkit permission; OR system, convert to AND?
		{
			if(p.hasPermission(perm))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private List<Method> getCommandList()
	{
		List<Method> cmdlist = new ArrayList<Method>();
		
		for (Method m: this.getClass().getMethods()) //search in every method for a command annotation
		{
			Command ann = m.getAnnotation(Command.class);
			
			if(ann != null) //is a command method
			{
				cmdlist.add(m);
			}
			
		}
		return cmdlist;
	}
	
	private static boolean hasValidArgs(Command ann, List<String> args)
	{
		int numargs = args.size()-1; //don't count command itself
		
		int maxargs = ann.maxArgs();
		int minargs = ann.minArgs();
		
		if(maxargs < minargs)
			maxargs = minargs;		
		
		
		if(numargs < minargs)
		{
			return false;
		}
		
		if(maxargs != -1)
		{
			if(numargs > maxargs)
			{
				return false;
			}
		}
		return true;
	}
	
	private static void sendHelp(CommandSender sender, Command a)
	{
		String[] args = a.args();
		StringBuilder msg = new StringBuilder(" Arguments:\n");
		
		for(String arg : args)
		{
			msg.append("  ").append(arg).append("\n");
		}
		
		sender.sendMessage(a.help());
		sender.sendMessage(msg.toString());
	}
	
	
	/* ##############################
	 * ########## COMMANDS ##########
	 * ############################## */
	
	@Command()
	public void list(CommandSender sender, List<String> args)
	{
		sender.sendMessage("Available subcommands:");
		StringBuilder sb = new StringBuilder();
		for(Method m : getCommandList())
		{
			sb.setLength(0);
			sender.sendMessage(sb.append(" - ").append(m.getName()).toString());
		}
	}
	
	@Command(permissions={"ultrahardcoremc.gamemaster"})
	public void help(CommandSender sender, List<String> args)
	{
		list(sender, args);
	}
	
	
	/* ################################
	 * ########## ADDITIONAL ##########
	 * ################################ */
	
	class CommandException extends Throwable
	{
		private static final long	serialVersionUID	= -580807186498951992L;
		
		public CommandException(String str)  
		{
			super(str);
		}
	}
	
	@java.lang.annotation.Target(value={java.lang.annotation.ElementType.METHOD})
	@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
	protected @interface Command 
	{
		boolean needsPlayer() default false;
		String[] permissions() default {};
		
		String help() default "No usage found!";
		String[] args() default "No arguments";
		
		int minArgs() default 0;
		int maxArgs() default 0;
	}
}

