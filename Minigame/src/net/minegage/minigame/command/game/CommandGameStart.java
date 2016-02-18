package net.minegage.minigame.command.game;


import net.minegage.common.command.Flags;
import net.minegage.common.C;
import net.minegage.minigame.GameManager;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.game.Game.GameState;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandGameStart
		extends CommandGameBase {
		
	public CommandGameStart(GameManager gameManager) {
		super(gameManager, "start");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		Game game = getGame(player);
		if (game == null) {
			return;
		}
		
		if (!game.inLobby()) {
			C.pMain(player, "Game", "A game is already running");
			return;
		}

		game.paused = false;
		game.setState(GameState.PLAYING);
	}
	
}
