package net.minegage.common.command.type;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public abstract class ConsoleCommand
		extends AbstractCommand<ConsoleCommandSender> {

	protected ConsoleCommand(String name, String... aliases) {
		super(name, aliases);
	}

	@Override
	protected ConsoleCommandSender castSender(CommandSender sender) {
		return (ConsoleCommandSender) sender;
	}

	@Override
	protected boolean hasPermission(ConsoleCommandSender sender) {
		return true;
	}
}
