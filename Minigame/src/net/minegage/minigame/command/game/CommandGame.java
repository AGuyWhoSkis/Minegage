package net.minegage.minigame.command.game;


import net.minegage.common.command.Flags;
import net.minegage.minigame.GameManager;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandGame
		extends CommandGameBase {
		
	public CommandGame(GameManager manager) {
		super(manager, "game");
		
		addSubCommand(new CommandGameStart(manager));
		addSubCommand(new CommandGameEnd(manager));
		addSubCommand(new CommandGamePause(manager));
		addSubCommand(new CommandGameSet(manager));
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
	
	}
	
}
