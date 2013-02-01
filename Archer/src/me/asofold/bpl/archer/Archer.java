package me.asofold.bpl.archer;

import java.io.File;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.asofold.bpl.archer.config.Settings;
import me.asofold.bpl.archer.config.compatlayer.CompatConfig;
import me.asofold.bpl.archer.config.compatlayer.CompatConfigFactory;
import me.asofold.bpl.archer.config.compatlayer.ConfigUtil;
import me.asofold.bpl.archer.core.PlayerData;
import me.asofold.bpl.archer.core.TargetSignSpecs;
import me.asofold.bpl.archer.utils.TargetUtil;
import me.asofold.bpl.archer.utils.Utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.material.Attachable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class Archer extends JavaPlugin implements Listener{

	
	private final Map<String, PlayerData> players = new HashMap<String, PlayerData>(20);

	public static final String msgStart = ChatColor.DARK_GRAY + "[Archer] " + ChatColor.GRAY;
	
	private final Settings settings = new Settings();

	public void reloadSettings() {
		File file = new File(getDataFolder(), "config.yml");
		CompatConfig cfg = CompatConfigFactory.getConfig(file);
		boolean exists = file.exists();
		if (exists) cfg.load();
		if (ConfigUtil.forceDefaults(Settings.getDefaultSettings(), cfg) || !exists) cfg.save();
		settings.applyConfig(cfg);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command != null) label = command.getLabel();
		label = label.toLowerCase();
		
		if (label.equals("archer")){
			return archerCommand(sender, args);
		}
		return false;
	}

	private boolean archerCommand(CommandSender sender, String[] args) {
		int len = args.length;
		String cmd = null;
		if (len > 0){
			cmd = args[0].trim().toLowerCase();
		}
		if (len == 1 && cmd.equals("notify")){
			// toggle notify
			if (settings.usePermissions && !Utils.checkPerm(sender, "archer.notify")) return true;
			if (!Utils.checkPlayer(sender) ) return true;
			Player player = (Player) sender;
			String playerName = player.getName();
			String lcName = playerName.toLowerCase();
			if (removeData(lcName)){
				player.sendMessage(msgStart + "You " + ChatColor.RED + "unsubscribed" + ChatColor.GRAY + " from archer events.");
				return true;
			}
			players.put(lcName, new PlayerData(player));
			player.sendMessage(msgStart + "You " + ChatColor.GREEN + "subscribed" + ChatColor.GRAY + " to archer events.");
			return true;
		}
		else if (len == 1 && cmd.equals("reload")){
			if (!Utils.checkPerm(sender, "archer.reload")) return true;
			reloadSettings();
			sender.sendMessage("[Archer] Settings reloaded.");
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param lcName
	 * @return If data was present.
	 */
	private boolean removeData(String lcName) {
		PlayerData data = players.remove(lcName);
		if (data == null) return false;
		data.clear();
		return true;
	}

	@Override
	public void onEnable() {
		reloadSettings();
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				checkExpiredData();
			}
		}, 1337, 1337);
		super.onEnable();
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
	final void onHit(final ProjectileHitEvent event){
		if (players.isEmpty()) return;
		final Projectile projectile = event.getEntity();
		final PlayerData data = getPlayerData(projectile);
		if (data == null) return;
		final int entityId = projectile.getEntityId();
		final Location launchLoc = data.removeLaunch(entityId);
		if (launchLoc == null) return;
		
		// TODO: later: add miss / hit events
		final Vector velocity = projectile.getVelocity();
		final Location projLoc = projectile.getLocation();
		final boolean verbose = settings.verbose;
		if (verbose) System.out.println("projectile at: " + stringPos(projLoc)); // TODO: REMOVE
		
		final Location hitLoc = TargetUtil.getHitLocation(projLoc, velocity, settings);
		if (hitLoc == null) return;
		
		if (verbose) System.out.println("hit loc at: " + stringPos(hitLoc)); // TODO: REMOVE
		
		final Block hitBlock = hitLoc.getBlock();
		
		final BlockState state = hitBlock.getState();
		if (!(state instanceof Sign)) return;
		final Sign sign = (Sign) state;
		
		final TargetSignSpecs specs = TargetSignSpecs.getSpecs(sign, settings);
		if (specs == null) return;
		// Target sign hit !
		
		// Get middle of sign (!)
		final BlockFace attached = ((Attachable) sign.getData()).getAttachedFace();
		
		// TODO: get rid of block getting , set increments by block face !
		final Block attachedTo = hitBlock.getRelative(attached); 
		
		// Hit block (sign) coordinates.
		final double x = hitBlock.getX();
		final double y = hitBlock.getY();
		final double z = hitBlock.getZ();
		
		// Attached to block coordinates.
		final double dx = attachedTo.getX() - x;
		final double dy = attachedTo.getY() - y;
		final double dz = attachedTo.getZ() - z;
		
		// Middle of of sign.
		final double mx = 0.5 + x + .5 * dx + settings.offsetX;
		final double my = 0.5 + y + .5 * dy + settings.offsetY;
		final double mz = 0.5 + z + .5 * dz + settings.offsetZ;
		
		final double distOff;
		
		// Hit location on sign block (not exact !).
		final double hX = hitLoc.getX();
		final double hY = hitLoc.getY();
		final double hZ = hitLoc.getZ();
		
		// Velocity
		final double vX = velocity.getX();
		final double vY = velocity.getY();
		final double vZ = velocity.getZ();
		
		// add time correction ? [Rather not, the arrow has hit, so calculate where it would actually hit.]
		
		// Corrected coordinates + distance off target.
		final double cX;
		final double cY;
		final double cZ;
		if (dx != 0.0){
			final double t = (mx - hX)/vX;
			cX = mx;
			cY = hY + t * vY;
			cZ = hZ + t * vZ;
			distOff = Utils.getLength(my - cY, mz - cZ );
		}
		// Not for dy !
		else if (dz != 0.0){
			final double t = (mz - hZ)/vZ;

			cX = hX + t * vX;
			cY = hY + t * vY;
			cZ = mz;
			distOff = Utils.getLength(mx - cX, my - cY );
		}
		else throw new RuntimeException("HUH?");
		final DecimalFormat format = settings.format;
		if (verbose) System.out.println("dx,dy,dz: " + stringPos(dx, dy, dz)); // TODO: REMOVE
		if (verbose) System.out.println("middle at: " + stringPos(mx, my, mz)); // TODO: REMOVE
		if (verbose) System.out.println("corrected hit pos: " +stringPos(cX, cY, cZ) + " -> off by " + format.format(distOff)); // TODO: REMOVE
		
		if (distOff > settings.signHitDist) return;
		// Hit !
		final Location targetLocation = new Location(hitLoc.getWorld(), mx,my,mz);
		final double shootDist = launchLoc.toVector().distance(new Vector(mx,my,mz));
		if (settings.shootDistMin > 0.0 && shootDist < settings.shootDistMin) return;
		if (settings.shootDistMax > 0.0 && shootDist > settings.shootDistMax) return;
		final int off = (int) Math.round((1000.0 - 1000.0 * (settings.signHitDist - distOff) / settings.signHitDist) / settings.offDivisor);
		final String targetName;
		if (specs.targetName.isEmpty()) targetName = "";
		else targetName = " (" + ChatColor.WHITE + specs.targetName + ChatColor.GRAY + ")";
		// (Might use StringBuilder here.)
		final String specPart = ChatColor.YELLOW.toString() + off + ChatColor.GRAY + " off target" + targetName +" at " + ChatColor.WHITE + format.format(shootDist) + ChatColor.GRAY + " distance.";
		final String msg = ChatColor.WHITE + data.playerName + ChatColor.GRAY + " hits " + specPart;
		data.player.sendMessage(ChatColor.WHITE + "---> " +  ChatColor.GRAY + "hits " + specPart);
		sendAll(msg, targetLocation, data);
	}
	
	private final String stringPos(final double x, final double y, final double z) {
		return Utils.stringPos(x, y, z, settings);
	}

	private final String stringPos(final Location loc){
		return Utils.stringPos(loc, settings);
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = true)
	final void onLaunch(final ProjectileLaunchEvent event){
		if (players.isEmpty()) return;
		final Projectile projectile = event.getEntity();
		final PlayerData data = getPlayerData(projectile);
		if (data == null) return;
		// Register projectile for aiming.
		data.addLaunch(projectile.getEntityId(), data.player.getLocation().add(new Vector(0.0, data.player.getEyeHeight(), 0.0))); // projectile.getLocation());
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled = false)
	final void onDamage(final EntityDamageByEntityEvent event){
		// also check cancelled events.
		if (players.isEmpty()) return;
		final Entity entity = event.getDamager();
		if (!(entity instanceof Projectile)) return;
		final Projectile projectile = (Projectile) entity;
		final PlayerData data = getPlayerData(projectile);
		if (data == null) return;
		final int id = projectile.getEntityId();
		final Location launchLoc = data.removeLaunch(id);
		if (launchLoc == null) return;
		// TODO: later: check if contest + add miss / hit events
	}
	
	public void sendAll(String msg, boolean label, Location ref, PlayerData exclude){
		if (!label) sendAll(msg, ref, exclude);
		else sendAll(msgStart + msg, ref, exclude);
	}
	
	public void sendAll(String msg, Location ref, PlayerData exclude){
		boolean distance = settings.notifyDistance > 0.0;
		boolean restrict = ref != null && (!settings.notifyCrossWorld || distance);
		String worldName = null;
		if (restrict) worldName = ref.getWorld().getName();
		List<String> rem = new LinkedList<String>();
		final long durExpireData = settings.durExpireData;
		final double notifyDistance = settings.notifyDistance;
		final long tsNow = System.currentTimeMillis();
		for (PlayerData data : players.values()){
			if (data == exclude) continue;
			if (data.player == null || !data.player.isOnline()){
				if (durExpireData > 0 && data.mayForget(tsNow, durExpireData)) rem.add(data.playerName.toLowerCase());
				continue;
			}
			if (restrict){
				if (!worldName.equals(data.player.getWorld().getName())) continue;
				else if (distance && ref.distance(data.player.getLocation()) > notifyDistance) continue; 
			}
			data.player.sendMessage(msg);
		}
		for (String name : rem){
			players.remove(name);
		}
	}
	
	public void checkExpiredData(){
		if (settings.durExpireData <= 0 || players.isEmpty()) return;
		List<String> rem = new LinkedList<String>();
		final long tsNow = System.currentTimeMillis();
		for (PlayerData data : players.values()){
			if (data.player == null || !data.player.isOnline()){
				if (data.mayForget(tsNow, settings.durExpireData)) rem.add(data.playerName.toLowerCase());
				continue;
			}		
		}
		for (String name : rem){
			players.remove(name);
		}
	}
	
	/**
	 * Get PlayerData and set bPlayer if present and projectile is Arrow (!).
	 * @param projectile
	 * @return
	 */
	public final PlayerData getPlayerData(final Projectile projectile){
		final Player player = Utils.getPlayer(projectile);
		if (player == null) return null;
		final PlayerData data = players.get(player.getName().toLowerCase());
		if ( data == null) return null;
		data.setPlayer(player);
		return data;
	}

}
