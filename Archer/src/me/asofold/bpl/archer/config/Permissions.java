package me.asofold.bpl.archer.config;

public class Permissions {
	
	private static final String COMMAND = "command";
	
	private static final String ARCHER = "archer";
	
	private static final String ACCESS_COMMAND = ARCHER + ".access." + COMMAND;
	public static final String ACCESS_COMMAND_ARCHER = ACCESS_COMMAND + ".archer";
	public static final String ACCESS_COMMAND_CONTEST = ACCESS_COMMAND + ".contest";
	
	public static final String COMMAND_RELOAD = ARCHER + ".reload";
	public static final String COMMAND_NOTIFY = ARCHER + ".notify";
	
	private static final String COMMAND_ARCHER = ARCHER + "." + COMMAND;
	private static final String COMMAND_CONTEST = COMMAND_ARCHER + ".contest";
	public static final String COMMAND_CONTEST_JOIN = COMMAND_CONTEST + ".join";
	public static final String COMMAND_CONTEST_LEAVE = COMMAND_CONTEST + ".leave";

	
}
