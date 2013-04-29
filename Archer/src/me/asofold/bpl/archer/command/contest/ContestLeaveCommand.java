package me.asofold.bpl.archer.command.contest;

import java.util.Collection;
import java.util.List;

import me.asofold.bpl.archer.Archer;
import me.asofold.bpl.archer.command.AbstractCommand;
import me.asofold.bpl.archer.command.TabUtil;
import me.asofold.bpl.archer.config.Permissions;
import me.asofold.bpl.archer.core.Contest;
import me.asofold.bpl.archer.utils.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ContestLeaveCommand extends AbstractCommand<Archer> {
	
	public ContestLeaveCommand(Archer access) {
		super(access, "leave", Permissions.COMMAND_CONTEST_LEAVE);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		if (!Utils.checkPlayer(sender)) return null;
		if (args.length <= 3){
			final List<String> choices =  TabUtil.tabCompleteActiveContests(access, (Player) sender, args.length == 3 ? args[2] : "");
			if (!choices.isEmpty()) choices.add("*");
			return choices;
		}
		else{
			return noTabChoices;
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args)
	{
		if (!Utils.checkPlayer(sender)) return true;
		if (args.length != 3) return false;
		String arg = args[2].trim().toLowerCase();
		final Collection<Contest> active = access.getActiveContests((Player) sender);
		if (active.isEmpty()){
			Archer.send(sender, "No active contests.");
		}
		final Player player = (Player) sender;
		if (arg.equals("*")){
			// Leave all contests.
			for (Contest contest : active){
				access.leaveContest(player, contest);
			}
			Archer.send(sender, "Left all contests.");
		}
		else{
			// TODO: Access method, something better?
			for (Contest contest : active){
				if (contest.name.equalsIgnoreCase(arg)){
					access.leaveContest(player, contest);
					Archer.send(sender, "Left contest: " + contest.name);
					return true;
				}
			}
			Contest contest = access.getContestManager().getContest(arg);
			if (contest == null) Archer.send(sender, "No contest: " + arg);
			else Archer.send(sender, "Not in contest: " + contest.name);
		}
		return true;
	}
	
}
