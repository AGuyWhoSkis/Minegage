package net.minegage.minigame.board.helper;

import net.minegage.common.board.Board;
import net.minegage.common.module.PluginModule;
import net.minegage.minigame.board.GameBoardManager;
import net.minegage.minigame.game.Game;
import org.bukkit.entity.Player;

/**
 * Only enabled when game state is playing or ending
 */
public abstract class BoardHelper
		extends PluginModule {

	protected Game game;

	public BoardHelper(Game game, String name) {
		super(name + " Board Helper", game.getPlugin());
		this.game = game;
	}

	public abstract void giveBoard(Player player, Board board);

	protected Game getGame() {
		return game;
	}

	protected GameBoardManager getBoardManager() {
		return getGame().getBoardManager();
	}

}
