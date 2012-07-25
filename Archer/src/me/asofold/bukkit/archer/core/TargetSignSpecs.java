package me.asofold.bukkit.archer.core;

import me.asofold.bukkit.archer.config.Settings;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;

public class TargetSignSpecs {

	public static TargetSignSpecs getSpecs(Sign sign, Settings settings) {
		final String[] lines = sign.getLines();
		for (int i = 0; i < 4; i ++){
			String line = lines[i];
			if (settings.trim) line = line.trim();
			if (settings.stripColor) line = ChatColor.stripColor(line);
			if (settings.ignoreCase) line = line.toLowerCase();
			if (!line.equals(settings.lines[i])) return null;
		}
		TargetSignSpecs specs = new TargetSignSpecs();
		// TODO: name etc.
		return specs;
	}

}
