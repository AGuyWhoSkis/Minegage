package net.minegage.minigame.command.game;


import net.minegage.common.command.Flags;
import net.minegage.common.C;
import net.minegage.minigame.GameManager;
import net.minegage.minigame.game.Game;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandGamePause
		extends CommandGameBase {
		
	public CommandGamePause(GameManager manager) {
		super(manager, "pause", "unpause", "resume");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		Game game = getGame(player);
		if (game == null) {
			return;
		}
		
		game.paused = !game.paused;
		String paused = ( game.paused ) ? "paused" : "resumed";
		
		C.pMain(player, "Game", "Game " + paused);
	}
	
}
