package me.asofold.bpl.archer.command;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

/**
 * Base command class, featuring some features.
 * @author mc_dev
 *
 */
public abstract class AbstractCommand<A> implements TabExecutor{

	protected final A access;
	public final String label;
	/** Permission necessary to use this command. May be null. */
	public final String permission;
	/** Sub commands for delegation. */
	protected final Map<String, AbstractCommand<?>> subCommands = new LinkedHashMap<String, AbstractCommand<?>>();
	/** The index in args to check for sub-commands. */
	protected int subCommandIndex = 0;

	public AbstractCommand(A access, String label, String permission){
		this.access = access;
		this.label = label;
		this.permission = permission;
	}
	
	public void addSubCommands(AbstractCommand<?>... commands){
		for (AbstractCommand<?> subCommand : commands ){
			subCommands.put(subCommand.label, subCommand);
		}
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
	{
		final List<String> choices = new ArrayList<String>(subCommands.size());
		int len = args.length;
		// Attempt to delegate.
		if (len == subCommandIndex || len == subCommandIndex + 1){
			String arg = len == subCommandIndex ? "" : args[subCommandIndex].trim().toLowerCase();
			for (AbstractCommand<?> cmd : subCommands.values()){
				if (cmd.label.startsWith(arg) && (cmd.permission == null || sender.hasPermission(cmd.permission))){
					choices.add(cmd.label);
				}
			}
		}
		else if (len > subCommandIndex + 1){
			String arg = args[subCommandIndex].trim().toLowerCase();
			AbstractCommand<?> subCommand = subCommands.get(arg);
			if (subCommand != null && (subCommand.permission == null || sender.hasPermission(subCommand.permission))){
				return subCommand.onTabComplete(sender, command, alias, args);
			}
		}
		// No tab completion by default.
		return choices;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args)
	{
		int len = args.length;
		if (len > subCommandIndex){
			String arg = args[subCommandIndex].trim().toLowerCase();
			AbstractCommand<?> subCommand = subCommands.get(arg);
			if (subCommand != null){
				if (subCommand.permission != null && !sender.hasPermission(subCommand.permission)){
					sender.sendMessage(ChatColor.DARK_RED + "You don't have permission.");
					return true;
				}
				return subCommand.onCommand(sender, command, alias, args);
			}
		}
		// Usage.
		return false;
	}

}
