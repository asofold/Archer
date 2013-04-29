package me.asofold.bpl.archer.command.contest;

import java.util.List;

import me.asofold.bpl.archer.Archer;
import me.asofold.bpl.archer.command.AbstractCommand;
import me.asofold.bpl.archer.command.TabUtil;
import me.asofold.bpl.archer.config.Permissions;
import me.asofold.bpl.archer.core.Contest;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ContestInspectCommand extends AbstractCommand<Archer> {

	public ContestInspectCommand(Archer access) {
		super(access, "inspect", Permissions.COMMAND_CONTEST_INSPECT);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		if (args.length == 3){
			return TabUtil.tabCompleteAllContests(access, sender, args.length == 3 ? args[2].trim().toLowerCase() : "", false);
		}
		else{
			return noTabChoices;
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args)
	{
		if (args.length != 3) return false;
		Contest contest = access.getContestManager().getContest(args[2].trim());
		if (contest == null) Archer.send(sender, "No Contest: " + args[2].trim().toLowerCase());
		else{
			Archer.send(sender, contest.getPropertyMessage());
		}
		return true;
	}
	
}
