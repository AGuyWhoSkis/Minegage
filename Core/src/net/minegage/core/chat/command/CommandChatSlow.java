package net.minegage.core.chat.command;


import net.minegage.common.command.Flags;
import net.minegage.core.chat.ChatManager;
import net.minegage.core.command.CommandModule;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandChatSlow
		extends CommandModule<ChatManager> {
		
	private ChatManager chatManager;
	
	public CommandChatSlow(ChatManager chatManager) {
		super(chatManager, Rank.ADMIN, "slow");
		this.chatManager = chatManager;
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		boolean slow = !chatManager.isSlow();
		chatManager.setSlow(slow);
		
		String toggle = slow ? "enabled" : "disabled";
		C.bMain("Chat", "Slow chat has been " + C.fElem(toggle) + " by " + player.getDisplayName());
	}
	
}
