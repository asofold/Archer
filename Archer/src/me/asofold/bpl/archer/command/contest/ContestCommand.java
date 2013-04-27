package me.asofold.bpl.archer.command.contest;

import me.asofold.bpl.archer.Archer;
import me.asofold.bpl.archer.command.AbstractCommand;
import me.asofold.bpl.archer.config.Permissions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ContestCommand extends AbstractCommand<Archer> {

	public ContestCommand(Archer access) {
		super(access, "contest", Permissions.ACCESS_COMMAND_CONTEST);
		addSubCommands(
			new ContestInfoCommand(access),
			new ContestJoinCommand(access),
			new ContestLeaveCommand(access)
		);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args)
	{
		if (args.length == 1){
			// Delegate to info command.
			return subCommands.get("info").onCommand(sender, command, alias, args);
		}
		else{
			return super.onCommand(sender, command, alias, args);
		}
	}
	
	

}
