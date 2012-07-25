package me.asofold.bukkit.archer.core;

import me.asofold.bukkit.archer.config.Settings;
import me.asofold.bukkit.archer.utils.Utils;

import org.bukkit.block.Sign;

public class TargetSignSpecs {
	
	public String targetName = "";

	public static final TargetSignSpecs getSpecs(final Sign sign, final Settings settings) {
		final String[] lines = sign.getLines();
		for (int i = 0; i < 3; i ++){
			final String line = Utils.getLine(lines, i, settings.trim, settings.stripColor, settings.ignoreCase);
			if (!line.equals(settings.lines[i])) return null;
		}
		final String name;
		final String line = Utils.getLine(lines, 3, settings.trim, settings.stripColor, settings.ignoreCase);
		if (line.equals(settings.lines[3])) name = ""; // No name.
		else if (line.equals(settings.targetNameDelegator)){
			// TODO: find sign with name on it or set to default.
			name = ""; // TODO: exchange for name.
		}
		else{
			// Check for direct name def:
			name = Utils.getWrappedContent(line, settings.targetNamePrefix, settings.targetNameSuffix);
			if (name == null) return null; // invalid
		}
		final TargetSignSpecs specs = new TargetSignSpecs();
		specs.targetName = name.trim();
		return specs;
	}

}
