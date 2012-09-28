package me.nithanim.UltraHardcoreMC.commands;

import java.util.Iterator;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;
import me.nithanim.UltraHardcoreMC.spawn.SpawnHandler;
import me.nithanim.UltraHardcoreMC.spawn.Team;


public class TeamCommandExecutor extends BasicCommandExecutor {
	
	private final SpawnHandler sh;
	
	public TeamCommandExecutor(UltraHardcoreMC plugin)
	{
		super(plugin);
		sh = plugin.getSpawnHandler();
	}
	
	
	@Command(needsPlayer = true, help="Creates a new team.")
	public void create(CommandSender sender, List<String> args)
	{
		Player p = (Player)sender;
		
		if(!sh.isInTeam(p.getName()))
		{
			try
			{
				sh.createNewTeam(p.getName());
				p.sendMessage("You successfully created a Team!");
			}
			catch (me.nithanim.UltraHardcoreMC.spawn.SpawnCreationException e)
			{
				if(e.getMessage() != null)
				{
					p.sendMessage(e.getMessage());
				}
				else
				{
					p.sendMessage("An arror occoured!");
					e.printStackTrace();
				}
			}
		}
		else
		{
			p.sendMessage("You are already in a team!");
		}
	}
	
	@Command(needsPlayer = true, help="Joins an existing team.", minArgs=1, args={"<playername> - Name of a player who created a team (case sensitive!)"})
	public void join(CommandSender sender, List<String> args)
	{
		Player p = (Player)sender;
		Team t = sh.getPlayerTeam(p.getName());
		
		if(t == null) //player is not in a team
		{
			Team lt = sh.getTeamFromLeader(args.get(0)); //LeaderTeam
			
			if(lt != null) //targetted player is leader of a team
			{
				lt.addPlayer(p.getName());
				p.sendMessage("Successfully joined team of " + lt.getLeader() + '!');
			}
			else
			{
				p.sendMessage(args.get(0) + " is not a teamleader!");
			}
		}
		else //player is in a team
		{
			if(p.getName().equals(t.getLeader()))
			{
				p.sendMessage("You are already a leader of a team!");
			}
			else
			{
				p.sendMessage("You are already in the team of " + t.getLeader() + '!');
			}
		}
		
	}
	
	@Command(needsPlayer = true, help="Leave a team.")
	public void leave(CommandSender sender, List<String> args)
	{
		Player p = (Player)sender;
		Team t = sh.getPlayerTeam(p.getName());
		
		if(t != null)
		{
			if(!t.getLeader().equals(p.getName())) //not the leader
			{
				t.getPlayers().remove(p.getName()); //TODO prove working
				p.sendMessage("You left the team of " + t.getLeader() + '!');
				sh.sendTeamMessage(t, p.getName() + " left the team!");
			}
			else //remove team
			{
				//remove leader from team-list to be able to send separate messages
				Iterator<String> pit = t.getPlayers().iterator();
				while(pit.hasNext())
				{
					if(pit.next().equals(t.getLeader()))
					{
						pit.remove();
						break;
					}
				}
				
				sh.sendTeamMessage(t, t.getLeader() + " removed the team!");
				plugin.getServer().getPlayer(t.getLeader()).sendMessage("You removed your team!");
				sh.removeTeam(t.getLeader());
			}
		}
		else
		{
			p.sendMessage("You are not in a team!");
		}
		
	}
}
