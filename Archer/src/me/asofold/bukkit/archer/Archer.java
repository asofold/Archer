package me.asofold.bukkit.archer;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import me.asofold.bukkit.archer.config.Settings;
import me.asofold.bukkit.archer.config.compatlayer.CompatConfig;
import me.asofold.bukkit.archer.config.compatlayer.CompatConfigFactory;
import me.asofold.bukkit.archer.config.compatlayer.ConfigUtil;
import me.asofold.bukkit.archer.core.PlayerData;
import me.asofold.bukkit.archer.core.TargetSignSpecs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
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
	
	private final DecimalFormat format = new DecimalFormat("##.##");
	
	private final Map<String, PlayerData> players = new HashMap<String, PlayerData>(20);

	private static final String msgStart = ChatColor.DARK_GRAY + "[Archer] " + ChatColor.GRAY;
	
	private final Settings settings = new Settings();

	public void reloadSettings() {
		File file = new File(getDataFolder(), "config.yml");
		CompatConfig cfg = CompatConfigFactory.getConfig(file);
		boolean exists = file.exists();
		if (exists) cfg.load();
		if (ConfigUtil.forceDefaults(Settings.getDefaultSettings(), cfg) || !exists) cfg.save();
		settings.applyConfig(cfg);
	}

	public Archer(){
		DecimalFormatSymbols sym = format.getDecimalFormatSymbols();
		sym.setDecimalSeparator('.');
		format.setDecimalFormatSymbols(sym);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
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
			if (settings.usePermissions && !checkPerm(sender, "archer.notify")) return true;
			if (!checkPlayer(sender) ) return true;
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
			if (!checkPerm(sender, "archer.reload")) return true;
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

	private boolean checkPlayer(CommandSender sender) {
		if (sender instanceof Player) return true;
		else{
			sender.sendMessage("[Archer] Only available for players !");
			return false;
		}
	}

	private boolean checkPerm(CommandSender sender, String perm) {
		if (!hasPermission(sender, perm)){
			sender.sendMessage(ChatColor.DARK_RED + "You don't have permission.");
			return false;
		}
		else return true;
	}

	private boolean hasPermission(CommandSender sender, String perm) {
		return sender.isOp() || sender.hasPermission(perm);
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
		
		final Location hitLoc = getHitLocation(projLoc, velocity);
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
			distOff = getLength(my - cY, mz - cZ );
		}
		// Not for dy !
		else if (dz != 0.0){
			final double t = (mz - hZ)/vZ;

			cX = hX + t * vX;
			cY = hY + t * vY;
			cZ = mz;
			distOff = getLength(mx - cX, my - cY );
		}
		else throw new RuntimeException("HUH?");
		
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
		final String specPart = ChatColor.YELLOW.toString() + off + ChatColor.GRAY + " off target at " + ChatColor.WHITE + format.format(shootDist) + ChatColor.GRAY + " blocks distance.";
		final String msg = ChatColor.WHITE + data.playerName + ChatColor.GRAY + " hits " + specPart;
		data.player.sendMessage(msgStart + ChatColor.GRAY + "hits " + specPart);
		sendAll(msg, targetLocation, data);
	}
	
	private String stringPos(double x, double y, double z) {
		return "" + format.format(x) + ", " + format.format(y) + ", " + format.format(z);
	}

	private final String stringPos(final Location loc){
		return "" + format.format(loc.getX()) + ", " + format.format(loc.getY()) + ", " + format.format(loc.getZ());
	}
	
	/**
	 * Sign hit location;
	 * @param loc
	 * @return
	 */
	public final Location getHitLocation(Location loc, final Vector velocity) {
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
				if (verbose) System.out.println("EXTEND: " + stringPos(loc)); // TODO: REMOVE
				type = loc.getBlock().getTypeId();
				done += step;
				if (done >= l) break;
			}
			
		}
		
		if (verbose) System.out.println("Hit type ("+format.format(l)+"): "+ type); // TODO: REMOVE
		
		if (type != Material.WALL_SIGN.getId()) return null;
		return loc;
	}

	private double getLength(double x1, double x2) {
		return Math.sqrt(x1*x1 + x2*x2);
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
		final Player player = getPlayer(projectile);
		if (player == null) return null;
		final PlayerData data = players.get(player.getName().toLowerCase());
		if ( data == null) return null;
		data.setPlayer(player);
		return data;
	}
	
	public static final Player getPlayer(final Projectile projectile){
		if (!(projectile instanceof Arrow)) return null;
		final Entity entity = projectile.getShooter();
		if (entity == null) return null;
		else if (entity instanceof Player) return (Player) entity;
		else return null;
	}

}
