package me.asofold.bukkit.archer.config;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;

import me.asofold.bukkit.archer.config.compatlayer.CompatConfig;
import me.asofold.bukkit.archer.config.compatlayer.CompatConfigFactory;

public class Settings {
	
	public final DecimalFormat format = new DecimalFormat("##.##");
	
	/**
	 * Lines of target (trim applied).
	 */
	public String[] lines = new String[]{
			"ooooo",
			"ooxxxoo",
			"ooxxxoo",
			"ooooo"
	};
	
	public double signHitDist = 0.31;
	
//	public double arrowLength = 0.7;
	
	public double step = 0.3;

	public double offsetX =  0.0;
	public double offsetY = 0.08 ;
	public double offsetZ = 0.0;

	public double offDivisor = 100;

	public double notifyDistance = 120.0 ;

	public boolean notifyCrossWorld = false ;

	public boolean verbose = false ;

	public double shootDistMin = 0.0 ;
	
	public double shootDistMax = 0.0;

	public boolean trim = false;

	public boolean stripColor = false;

	public boolean ignoreCase = false;

	public boolean usePermissions = true;

	public long durExpireData = 20 * 60 * 1000;

	public String targetNamePrefix = ">>";
	
	public String targetNameSuffix = "<<";

	public String targetNameDelegator = "^^^^^";
	
	public static CompatConfig getDefaultSettings(){
		Settings ref = new Settings(); // ref.s.
		CompatConfig cfg = CompatConfigFactory.getConfig(null);
		LinkedList<String> lines = new LinkedList<String>();
		for (String line : ref.lines){
			lines.add(line);
		}
		cfg.set("target.lines", lines);
		cfg.set("target.trim", ref.trim);
		cfg.set("target.stripColor", ref.stripColor);
		cfg.set("target.ignore-case", ref.ignoreCase);
		cfg.set("target.name.prefix", ref.targetNamePrefix);
		cfg.set("target.name.suffix", ref.targetNameSuffix);
		cfg.set("target.name.delegator", ref.targetNameDelegator);
		cfg.set("notify.distance", ref.notifyDistance);
		cfg.set("notify.cross-world", ref.notifyCrossWorld);
		cfg.set("shooter.distance.min", ref.shootDistMin);
		cfg.set("shooter.distance.max", ref.shootDistMax);
		cfg.set("off-target.distance", ref.signHitDist);
//		cfg.set("arrow-length", ref.arrowLength);
		cfg.set("step", ref.step);
		cfg.set("offset.x", ref.offsetX);
		cfg.set("offset.y", ref.offsetY);
		cfg.set("offset.z", ref.offsetZ);
		cfg.set("off-target.divisor", ref.offDivisor);
		cfg.set("verbose", ref.verbose);
		cfg.set("permissions.use", ref.usePermissions);
		cfg.set("players.expire-offline", ref.durExpireData / 60 / 1000); // Set as minutes.
		return cfg;
	}
	
	public void applyConfig(CompatConfig cfg){
		Settings ref = new Settings(); // ref.s.
		signHitDist = cfg.getDouble("off-target.distance", ref.signHitDist);
//		arrowLength = cfg.getDouble("arrow-length", ref.arrowLength);
		step = cfg.getDouble("step", ref.step);
		offsetX = cfg.getDouble("offset.x", ref.offsetX);
		offsetY = cfg.getDouble("offset.y", ref.offsetY);
		offsetZ = cfg.getDouble("offset.z", ref.offsetZ);
		offDivisor = cfg.getDouble("off-target.divisor", ref.offDivisor);
		verbose = cfg.getBoolean("verbose", ref.verbose);
		notifyDistance = cfg.getDouble("notify.distance", ref.notifyDistance);
		notifyCrossWorld = cfg.getBoolean("notify.cross-world", ref.notifyCrossWorld);
		shootDistMin = cfg.getDouble("shooter.distance.min", ref.shootDistMin);
		shootDistMax = cfg.getDouble("shooter.distance.max", ref.shootDistMax);
		String[] lines = readLines(cfg, "target.lines", trim, stripColor, ignoreCase);
		if (lines == null) lines = ref.lines;
		for (int i = 0; i < 4; i++){
			this.lines[i] = lines[i];
		}
		trim = cfg.getBoolean("target.trim", ref.trim);
		stripColor = cfg.getBoolean("target.stripColor", ref.stripColor);
		ignoreCase = cfg.getBoolean("target.ignore-case", ref.ignoreCase);
		usePermissions = cfg.getBoolean("permissions.use", ref.usePermissions);
		durExpireData = cfg.getLong("players.expire-offline", ref.durExpireData) * 60 * 1000; // Set as minutes.
		targetNamePrefix = cfg.getString("target.name.prefix", ref.targetNamePrefix);
		targetNameSuffix = cfg.getString("target.name.suffix", ref.targetNameSuffix);
		targetNameDelegator = cfg.getString("target.name.delegator", ref.targetNameDelegator);
	}
	
	public Settings(){
		DecimalFormatSymbols sym = format.getDecimalFormatSymbols();
		sym.setDecimalSeparator('.');
		format.setDecimalFormatSymbols(sym);
	}
	
	public static  String[] readLines(CompatConfig cfg, String path, boolean trim, boolean stripColor, boolean ignoreCase) {
		String[] out = new String[4];
		List<String> lines = cfg.getStringList(path, null);
		if (lines == null) return null;
		if (lines.size() != 4) return null;
		for (int i = 0; i < 4; i++){
			String line = lines.get(i);
			if (trim) line = line.trim();
			if (stripColor) line = ChatColor.stripColor(line);
			if (ignoreCase) line = line.toLowerCase();
			if (line.length() > 15) return null;
			out[i] = line;
		}
		return out;
	}
}
