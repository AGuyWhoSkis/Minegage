package net.minegage.core.command.message;

import net.minegage.common.command.Flags;
import net.minegage.core.command.RankedCommand;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandSpy
		extends RankedCommand {

	private MessageManager messageManager;
	
	public CommandSpy(MessageManager messageManager) {
		super(Rank.MODERATOR, "spy");
		this.messageManager = messageManager;
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		messageManager.toggleSpy(player, true);
	}
	
}
