package me.asofold.bpl.archer.command.contest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import me.asofold.bpl.archer.Archer;
import me.asofold.bpl.archer.command.AbstractCommand;
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
		if (args.length <= 3){
			final Collection<Contest> contests = access.getContestManager().getAllContests();
			final String arg = args.length == 3 ? args[2].trim().toLowerCase() : "";
			final List<String> choices = new ArrayList<String>(contests.size());
			for (final Contest contest : contests){
				if (contest.name.toLowerCase().startsWith(arg)){
					choices.add(contest.name);
				}
			}
			Collections.sort(choices, String.CASE_INSENSITIVE_ORDER);
			choices.add("*");
			return choices;
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
			for (final Contest contest : access.getContestManager().getAllContests()){
				contest.endContest("Abortet by an administrator.");
			}
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
