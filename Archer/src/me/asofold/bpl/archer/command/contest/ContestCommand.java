package me.asofold.bpl.archer.command.contest;

import me.asofold.bpl.archer.Archer;
import me.asofold.bpl.archer.command.AbstractCommand;
import me.asofold.bpl.archer.config.Permissions;

public class ContestCommand extends AbstractCommand<Archer> {

	public ContestCommand(Archer access) {
		super(access, "contest", Permissions.ACCESS_COMMAND_CONTEST);
		addSubCommands(
			);
	}

}
