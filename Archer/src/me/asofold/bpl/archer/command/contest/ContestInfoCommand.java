package me.asofold.bpl.archer.command.contest;

import java.util.Collection;
import java.util.List;

import me.asofold.bpl.archer.Archer;
import me.asofold.bpl.archer.command.AbstractCommand;
import me.asofold.bpl.archer.config.Permissions;
import me.asofold.bpl.archer.core.Contest;
import me.asofold.bpl.archer.utils.Utils;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ContestInfoCommand extends AbstractCommand<Archer> {

	public ContestInfoCommand(Archer access) {
		// TODO: Might use extra permission later.
		super(access, "info", Permissions.ACCESS_COMMAND_CONTEST);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		if (args.length <= 3){
			return access.tabCompleteAvailableContests(sender, args.length == 3 ? args[2] : "");
		}
		else{
			return noTabChoices;
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args)
	{
		if (args.length == 3){
			String arg = args[2].trim().toLowerCase();
			if (arg.equals("*")){
				sendInfoAll(sender);
				return true;
			}
			else{
				sendInfo(sender, arg);
				return true;
			}
		}
		else if (args.length < 3){
			// Info all
			sendInfoAll(sender);
			return true;
		}
		else return false;
	}

	private void sendInfo(CommandSender sender, String contestName) {
		Contest contest = access.getContestManager().getContest(contestName);
		if (contest == null){
			Archer.send(sender, "No contest: " + contestName);
		} else {
			// TODO: Send contest details info ...
			String[] msgs = new String[]{
				"Contest: " + contest.name,
				"Players: " + Utils.joinObjects(contest.getOnlineNameList(), ", "),
				"Starting: " + (contest.started ? "Already started." : (contest.startDelay.nonzero() && contest.lastTimeValid > 0 ? "soon" : "unknown")),
			};
			Archer.send(sender, msgs);
		}
	}

	private void sendInfoAll(CommandSender sender) {
		final Collection<Contest> available;
		if (sender instanceof Player){
			Collection<Contest> active = access.getActiveContests((Player) sender);
			if (active.isEmpty()){
				Archer.send(sender, "(No active contests.)");
			}
			else{
				Archer.send(sender, "Active contests: " + Utils.joinObjects(active, " | "));
			}
			available = access.getAvailableContests((Player) sender);
		}
		else{
			available = access.getAvailableContests(null);
		}
		if (available.isEmpty()){
			Archer.send(sender, "(No more contests available.)");
		}
		else{
			Archer.send(sender, "Available contests: " + Utils.joinObjects(available, " | "));
		}
	}

	
	
}
