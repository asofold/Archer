package me.asofold.bukkit.archer.core;

import java.util.HashMap;
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
	
	public Player bPlayer = null;
	
	/**
	 * Entity id to launch location.
	 */
	public final Map<Integer, Location> launchs = new HashMap<Integer, Location>(10);
	
	public PlayerData(String playerName){
		this.playerName = playerName;
	}

	public PlayerData(Player player) {
		this(player.getName());
		bPlayer = player;
	}

	public void clear() {
		bPlayer = null;
		launchs.clear();
	}

	
	
}
