package me.nithanim.UltraHardcoreMC;

/**
 * Represents the state of the game
 * @author Tempelchat
 *
 */
public enum Gamestate {
	NONE,
	COUNTDOWN,
	RUNNING,
	PAUSED;
	
	public static int toInt(Gamestate state)
	{
		return state.ordinal();
	}
	
	public static Gamestate toEnum(int state)
	{
		return Gamestate.values()[state];
	}
}
