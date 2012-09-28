package me.nithanim.UltraHardcoreMC.tasks.runnables;

import me.nithanim.UltraHardcoreMC.HardcoreHandler;
import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;


public class AutosaveRunnable implements Runnable {
	

	@Override
	public void run() {
		HardcoreHandler uhhh = UltraHardcoreMC.getPlugin().getHandler();
		
		uhhh.getMemory().set("lastautosave.time", (int)(System.currentTimeMillis()/1000));
		uhhh.getMemory().set("lastautosave.gamestate", uhhh.getMemory().getInt("game.state"));
		
		uhhh.saveMemory();
		//uhhh.getPlugin().getLogger().info("Autosave finished!");
	}
	
}
