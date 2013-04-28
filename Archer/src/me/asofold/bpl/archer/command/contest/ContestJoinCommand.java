package me.asofold.bpl.archer.command.contest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import me.asofold.bpl.archer.Archer;
import me.asofold.bpl.archer.command.AbstractCommand;
import me.asofold.bpl.archer.config.Permissions;
import me.asofold.bpl.archer.core.Contest;
import me.asofold.bpl.archer.utils.Utils;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ContestJoinCommand extends AbstractCommand<Archer> {

	public ContestJoinCommand(Archer access) {
		super(access, "join", Permissions.COMMAND_CONTEST_JOIN);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		if (!Utils.checkPlayer(sender)) return null;
		if (args.length <= 3){
			final List<String> choices =  access.tabCompleteAvailableContests(sender, args.length == 3 ? args[2] : "");
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
		final Player player = (Player) sender;
		final Location loc = player.getLocation();
		final Collection<Contest> available = access.getAvailableContests(player, loc);
		if (available.isEmpty()){
			Archer.send(sender, "No more contests available.");
			return true;
		}
		if (arg.equals("*")){
			// Join all contests.
			List<String> done = new ArrayList<String>(available.size());
			for (Contest contest : available){
				if (access.joinContest(player, loc, contest)){
					done.add(contest.name);
				}
				else{
					Archer.send(sender, "Failed to join contest: " + contest.name);
				}
			}
			if (done.isEmpty()){
				Archer.send(sender, "Could not join any contests.");
			}
			else{
				Archer.send(sender, "Contests joined: " + Utils.joinObjects(done, " | "));
			}
		}
		else{
			Contest contest = access.getContestManager().getContest(arg);
			if (contest == null){
				Archer.send(sender, "No contest: " + arg);
			}
			else{
				if (access.joinContest(player, loc, contest)){
					Archer.send(sender, "Joined contest: " + contest.name);
				}
				else{
					Archer.send(sender, "Failed to join contest: " + contest.name);
				}
			}
		}
		return true;
	}

}
