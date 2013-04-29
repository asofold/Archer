package me.asofold.bpl.archer.command.contest;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import me.asofold.bpl.archer.Archer;
import me.asofold.bpl.archer.command.AbstractCommand;
import me.asofold.bpl.archer.command.TabUtil;
import me.asofold.bpl.archer.config.Permissions;
import me.asofold.bpl.archer.core.Contest;



public class ContestDeleteCommand extends AbstractCommand<Archer> {

	public ContestDeleteCommand(Archer access) {
		super(access, "delete", Permissions.COMMAND_CONTEST_DELETE);
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		if (args.length == 3){
			return TabUtil.tabCompleteAllContests(access, sender, args.length == 3 ? args[2].trim().toLowerCase() : "", true);
		}
		else{
			return noTabChoices;
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args)
	{
		if (args.length != 3) return false;
		final String name = args[2].trim().toLowerCase();
		if (name.equals("*")){
			access.getContestManager().deleteAllContests();
			Archer.send(sender, "All constests deleted.");
		}
		else{
			final Contest contest = access.getContestManager().getContest(name);
			if (contest == null){
				Archer.send(sender, "No contest: " + name);
			}
			else{
				access.getContestManager().deleteContest(contest);
				Archer.send(sender, "Contest deleted: " + contest.name);
			}
		}
		return true;
	}
	
}
