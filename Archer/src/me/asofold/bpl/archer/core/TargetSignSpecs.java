package me.asofold.bpl.archer.core;

import me.asofold.bpl.archer.config.Settings;
import me.asofold.bpl.archer.utils.Utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.material.Attachable;

public class TargetSignSpecs {
	
	public String targetName = "";
	
	private static final BlockFace[][] searchFaces = new BlockFace[][] {
		{BlockFace.NORTH, BlockFace.WEST, BlockFace.EAST},
		{BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH},
		{BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST},
		{BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH}
	};

	/**
	 * Search straight, then counter clockwise.
	 * @param sign
	 * @param settings
	 * @return
	 */
	public static final TargetSignSpecs getSpecs(final Sign sign, final Settings settings) {
		final String[] lines = sign.getLines();
		for (int i = 0; i < 3; i ++) {
			final String line = Utils.getLine(lines, i, settings.trim, settings.stripColor, settings.ignoreCase);
			if (!line.equals(settings.lines[i])) {
				return null;
			}
		}
		final String name;
		final String line = Utils.getLine(lines, 3, settings.trim, settings.stripColor, settings.ignoreCase);
		if (line.equals(settings.lines[3])) {
			name = ""; // No name.
		}
		else if (line.equals(settings.targetNameDelegator)) {
			// Find sign with name on it or set to default.
			final Block block = sign.getBlock();
			final BlockFace attached = ((Attachable) sign.getData()).getAttachedFace();
			// TODO: get rid of block getting , set increments by block face !
			final Block attachedTo = block.getRelative(attached);
			switch(attached) {
    			case NORTH:
    				name = searchName(attachedTo, searchFaces[0], settings);
    				break;
    			case WEST:
    				name = searchName(attachedTo, searchFaces[1], settings);
    				break;
    			case SOUTH:
    				name = searchName(attachedTo, searchFaces[2], settings);
    				break;
    			case EAST:
    				name = searchName(attachedTo, searchFaces[3], settings);
    				break;
    			default:
    				name = "";
			}
		}
		else{
			// Check for direct name def:
			name = Utils.getWrappedContent(line, settings.targetNamePrefix, settings.targetNameSuffix);
			if (name == null) {
				return null; // invalid
			}
		}
		final TargetSignSpecs specs = new TargetSignSpecs();
		specs.targetName = name.trim();
		return specs;
	}

	/**
	 * Look for fourth line name definition.
	 * @param startBlock
	 * @param faces
	 * @param settings
	 * @return name or, "" if nothing found.
	 */
	private static final String searchName(final Block startBlock, final BlockFace[] faces, final Settings settings) {
		if (settings.targetNamePrefix.isEmpty() && settings.targetNameSuffix.isEmpty()) {
			return "";
		}
		for (final BlockFace face : faces) {
			final Block block = startBlock.getRelative(face);
			if (block.getType() != Material.WALL_SIGN) {
				continue;
			}
			final BlockState state = block.getState();
			if (!(state instanceof Sign)) {
				continue;
			}
			final Sign sign = (Sign) state;
			final String line = Utils.getLine(sign.getLines(), 3, settings.trim, settings.stripColor, settings.ignoreCase);
			final String name = Utils.getWrappedContent(line, settings.targetNamePrefix, settings.targetNameSuffix);
			if (name != null) {
				return name;
			}
		}
 		return "";
	}

}
