package me.nithanim.UltraHardcoreMC.tasks.runnables;

import java.util.concurrent.TimeUnit;

import me.nithanim.UltraHardcoreMC.HardcoreHandler;
import me.nithanim.UltraHardcoreMC.UltraHardcoreMC;


public class CountdownRunnable implements Runnable {
	
	private UltraHardcoreMC plugin;
	private long gamestarttime;
	
	private long lastShout;
	
	public CountdownRunnable(long minutesToGo) {
		long currtime = System.currentTimeMillis() / 1000;
		
		this.plugin = UltraHardcoreMC.getPlugin();
		this.gamestarttime = currtime + minutesToGo * 60; //future (unix)timestamp when game starts
		
		lastShout = 0;
		run();
	}
	
	@Override
	public void run() {
		long currtime = System.currentTimeMillis() / 1000;
		int timediff = (int) (gamestarttime - currtime); //remeaning time in seconds
		Shoutinterval si = getShoutinterval(timediff);
		
		if (timediff <= 0) //countdown ended
		{
			HardcoreHandler handler = plugin.getHandler();
			
			handler.countdownTask.cancleTask(); //cancel countdown
			handler.countdownTask = null;
			
			handler.startGame(); //start game
			return;
		}
		else if (shouldShout(si, currtime)) {
			StringBuilder msg = new StringBuilder(30);
			TimeUnit tu = si.getTimeUnit();
			
			msg.append("Game starts in ");
			msg.append(tu.convert(timediff, TimeUnit.SECONDS));
			msg.append(" ");
			msg.append(tu.toString().toLowerCase());
			msg.append('!');
			
			plugin.sayServer(msg.toString());
			lastShout = currtime;
		}
		
	}
	
	/**
	 * Searches for an appropriate shout-interval
	 * 
	 * @param timediff
	 * @return appropriate shoutinterval
	 */
	private Shoutinterval getShoutinterval(int timediff) {
		Shoutinterval[] shoutvals = Shoutinterval.values();
		Shoutinterval lastshoutval = null;
		
		for (int i = shoutvals.length - 1; i >= 0; i--) {
			if (timediff <= shoutvals[i].getSeconds()) {
				lastshoutval = shoutvals[i];
				continue;
			}
			else {
				if (lastshoutval == null)
					throw new IllegalArgumentException(
							"No appropriate shoutinterval could be found!");
				return lastshoutval;
			}
		}
		return lastshoutval;
		
	}
	
	private boolean shouldShout(Shoutinterval si, long currTime) {
		return lastShout + si.getInterval() <= currTime;
	}
	
	
	
	private enum Shoutinterval {
		//shout every  //TimeUnit	   //use below until //delay (sec) between messages 
		SECONDS     (TimeUnit.SECONDS,           10,        1), //shout every second when there is only 10s left
		TEN_SECONDS (TimeUnit.SECONDS,           60,       10), //shout every 10s when there are 60s left
		MINUTES     (TimeUnit.MINUTES,      10 * 60,       60), //shout every minute when there are 10m left
		TEN_MINUTES (TimeUnit.MINUTES,      60 * 60,  10 * 60), //shout every 10m when there are 60m left
		HOURS       (TimeUnit.HOURS,   24 * 60 * 60,  60 * 60);
		
		
		private int seconds;
		private int shoutevery;
		private TimeUnit tu;
		
		Shoutinterval(TimeUnit tu, int seconds, int shoutevery) {
			this.seconds = seconds;
			this.shoutevery = shoutevery;
			this.tu = tu;
		}
		
		public int getSeconds() {
			return seconds;
		}
		
		public TimeUnit getTimeUnit() {
			return tu;
		}
		
		int getInterval()
		{
			return shoutevery;
		}
		
	}
}