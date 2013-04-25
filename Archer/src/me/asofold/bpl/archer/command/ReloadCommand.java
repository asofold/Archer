package me.asofold.bpl.archer.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.asofold.bpl.archer.Archer;
import me.asofold.bpl.archer.config.Permissions;

public class ReloadCommand extends AbstractCommand<Archer> {

	public ReloadCommand(Archer access) {
		super(access, "reload", Permissions.COMMAND_RELOAD);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args)
	{
		if (args.length != 1){
			return false;
		}
		access.reloadSettings();
		sender.sendMessage("[Archer] Settings reloaded.");
		return true;
	}
	
	

}
