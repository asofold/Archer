package me.asofold.bpl.archer.command;

import me.asofold.bpl.archer.Archer;
import me.asofold.bpl.archer.command.contest.ContestCommand;
import me.asofold.bpl.archer.config.Permissions;

/**
 * Root command.
 * @author mc_dev
 *
 */
public class ArcherCommand extends AbstractCommand<Archer> {

	public ArcherCommand(Archer access) {
		super(access, "archer", Permissions.ACCESS_COMMAND_ARCHER);
		addSubCommands(
			new ContestCommand(access),
			new NotifyCommand(access),
			new ReloadCommand(access)
			);
	}

}
