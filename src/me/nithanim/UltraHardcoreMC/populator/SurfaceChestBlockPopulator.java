package me.nithanim.UltraHardcoreMC.populator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;


public class SurfaceChestBlockPopulator extends BlockPopulator {
	
	//private UltraHardcoreMC plugin;
	private static ItemStack[] chestDrops;
	
	public SurfaceChestBlockPopulator(UltraHardcoreMC plugin) {
		//this.plugin = plugin;
		
		//set chest contents
		if (chestDrops != null)
			chestDrops = null; //reset last contents
		String[] splitted = plugin.getConfig()
				.getString("bonus.surfacechest.contains").split(",");
		List<ItemStack> itemStacks = new ArrayList<ItemStack>(); //dynamic list for adding, stored in static array afterwards
		
		for (int i = 0; i < splitted.length; i++) {
			try {
				int id = Integer.valueOf(splitted[i]);
				itemStacks.add(new ItemStack(id));
			}
			catch (NumberFormatException e) {
				plugin.getLogger().info(
						splitted[i] + " is not a valid item-id!");
			}
		}
		
		chestDrops = itemStacks.toArray(new ItemStack[0]);
		
	}
	
	@Override
	public void populate(World world, Random rnd, Chunk chunk) {
		if (rnd.nextInt(100) < 50) //TODO configureable chance
		{
			Block block = world.getHighestBlockAt(
					chunk.getX() * 16 + rnd.nextInt(16), chunk.getZ() * 16
							+ rnd.nextInt(16));
			Location l = block.getLocation();
			
			//prohibit spawn on wrong material
			switch (l.clone().subtract(new Vector(0, 1, 0)).getBlock().getType()) {
				case AIR:
				case BEDROCK:
				case STATIONARY_WATER:
				case WATER:
				case LAVA:
				case STATIONARY_LAVA:
				case ICE:
				case LEAVES:
					return;
				default:
					break;
			}
			
			//chest + contents
			block.setType(Material.CHEST);
			
			populateChest(block, rnd, chunk);
			buildChestHouse(world, rnd, chunk);
		}
	}
	
	private void buildChestHouse(World wld, Random rnd, Chunk chunk) {
		//TODO building
	}
	
	private void populateChest(Block block, Random rnd, Chunk chunk) {
		
		Chest chest = (Chest) block.getState();
		Inventory inv = chest.getInventory();
		
		ItemStack[] chestitems = new ItemStack[27]; //27 chestslots
		for (int i = 0; i < rnd.nextInt(6); i++) {
			
			chestitems[rnd.nextInt(27)] = chestDrops[rnd.nextInt(chestDrops.length)];
		}
		inv.setContents(chestitems);
	}
	
}
