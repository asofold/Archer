package me.asofold.bpl.archer.command;

import me.asofold.bpl.archer.Archer;
import me.asofold.bpl.archer.config.Permissions;
import me.asofold.bpl.archer.core.PlayerData;
import me.asofold.bpl.archer.utils.Utils;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NotifyCommand extends AbstractCommand<Archer> {

	public NotifyCommand(Archer access) {
		super(access, "notify", Permissions.COMMAND_NOTIFY);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args)
	{
		if (!Utils.checkPlayer(sender) ) return true;
		if (args.length != 1){
			return false;
		}
		
		Player player = (Player) sender;
		PlayerData data = access.getPlayerData(player, true);
		
		if (data.notifyTargets){
			data.notifyTargets = false;
			data.clearLaunchs();
			player.sendMessage(Archer.msgStart + "You " + ChatColor.RED + "unsubscribed" + ChatColor.GRAY + " from archer target events.");
		}
		else {
			data.notifyTargets = true;
			player.sendMessage(Archer.msgStart + "You " + ChatColor.GREEN + "subscribed" + ChatColor.GRAY + " to archer target events.");
		}
		if (data.mayForget()){
			access.removePlayerData(data);
		}
		return true;
	}
	
	

}
