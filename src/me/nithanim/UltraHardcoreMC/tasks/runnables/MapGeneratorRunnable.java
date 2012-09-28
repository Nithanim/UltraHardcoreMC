package me.nithanim.UltraHardcoreMC.tasks.runnables;

import java.util.LinkedList;
import java.util.Queue;

import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;
import me.nithanim.util.Locationx;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;



public class MapGeneratorRunnable implements Runnable {
	
	private World world;
	private UltraHardcoreMC plugin;
	private CommandSender sender;
	
	private Vector point1;
	private Vector point2;
	
	private Queue<Chunk> queue = new LinkedList<Chunk>();
	private Chunk lastChunk;
	
	private State state;
	
	private CollectionState collectingState;
	
	public MapGeneratorRunnable(World world, UltraHardcoreMC plugin, CommandSender sender)
	{
		this.world = world;
		this.plugin = plugin;
		this.sender = sender;
		
		
		Location midpoint = world.getSpawnLocation();
		int radius = plugin.getConfig().getInt("border.radius");
		Vector radiusvec = new Vector(radius, 0, radius);
		
		if(plugin.getConfig().getBoolean("border.midpoint.enabled"))
		{
			Vector confvec = plugin.getConfig().getVector("border.midpoint.location");
			if(confvec != null)
				midpoint = new Locationx(world, confvec);
		}
		
		Vector mid = midpoint.toVector();
		point1 = mid.clone().subtract(radiusvec);
		point2 = mid.add(radiusvec);
		
		state = State.COLLECTING;
		
		
		System.out.println("Generating queue...");
		collectingState = new CollectionState(point1.getBlockX()/16, point2.getBlockX()/16, point1.getBlockZ()/16, point2.getBlockZ()/16);
		System.out.println("Adding chunks from " + collectingState.currX + "," + collectingState.currZ + " to " + collectingState.maxX + "," + collectingState.maxZ);
	}

	
	
	
	@Override
	public void run()
	{
		if(state == State.COLLECTING)
		{
			for( ; collectingState.currX <= collectingState.maxX ; collectingState.currX++)
			{
				for( ; collectingState.currZ <= collectingState.maxZ ; collectingState.currZ++)
				{
					//System.out.println("Adding chunk " + collectingState.currX + "," + collectingState.currZ);
					queue.add(world.getChunkAt(collectingState.currX, collectingState.currZ));
					
					if(collectingState.currZ == collectingState.maxZ )
					{
						collectingState.currZ = collectingState.minZ;
						break;
					}
					
					if(collectingState.currZ%10 == 0) 
					{
						collectingState.currZ++;
						return; //prevent server not responding
					}
				}
				
				if(collectingState.currX%10 == 0)
				{
					collectingState.currX++;
					return; //prevent server not responding
				}
			}
			
			if(collectingState.currX > collectingState.maxX) //begin of next, not existing cycle
			{
				System.out.println("Generating queue finished! " + queue.size() + " chunks to generate...");
				state = State.WORKING;
			}
		}
		else
		{
			
			Chunk chunk = null;
			
			for(int i=0 ; i<100 ; i++) //prevent server not responding
				
			{
				chunk = queue.poll();
				
				if(chunk == null && lastChunk == null)
				{
					plugin.mapGeneratorTask.cancleTask();
					plugin.mapGeneratorTask = null;
					
					sender.sendMessage("Chunkgeneration finished!");
					return;
				}
				
				if(lastChunk != null)
				{
					lastChunk.unload(true, true);
					lastChunk = null;
				}
				
				if(chunk != null)
				{
					chunk.load();
					lastChunk = chunk;
				}
			}
			System.out.print("Cycle finished, " + queue.size() + "Chunks to go.");
		}
	}
	
}
enum State {
	COLLECTING,
	WORKING
}
class CollectionState
{
	public final int minX;
	public final int minZ;
	public final int maxX;
	public final int maxZ;
	
	public int currX;
	public int currZ;
	
	public CollectionState(int minX, int maxX, int minZ, int maxZ)
	{
		this.minX = minX-1;
		this.minZ = minZ-1;
		this.currX = minX;
		this.currZ = minZ;
		this.maxX = maxX+1;
		this.maxZ = maxZ+1;
	}
}
