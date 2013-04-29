package me.asofold.bpl.archer.core;

import org.bukkit.Location;
import org.bukkit.util.Vector;

/**
 * Properties of a launched arrow.
 * @author mc_dev
 *
 */
public class LaunchSpec {
	
	public final String world;
	public final double x, y, z;
	public final long time;
	public final float force;
	
	/** For periodic cleanup. */
	public boolean consumed = false;
	
	public LaunchSpec(Location footLoc, double eyeHeight, long time, float force){
		this(footLoc.getWorld().getName(), footLoc.getX(), footLoc.getY() + eyeHeight, footLoc.getZ(), time, force);
	}
	
	public LaunchSpec(String world, double x, double y, double z, long time, float force){
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.time = time;
		this.force = force;
	}
	
	/**
	 * Coordinate distance.
	 * @param loc
	 * @return
	 */
	public double distance(final Location loc){
		// TODO: World ?
		return distance(loc.getX(), loc.getY(), loc.getZ());
	}
	
	/**
	 * Coordinate distance.
	 * @param loc
	 * @return
	 */
	public double distance(final Vector loc){
		// TODO: World ?
		return distance(loc.getX(), loc.getY(), loc.getZ());
	}
	
	/**
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public double distance(final double x, final double y, final double z){
		// TODO: World ?
		final double dX = x - this.x;
		final double dY = y - this.y;
		final double dZ = z - this.z;
		return Math.sqrt(dX * dX + dY * dY + dZ * dZ);
	}
	
}
