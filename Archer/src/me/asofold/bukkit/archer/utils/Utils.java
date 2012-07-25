package me.asofold.bukkit.archer.utils;

import me.asofold.bukkit.archer.config.Settings;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

public class Utils {
	
	public static boolean hasPermission(CommandSender sender, String perm) {
		return sender.isOp() || sender.hasPermission(perm);
	}

	public static boolean checkPerm(CommandSender sender, String perm) {
		if (!hasPermission(sender, perm)){
			sender.sendMessage(ChatColor.DARK_RED + "You don't have permission.");
			return false;
		}
		else return true;
	}
	
	public static boolean checkPlayer(CommandSender sender) {
		if (sender instanceof Player) return true;
		else{
			sender.sendMessage("[Archer] Only available for players !");
			return false;
		}
	}
	
	public static final Player getPlayer(final Projectile projectile){
		if (!(projectile instanceof Arrow)) return null;
		final Entity entity = projectile.getShooter();
		if (entity == null) return null;
		else if (entity instanceof Player) return (Player) entity;
		else return null;
	}
	
	public static String stringPos(double x, double y, double z, Settings settings) {
		return "" + settings.format.format(x) + ", " + settings.format.format(y) + ", " + settings.format.format(z);
	}

	public static final String stringPos(final Location loc, Settings settings){
		return "" + settings.format.format(loc.getX()) + ", " + settings.format.format(loc.getY()) + ", " + settings.format.format(loc.getZ());
	}
	
	public static final double getLength(final double x1, final double x2) {
		return Math.sqrt(x1*x1 + x2*x2);
	}


}
