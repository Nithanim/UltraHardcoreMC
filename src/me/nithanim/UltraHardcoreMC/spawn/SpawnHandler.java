package me.nithanim.UltraHardcoreMC.spawn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;



public class SpawnHandler {
	
	private UltraHardcoreMC plugin;
	
	
	private Map<String, Spawn> playerMap = new HashMap<String, Spawn>();
	private List<Team> teams = new ArrayList<Team>();
	
	
	
	
	public SpawnHandler(UltraHardcoreMC plugin)
	{
		this.plugin = plugin;
	}
	
	
	private Spawn createNewSpawn() throws SpawnCreationException
	{
		return SpawnFactory.getInstance().createNewSpawn();
	}

	public boolean spawnPlayer(Player p, final boolean newspawn) throws SpawnCreationException
	{
		String  pname = p.getName();
		Team    pteam = getPlayerTeam(pname);
		Spawn  pspawn = getPlayerSpawn(pname);
		
		
		
	
		if(newspawn) //requested new spawnpoint
		{
			if(pteam != null) //is in team
			{
				if(isTeamLeader(pname, pteam))
				{
					pteam.setSpawn(createNewSpawn());
				}
				else
				{
					throw new SpawnCreationException("You can't create a new spawn point because you are in a team!");
				}
			}
			else
			{
				pspawn = playerMap.put(pname, createNewSpawn());
			}
		}
		else //fetch old one
		{
			if(pspawn == null) //teamspawn can't be null
			{
				pspawn = createNewSpawn();
				playerMap.put(pname, pspawn);
			}
		}
		
		
		p.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 10));
		p.teleport(pspawn.getLocation());
		return true;
	}

	public boolean spawnPlayer(Player p)
	{
		try
		{
			return spawnPlayer(p, false);
		}
		catch (SpawnCreationException e) { } //Exeption cannot occour
		return false;
	}
	
	/**
	 * Searches for a defined spawn for a specific player. Searches in playerlist and teamlist.
	 * @param playername Name of player
	 * @return Spawn on success, null on failure
	 */
	private Spawn getPlayerSpawn(String playername)
	{
		Spawn pspawn = playerMap.get(playername);
		
		if(pspawn != null) 
		{
			return pspawn;
		}
		else //player has no individual spawn
		{
			Team t = getPlayerTeam(playername);
			if(t != null)
			{
				return t.getSpawn();
			}
			else
			{
				return null;
			}
		}
		
	}
	
	public Team createNewTeam(String player) throws SpawnCreationException
	{
		if(isInTeam(player))
			throw new IllegalArgumentException("Player is already in a Team!");
		
		
		Team t = new Team(createNewSpawn(), player);
		teams.add(t);
		return t;
	}
	
	public Team getTeamFromLeader(String leader)
	{
		for(Team t : teams)
		{
			if(t.getLeader().equals(leader))
			{
				return t;
			}
			
		}
		return null;
	}
	
	public boolean isTeamLeader(String player, Team team)
	{
		if(team == null || player == null)
		{
			return false;
		}
		return player.equals(team.getLeader());
	}
	
	public boolean isInTeam(String player)
	{
		return getPlayerTeam(player) != null;
	}
	
	public Team getPlayerTeam(String player)
	{
		for(Team t : teams) //look in every team
		{
			for(String p : t.getPlayers())
			{
				if(player.equals(p)) //and find out if he is in any
				{
					return t; //the team spawn is the player spawn
				}
			}
		}
		return null;
	}
	
	/** Gets all spawns.
	 * 
	 * @return A list containing player and team spawns.
	 */
	public List<Spawn> getSpawns()
	{
		List<Spawn> l = new ArrayList<Spawn>(playerMap.values());
		for(Team t : teams)
		{
			l.add(t.getSpawn());
		}
		return l;
	}
	
	public void sendTeamMessage(Team t, String msg)
	{
		org.bukkit.Server s = plugin.getServer();
		
		Player p;
		for(String ps : t.getPlayers())
		{
			p = s.getPlayer(ps);
			if(p != null)
				p.sendMessage(msg);
		}
	}


	public void removeTeam(String leader)
	{
		Iterator<Team> it = teams.iterator();
		
		Team t = null;
		while(it.hasNext())
		{
			t = it.next();
			if(t.getLeader().equals(leader))
			{
				it.remove();
				return;
			}
		}
	}
}
