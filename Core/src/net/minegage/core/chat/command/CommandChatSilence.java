package net.minegage.core.chat.command;


import net.minegage.common.command.Flags;
import net.minegage.core.chat.ChatManager;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandChatSilence
		extends RankedCommand {
		
	private ChatManager manager;
	
	public CommandChatSilence(ChatManager manager) {
		super(Rank.ADMIN, "silence", "mute");
		this.manager = manager;
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		boolean silence = !manager.isSilenced();
		manager.setSilenced(silence);
		
		String toggle = silence ? "silenced" : "unsilenced";
		C.bMain("Chat", "Chat has been " + C.fElem(toggle) + " by " + player.getDisplayName());
	}

}
