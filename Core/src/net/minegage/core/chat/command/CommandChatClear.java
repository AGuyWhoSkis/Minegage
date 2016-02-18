package net.minegage.core.chat.command;


import net.minegage.common.command.Flags;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandChatClear
		extends RankedCommand {
		
		
	public CommandChatClear() {
		super(Rank.ADMIN, "clear");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		for (int i = 0; i < 100; i++) {
			C.bRaw("");
		}
		
		C.bMain("Chat", "Chat has been cleared by " + player.getDisplayName());
	}
	
	
	
}
