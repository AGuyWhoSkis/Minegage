package net.minegage.minigame.command.state;


import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilJava;
import net.minegage.core.command.CommandModule;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import net.minegage.minigame.GameManager;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.game.Game.GameState;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandStateGame
		extends CommandModule<GameManager> {
		
	public CommandStateGame(GameManager manager) {
		super(manager, Rank.ADMIN, "game");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		if (args.size() < 1) {
			C.pMain(player, "State", "Please specify a game state; loading, waiting, starting, playing, ending, or dead");
			return;
		}
		
		GameState state = UtilJava.parseEnum(GameState.class, args.get(0));
		if (state == null) {
			C.pMain(player, "State", "Invalid game state " + C.fElem(args.get(0))
			                         + "; must be loading, waiting, starting, playing, ending, or dead");
			return;
		}
		
		Game game = plugin.getGame();
		if (game == null) {
			C.pMain(player, "State", "A game is not currently running");
			return;
		}
		
		game.setState(state);
		C.pMain(player, "State", "Set game state to " + C.fElem(args.get(0)));
	}
}
