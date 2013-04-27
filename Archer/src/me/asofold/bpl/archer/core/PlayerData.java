package me.asofold.bpl.archer.core;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Data for players...
 * @author mc_dev
 *
 */
public class PlayerData {
	public final String playerName;
	
	public Player player = null;
	
	public long tsActivity = System.currentTimeMillis();
	
	public boolean notifyTargets = false;
	
	/** Active contests. */
	public final Map<String, ContestData> activeContests = new LinkedHashMap<String, ContestData>();
	
	/**
	 * Entity id to launch location.
	 */
	private final Map<Integer, Location> launchs = new HashMap<Integer, Location>(10);
	
	public PlayerData(String playerName){
		this.playerName = playerName;
	}

	public PlayerData(Player player) {
		this(player.getName());
		this.player = player;
	}
	
	public void clearLaunchs() {
		launchs.clear();
	}

	/**
	 * Just clear data, no side effects.
	 */
	public void clear() {
		player = null;
		clearLaunchs();
		activeContests.clear();
	}

	public void addLaunch(Integer id, Location loc){
		launchs.put(id, loc);
		tsActivity = System.currentTimeMillis();
	}
	
	public Location removeLaunch(Integer id){
		tsActivity = System.currentTimeMillis();
		return launchs.remove(id);
	}
	
	/**
	 * Check if data can be removed independent of timing.
	 * @return
	 */
	public boolean mayForget() {
		return !notifyTargets && activeContests.isEmpty();
	}
	
	/**
	 * Use to see if data can be removed. Might perform minimum cleanup. <br>
	 * This will respect if the player is in some contest and might be in when re-logging, later.
	 * @param tsNow
	 * @param durExpire
	 * @return
	 */
	public boolean mayForget(long tsNow, long durExpire){
		final long diff = tsNow - tsActivity;
		if (mayForget()){
			clear();
			return true;
		}
		if (diff > 30000){
			// Cleanup independent of other aspects.
			launchs.clear();
		}
		if (diff < 0){
			// Safety check.
			tsActivity = tsNow;
			return false;
		}
		else if (player == null || !player.isOnline()){
			// Might expire.
			return diff > durExpire;
		}
		else{
			// Online = active (at present).
			return false;
		}
	}

	/**
	 * 
	 * @param player
	 * @return The given player instance for chaining.
	 */
	public Player setPlayer(Player player) {
		this.player = player;
		tsActivity = System.currentTimeMillis();
		return player;
	}
	
}
