package me.asofold.bpl.archer.command.contest;

import java.util.List;

import me.asofold.bpl.archer.Archer;
import me.asofold.bpl.archer.command.AbstractCommand;
import me.asofold.bpl.archer.command.TabUtil;
import me.asofold.bpl.archer.config.Permissions;
import me.asofold.bpl.archer.core.Contest;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ContestEndCommand extends AbstractCommand<Archer> {

	public ContestEndCommand(Archer access) {
		super(access, "end", Permissions.COMMAND_CONTEST_END);
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
			access.getContestManager().endAllContests("Aborted by an administrator.");
			Archer.send(sender, "All constests ended.");
		}
		else{
			final Contest contest = access.getContestManager().getContest(name);
			if (contest == null){
				Archer.send(sender, "No contest: " + name);
			}
			else{
				contest.endContest("Abortet by an administrator.");
				Archer.send(sender, "Contest ended: " + contest.name);
			}
		}
		return true;
	}
	
	

}
