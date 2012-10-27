package me.nithanim.UltraHardcoreMC.commands;


@java.lang.annotation.Target(value={java.lang.annotation.ElementType.METHOD})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Command 
{
	boolean needsPlayer() default false;
	
	String help() default "No usage found!";
	String[] args() default "No arguments";
	
	int minArgs() default 0;
	int maxArgs() default 0;
}