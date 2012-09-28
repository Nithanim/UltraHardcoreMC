package me.nithanim.UltraHardcoreMC.commands;

import java.util.ArrayList;
import java.util.List;

import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;


public class DowngradeCommandExecutor implements CommandExecutor{
	
	private final MainCommandExecutor mainexec;
	
	public DowngradeCommandExecutor(UltraHardcoreMC plugin)
	{
		this.mainexec = new MainCommandExecutor(plugin);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args)
	{
		List<String> arguments = new ArrayList<String>(args.length);
		for(String arg : args)
		{
			arguments.add(arg);
		}
		return mainexec.onCommand(sender, arguments);
	}
	
}
