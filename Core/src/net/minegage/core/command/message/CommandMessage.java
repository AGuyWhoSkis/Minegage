package net.minegage.core.command.message;

import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilJava;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandMessage
		extends RankedCommand {
	
	private MessageManager messageManager;
	
	public CommandMessage(MessageManager messageManager) {
		super(Rank.DEFAULT, "message", "msg", "m", "tell", "t", "whisper", "w", "pm");
		this.messageManager = messageManager;
	}

	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		if (args.size() == 0) {
			C.pMain(player, "Msg", "Please specify a player");
			return;
		}
		
		String target = args.get(0);
		Player match = messageManager.getServer().getPlayer(target);
		
		if (match == null) {
			C.pMain(player, "Msg", "Player \"" + target + "\" not found");
			return;
		}
		
		if (args.size() == 1) {
			C.pMain(player, "Msg", "Please enter a message");
			return;
		}
		
		String message = UtilJava.joinList(args, " ", 1, args.size() - 1);
		messageManager.message(player, match, message);
	}	
	
}
