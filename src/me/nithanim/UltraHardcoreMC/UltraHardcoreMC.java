package me.nithanim.UltraHardcoreMC;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import me.nithanim.UltraHardcoreMC.commands.DowngradeCommandExecutor;
import me.nithanim.UltraHardcoreMC.listeners.HardcoreListener;
import me.nithanim.UltraHardcoreMC.listeners.WorldListener;
import me.nithanim.UltraHardcoreMC.listeners.PlayerlistListener;
import me.nithanim.UltraHardcoreMC.listeners.SpecificWorldsListener;
import me.nithanim.UltraHardcoreMC.spawn.SpawnFactory;
import me.nithanim.UltraHardcoreMC.spawn.SpawnHandler;
import me.nithanim.UltraHardcoreMC.tasks.RepeatingSyncTask;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;




public class UltraHardcoreMC extends JavaPlugin {
	
	private PluginDescriptionFile pdf;
	private HardcoreHandler handler;
	private SpawnHandler spawnhandler;
	
	public RepeatingSyncTask mapGeneratorTask;
	
	private static UltraHardcoreMC plugin;
	
	
	public static UltraHardcoreMC getPlugin()
	{
		return plugin;
	}
	
	@Override
	public void onEnable() {
		plugin = this;
		pdf = getDescription();
		
		setConfigDefaults();
		
		
		//init handlers
		handler = new HardcoreHandler(this);
		spawnhandler = new SpawnHandler(this);
		
		//try to restore last state
		if(!handler.restoreMemoryAfterShutdown()) {	return; /*handled in there */ }
		
		//new PlayerlistHandler(this);
		new PlayerlistListener(this); // TODO Activate for event listening
		
		
		//init listeners
		SpecificWorldsListener.setWorldsList(getConfig().getStringList("worlds"));
		new HardcoreListener(this);
		new WorldListener(this);
		//+ the statistic one, created in StatisticHandler
		
		
		
		//init command handler
		getCommand("ultrahardcore").setExecutor(new DowngradeCommandExecutor(this));
		
		//recipes
		Server server = getServer();
		Iterator<Recipe> ri = server.recipeIterator();
		
		while(ri.hasNext()) //sort out unwanted recipes
		{
			Recipe nextRecipe = ri.next();

			switch(nextRecipe.getResult().getType())
			{
				case GOLDEN_APPLE:
					if(!nextRecipe.getResult().getEnchantments().isEmpty()) //don't remove special apple
					{
						break;
					}
				case SPECKLED_MELON:
					ri.remove();
				default:
					break;
			}
		}
		
        //add replacements for deleted recipes
        final ShapedRecipe goldenapple = new ShapedRecipe(new ItemStack(Material.GOLDEN_APPLE, 1));
        goldenapple.shape("ggg","gag","ggg");
        goldenapple.setIngredient('g', Material.GOLD_INGOT);
        goldenapple.setIngredient('a', Material.APPLE);
        
        final ShapelessRecipe glisteringmelon = new ShapelessRecipe(new ItemStack(Material.SPECKLED_MELON));
        glisteringmelon.addIngredient(Material.MELON);
        glisteringmelon.addIngredient(Material.GOLD_BLOCK);
		
		server.addRecipe(goldenapple);
		server.addRecipe(glisteringmelon);
		
		getLogger().info(pdf.getName() + " " + pdf.getVersion() + " has been enabled.");
	}


	@Override
	public void onDisable() {
		saveConfig();
		SpawnFactory.destroyFactory();
		
		handler.pauseGame();
		handler.saveMemory();
		
		getServer().getScheduler().cancelTasks(this);
		
		SpecificWorldsListener.deleteWorldsList();
		
		plugin = null;
		getLogger().info(getName() + " has been disabled.");
	}
	
	@Override
	public void onLoad() {
		
	}
	
	private void setConfigDefaults()
	{
		FileConfiguration conf = getConfig();
		conf.options().copyDefaults(true);
		
		//conf.addDefault("master", "Tempelchat");
		
		  confAddDefaultMinMax("saveinterval", 1, 15, 5);
		conf.addDefault("marks.enabled", false);
		  confAddDefaultMinMax("marks.delay", 1, 30, 30);
		  
		  //confAddDefaultMinMax("difficulty", 0, 3, 3); //set in server.properties
		  
		List<String> defworlds = new ArrayList<String>(2);
		defworlds.add("world");
		defworlds.add("world_nether");
		conf.addDefault("worlds", defworlds);
		
		//conf.addDefault("border.enable", false);
		conf.addDefault("border.midpoint.enabled", false);
		conf.addDefault("border.midpoint.location", new Vector());
		  confAddDefaultMinMax("border.radius", 1, -1, 50);
		conf.addDefault("border.holdPlayerInside", false);
		conf.addDefault("border.wall.enabled", false);
		  confAddDefaultMinMax("border.wall.material", 1, -1, 20);
		  
		conf.addDefault("spawn.enabled", true);
		conf.addDefault("spawn.minSpawnDistance", 20);
		conf.addDefault("spawn.autoTele", true);
		conf.addDefault("spawn.clearInventory", false);
		
		conf.addDefault("spawn.teams.enabled", true);
		
		conf.addDefault("respawn.gocreative", false);
		conf.addDefault("respawn.banOnQuit", false);
		conf.addDefault("respawn.tpworld", false);
		
		//section bonus
		conf.addDefault("bonus.boltOnDeath.enabled", false);
		conf.addDefault("bonus.surfacechest.enabled", false);
		conf.addDefault("bonus.surfacechest.contains", "38,79");
		
		//save
		saveConfig();
	}
	
	/**
	 * Proves if a int value in the config matches the specified range. If not or not set, the def value will be chosen and written to config.
	 * Additionally, a warning will be print out to console.
	 * @param confstr String for the configuration like you would do it with bukkit API .
	 * @param min Minimum value
	 * @param max Maximum value
	 * @param def Default value if 
	 */ //Sets a int value in the config like the bukkit API with the exception that only a specified range is allowed.
	private void confAddDefaultMinMax(String confstr, int min, int max, int def)
	{
		getConfig().addDefault(confstr, def); //simple default
		
		//is it in range?		
		int num = getConfig().getInt(confstr);
		if(num < min || (max!=-1 && num > max))
		{
			StringBuffer sb = new StringBuffer(70);
			
			sb.append(confstr).append(" must be between ").append(min).append(" and ").append(max);
			sb.append("! Using ").append(def).append(" for now.");
			
			getLogger().log(Level.WARNING, sb.toString());
			getConfig().set(confstr, def);
		}
	}
	
	
	/**
	 * Dispatches a console "say" command.
	 * @param msg Message to print out.
	 */
	public void sayServer(String msg)
	{
		Server server = getServer();
		server.dispatchCommand(server.getConsoleSender(), "say " + msg);
	}
	
	public HardcoreHandler getHandler()
	{
		return handler;
	}
	public SpawnHandler getSpawnHandler()
	{
		return spawnhandler;
	}
}
