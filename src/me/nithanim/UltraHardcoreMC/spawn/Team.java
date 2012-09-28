package me.nithanim.UltraHardcoreMC.spawn;

import java.util.ArrayList;
import java.util.List;


public class Team {
	
	private List<String> players;
	
	/** Leader must always be in players list! */
	private String leader;
	private Spawn spawn;
	
	
	Team(Spawn spawn, String leader) throws SpawnCreationException
	{
		if(leader == null || spawn == null)
			throw new IllegalArgumentException();
		
		this.players = new ArrayList<String>();
		
		this.leader = leader;
		this.players.add(leader);
		this.spawn = spawn;
	}
	
	public void addPlayer(String player)
	{
		this.players.add(player); 
	}
	
	
	public Spawn getSpawn()
	{
		return spawn;
	}
	
	public List<String> getPlayers()
	{
		return players;
	}

	public String getLeader()
	{
		return leader;
	}

	public void setSpawn(Spawn spawn)
	{
		if(spawn == null)
			throw new IllegalArgumentException("Spawn must not be null!");
		this.spawn = spawn;
	}
}
