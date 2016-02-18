package net.minegage.common.command.type;

import net.minegage.common.command.Flags;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class PlayerCommand
		extends AbstractCommand<Player> {

	protected PlayerCommand(String name, String... aliases) {
		super(name, aliases);
	}

	public abstract void onCommand(Player player, List<String> args, String raw, Flags flags);

	@Override
	protected Player castSender(CommandSender sender) {
		if (sender instanceof Player) {
			return (Player) sender;
		}

		return null;
	}

	@Override
	protected boolean hasPermission(Player sender) {
		return getPermission() == null || sender.hasPermission(getPermission());
	}
}
