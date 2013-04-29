package me.asofold.bpl.archer.core;


public class ContestData {
	
	public final Contest contest;
	
	public double score = 0.0;
	public double scoreSuffered = 0.0;
	
	public int kills = 0;
	
	public int hitsDealt = 0;
	public int hitsTaken = 0;
	
	public int shotsLeft = 0;
	public int shotsFired = 0;
	
	public ContestData(Contest contest){
		this.contest = contest;
	}
	
	public boolean interesting(){
		return shotsFired != 0 || hitsTaken != 0;
	}
}
