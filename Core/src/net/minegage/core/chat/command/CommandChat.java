package net.minegage.core.chat.command;


import net.minegage.common.command.Flags;
import net.minegage.core.chat.ChatManager;
import net.minegage.core.command.CommandModule;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandChat
		extends CommandModule<ChatManager> {
		
	public CommandChat(ChatManager chatManager) {
		super(chatManager, Rank.ADMIN, "chat");
		
		addSubCommand(new CommandChatSlow(chatManager));
		addSubCommand(new CommandChatSilence(chatManager));
		addSubCommand(new CommandChatClear());
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		C.pHelp(player, "chat slow", "Toggles slow mode for chat");
		C.pHelp(player, "chat silence", "Toggles silence mode for chat");
		C.pHelp(player, "chat clear", "Clears the chat");
	}
	
}
