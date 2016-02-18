package net.minegage.minigame.command.game;


import net.minegage.common.C;
import net.minegage.minigame.GameManager;
import net.minegage.minigame.game.Game;
import org.bukkit.entity.Player;

import net.minegage.core.command.CommandModule;
import net.minegage.core.rank.Rank;


public abstract class CommandGameBase
		extends CommandModule<GameManager> {
		
	public CommandGameBase(GameManager manager, String name, String... aliases) {
		super(manager, Rank.ADMIN, name, aliases);
	}
	
	protected Game getGame(Player player) {
		Game game = plugin.getGame();
		if (game == null) {
			C.pMain(player, "Game", "No game is in progress");
		}
		return game;
	}
	
}
