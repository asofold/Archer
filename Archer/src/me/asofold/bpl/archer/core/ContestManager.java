package me.asofold.bpl.archer.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.asofold.bpl.archer.config.compatlayer.CompatConfig;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Central access point for all contests.<br>
 * Active contests are stored in PlayerData.
 * @author mc_dev
 *
 */
public class ContestManager {
	/** All contests by contest-name. */
	protected final Map<String, Contest> contests = new LinkedHashMap<String, Contest>();
	
	/** TEMP: World to contests. */
	protected final Map<String, Set<Contest>> worldMap = new HashMap<String, Set<Contest>>();
	
	public Contest getContestExact(String name){
		// TODO: Lower case ?
		return contests.get(name);
	}
	
	/**
	 * Get available contests for the current location the player is at (convenience method).
	 * @param player
	 * @return
	 */
	public Collection<Contest> getAvailableContests(Player player){
		return getAvailableContests(player, player.getLocation());
	}
	
	/**
	 * Get all available contests for the player at the given location.<br>
	 * TODO: Might do without the player ?
	 * 
	 * @param player
	 * @param loc
	 * @return
	 */
	public Collection<Contest> getAvailableContests(Player player, Location loc){
		final List<Contest> found = new LinkedList<Contest>();
		final Set<Contest> perWorld = worldMap.get(loc.getWorld().getName().toLowerCase());
		if (perWorld != null){
			found.addAll(perWorld);
		}
		return found;
	}
	
	public void fromConfig(CompatConfig cfg, String prefix){
		for (String key : cfg.getStringKeys(prefix)){
			Contest contest = new Contest(null, null);
			contest.fromConfig(cfg, prefix + key);
			if (contest.name != null){
				addContest(contest);
			}
			// TODO: else warn.
		}
	}

	public void addContest(Contest contest) {
		if (contest.world == null) contest.world = "*";
		if (contest.owner == null) contest.owner = "";
		contests.put(contest.name.toLowerCase(), contest);
		String wKey = contest.world.toLowerCase();
		Set<Contest> wContests = worldMap.get(wKey);
		if (wContests == null){
			wContests = new LinkedHashSet<Contest>();
			worldMap.put(wKey, wContests);
		}
		wContests.remove(contest); // To remove old one with the same name.
		wContests.add(contest);
	}
	
}
