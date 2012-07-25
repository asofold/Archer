package me.asofold.bukkit.archer.utils;

import me.asofold.bukkit.archer.config.Settings;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class TargetUtil {

	/**
	 * Sign hit location;
	 * @param loc
	 * @return
	 */
	public static final Location getHitLocation(final Location startLoc, final Vector velocity, final Settings settings) {
		Location loc = startLoc.clone();
	//		loc = loc.add(direction.normalize().multiply(arrowLength));
		final Block hitBlock = loc.getBlock();
		int type = hitBlock.getTypeId();
		final double l = velocity.length();
		double done = 0.0;
		final double step = settings.step;
		final boolean verbose = settings.verbose;
		if (type == 0){
			// TODO: also for other block types !
			// TODO: optimize: find block transitions directly (one by one).
			final Vector add = velocity.clone().multiply(step/l);
			while (type == 0){
				loc = loc.add(add);
				if (verbose) System.out.println("EXTEND: " + Utils.stringPos(loc, settings)); // TODO: REMOVE
				type = loc.getBlock().getTypeId();
				done += step;
				if (done >= l) break;
			}
			
		}
		
		if (verbose) System.out.println("Hit type ("+settings.format.format(l)+"): "+ type); // TODO: REMOVE
		
		if (type != Material.WALL_SIGN.getId()) return null;
		return loc;
	}

	public static final double getHitDist(final Location loc){
		return loc.distance(new Location(loc.getWorld(), 0.5 + (double) loc.getBlockX(), 0.5 + (double) loc.getBlockY(), 0.5 + (double) loc.getBlockZ()));
	}

	/**
	 * Always positive distance to 0.5 .
	 * @param coord
	 * @return
	 */
	public static final double getHitDist(final double coord){
		return Math.abs(0.5 - Math.abs(Math.floor(coord)));
	}

}
