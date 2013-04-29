package me.asofold.bpl.archer.command.contest;

import java.util.List;

import me.asofold.bpl.archer.Archer;
import me.asofold.bpl.archer.command.AbstractCommand;
import me.asofold.bpl.archer.command.TabUtil;
import me.asofold.bpl.archer.config.Permissions;
import me.asofold.bpl.archer.config.properties.Property;
import me.asofold.bpl.archer.core.Contest;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ContestEditCommand extends AbstractCommand<Archer> {

	public ContestEditCommand(Archer access) {
		super(access, "edit", Permissions.COMMAND_CONTEST_EDIT);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		if (args.length <= 3){
			return TabUtil.tabCompleteAllContests(access, sender, args.length == 3 ? args[2].trim().toLowerCase() : "", false);
		}
		else if (args.length == 4){
			final Contest contest = access.getContestManager().getContest(args[2].trim());
			if (contest == null) return noTabChoices;
			else return contest.tabCompleteProperties(args[3].trim());
		}
		else{
			return noTabChoices;
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args)
	{
		if (args.length != 5) return false;
		Contest contest = access.getContestManager().getContest(args[2].trim());
		if (contest == null) Archer.send(sender, "No Contest: " + args[2].trim().toLowerCase());
		else{
			// TODO: Further perm checks depending on ownership and other.
			
			final String propArg = args[3].trim().toLowerCase();
			// TODO: Edit world (needs re-register).
			// TODO: Consider ending contest ?
			if (propArg.equals("world")){
				String wn = args[4].trim();
				if (wn.isEmpty()) wn = "*";
				access.getContestManager().changeWorld(contest, wn);
				Archer.send(sender, "Contest " + contest.name + ": world set to " + wn + " .");
				return true;
			}
			
			double val;
			try{
				val = Double.parseDouble(args[4]);
			}
			catch(NumberFormatException e){
				Archer.send(sender, "Invalid number: " + args[4]);
				return true;
			}
			
			final Property prop;
			try{
				prop = contest.getProperty(propArg);
				if (prop == null) throw new IllegalArgumentException();
				prop.set(val); // TODO: Does this cover NaN ?
				if (prop.value == val){
					Archer.send(sender, "Contest " + contest.name + ": " + prop.name + " set to " + prop.value + " .");
				}
				else{
					Archer.send(sender, "Contest " + contest.name + ": " + prop.name + " value out of bounds (" + val + ") set to " + prop.value + " instead.");
				}
			}
			catch(Throwable t){
				Archer.send(sender, "Bad property: " + args[3]);
				return true;
			}
		}
		return true;
	}
	
}
