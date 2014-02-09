package me.asofold.bpl.archer.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import me.asofold.bpl.archer.config.Settings;
import me.asofold.bpl.archer.core.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
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
		Object shooter = null;
		try {
			shooter = projectile.getClass().getMethod("getShooter").invoke(projectile);
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
		} catch (NoSuchMethodException e) {
		}
		if (shooter instanceof Player) return (Player) shooter;
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

	/**
	 * Return content wrapped by prefix and suffix, if both are not set or line does not match, null is returned.
	 * @param line
	 * @param prefix Empty for not set.
	 * @param suffix Empty for not set.
	 * @return null if invalid definition.
	 */
	public static String getWrappedContent(String line, String prefix, String suffix) {
		boolean hasPrefix = !prefix.isEmpty();
		if (hasPrefix){
			if (!line.startsWith(prefix)) return null; // wrong prefix
			hasPrefix = true;
		}
		boolean hasSuffix = !suffix.isEmpty();
		if (hasSuffix){
			if (!line.endsWith(suffix)) return null; // wrong suffix
			hasSuffix = true;
		}
		if (!hasPrefix && !hasSuffix) return null;
		return line.substring(hasPrefix?prefix.length() : 0, hasSuffix?(line.length() - suffix.length()) : 0);
	}

	public static final String getLine(final String[] lines, final int index, final boolean trim, final boolean stripColor, final boolean ignoreCase){
		String line = lines[index];
		if (trim) line = line.trim();
		if (stripColor) line = ChatColor.stripColor(line);
		if (ignoreCase) line = line.toLowerCase();
		return line;
	}
	
	/**
     * 
     * @param parts
     * @param link can be null
     * @return
     */
    public static final String joinObjects(final Collection<?> parts, final String link){
        final StringBuilder builder = new StringBuilder();
        int i = 0;
        final int max = parts.size();
        for (final Object part : parts){
            if (part == null) builder.append("<null>");
            else builder.append(part.toString());
            i++;
            if ( i<max && link!=null ) builder.append(link);
        }
        return builder.toString();
    }

	public static void sendMessage(PlayerData data, String message) {
		if (data.player != null && data.player.isOnline()){
			data.player.sendMessage(message);
		}
	}


}
