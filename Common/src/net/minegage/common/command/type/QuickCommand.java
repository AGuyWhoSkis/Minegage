package net.minegage.common.command.type;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public abstract class QuickCommand
		extends AbstractCommand<CommandSender> {

	protected QuickCommand(String name, String... aliases) {
		super(name, aliases);
	}

	@Override
	protected CommandSender castSender(CommandSender sender) {
		return sender;
	}

	@Override
	protected boolean hasPermission(CommandSender sender) {
		if (sender instanceof ConsoleCommandSender) {
			return true;
		} else return getPermission() == null || sender.hasPermission(getPermission());
	}
}
