package net.minegage.minigame.command.game;


import net.minegage.common.command.Flags;
import net.minegage.common.C;
import net.minegage.minigame.GameManager;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.game.Game.GameState;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandGameEnd
		extends CommandGameBase {
		
	public CommandGameEnd(GameManager manager) {
		super(manager, "end", "stop", "kill");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		Game game = getGame(player);
		if (game == null) {
			return;
		}
		
		C.bWarn("Game", "The game was stopped by " + C.fElem(player.getName()));
		
		game.setState(GameState.DEAD);
	}
	
}
