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
	
	/** Active contests. */
	protected final Map<String, ContestData> activeContests = new LinkedHashMap<String, ContestData>(); 
	
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

	public void clear() {
		player = null;
		launchs.clear();
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
	 * Use to see if data can be removed.<br>
	 * This will respect if the player is in some contest and might be in when re-logging, later.
	 * @param tsNow
	 * @param durExpire
	 * @return
	 */
	public boolean mayForget(long tsNow, long durExpire){
		return tsNow - tsActivity > durExpire;
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
