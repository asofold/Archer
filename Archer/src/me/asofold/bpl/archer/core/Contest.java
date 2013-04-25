package me.asofold.bpl.archer.core;

import java.util.HashSet;
import java.util.Set;

import me.asofold.bpl.archer.config.compatlayer.CompatConfig;
import me.asofold.bpl.archer.core.properties.Property;
import me.asofold.bpl.archer.core.properties.PropertyHolder;

public class Contest extends PropertyHolder{
	
	public String name;
	public String owner;
	
	public String world = null;
	
	/** Last time started (2+ players in). */
	public long lastTimeStarted = 0;
	
	public Property maxPlayers = setProperty("max-players", 0, Integer.MAX_VALUE, 0);
	public Property minPlayers = setProperty("min-players", 0, Integer.MAX_VALUE, 0);
	/** Time that has to pass until the contest is really active. */
	public Property startDelay = setProperty("start-delay", 0, Long.MAX_VALUE, 0);
	
	public Property allowLateJoin = setProperty("late-join", 0, 1, 1);
	
	public Property maxTime = setProperty("max-time", 0, Long.MAX_VALUE, 0);
	public Property minDistance = setProperty("min-distance", 0, Integer.MAX_VALUE, 0);
	/** Only count shots that hurt (needs monitor)*/
	// TODO: Check this on what priority level (should be like normal, depending on worldguard).
	// TODO: Define a delay for hits to count.
	public Property ignoreCancelled = setProperty("ignore-cancelled", 0, 1, 0);
	/** Maximum number of shots allowed. */
	public Property maxShots = setProperty("max-shots", 0, Integer.MAX_VALUE, 0);
	public Property bonusShotsHit = setProperty("bonus-shots-hit", 0, Integer.MAX_VALUE, 0);
	public Property bonusShotsKill = setProperty("bonus-shots-kill", 0, Integer.MAX_VALUE, 0);
	
	public Property winHits = setProperty("win-hits", 0, Integer.MAX_VALUE, 0);
	public Property lossHits = setProperty("loss-hits", 0, Integer.MAX_VALUE, 0);
	public Property winHitBalance = setProperty("win-hit-balance", 0, Integer.MAX_VALUE, 0);
	
	public Property winScore = setProperty("win-score", 0, Double.MAX_VALUE, 0);
	public Property lossHealth = setProperty("loss-health", 0, Integer.MAX_VALUE, 0);
	public Property winScoreBalance = setProperty("win-score-balance", Integer.MIN_VALUE, Integer.MAX_VALUE, 0);
	
	/** Number of hits that made others leave the contest. */
	public Property winKills = setProperty("win-kills", 0, Integer.MAX_VALUE, 0);
	
	
	public Contest(String name, String owner){
		this.name = name;
		this.owner = owner;
		setAliases(); // Done here to be sure never to mix up order.
	}
	
	// get info message
	public void fromConfig(CompatConfig cfg, String prefix){
		name = cfg.getString(prefix + "name");
		owner = cfg.getString(prefix + "owner");
		Set<String> done = new HashSet<String>(properties.size());
		for (Property property : properties.values()){
			if (done.contains(property.name)) continue;
			String valDef = cfg.getString(prefix + property.name);
			if (valDef != null) property.fromString(valDef);
			done.add(property.name);
		}
	}
	
	@Override
	public int hashCode(){
		return name.toLowerCase().hashCode();
	}
	
	@Override
	public boolean equals(Object other){
		if (other instanceof Contest){
			return name.equalsIgnoreCase(((Contest) other).name);
		}
		else return false; // Might allow strings ?
	}
}
