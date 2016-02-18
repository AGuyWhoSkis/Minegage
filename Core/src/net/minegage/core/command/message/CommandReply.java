package net.minegage.core.command.message;

import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilJava;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandReply
		extends RankedCommand {
	
	private MessageManager messageManager;
	
	public CommandReply(MessageManager messageManager) {
		super(Rank.DEFAULT, "reply", "r");
		this.messageManager = messageManager;
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		if (args.size() == 0) {
			C.pMain(player, "Msg", "Please specify a message");
			return;
		}
		
		String message = UtilJava.joinList(args, " ");
		messageManager.reply(player, message);
	}
	
}
