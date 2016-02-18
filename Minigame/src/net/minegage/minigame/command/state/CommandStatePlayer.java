package net.minegage.minigame.command.state;


import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilJava;
import net.minegage.core.command.CommandModule;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import net.minegage.minigame.GameManager;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.game.Game.PlayerState;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandStatePlayer
		extends CommandModule<GameManager> {
		
	public CommandStatePlayer(GameManager manager) {
		super(manager, Rank.ADMIN, "player");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		if (args.size() < 1) {
			C.pMain(player, "State", "Please specify a player");
			return;
		}
		
		String playerName = args.get(0);
		Player target = Bukkit.getPlayerExact(playerName);
		if (target == null) {
			C.pMain(player, "State", "Player " + C.fElem(playerName) + " not found");
			return;
		}
		
		if (args.size() < 2) {
			C.pMain(player, "State", "Please specify a player state; in or out");
			return;
		}
		
		PlayerState state = UtilJava.parseEnum(PlayerState.class, args.get(1));
		if (state == null) {
			C.pMain(player, "State", "Invalid player state " + C.fElem(args.get(1)) + "; must be in or out");
			return;
		}
		
		Game game = plugin.getGame();
		if (game == null) {
			C.pMain(player, "State", "No game is currently running");
			return;
		}
		
		PlayerState currentState = game.getState(target);
		if (currentState == state) {
			C.pMain(player, "State", C.fElem(playerName) + " already has that player state");
			return;
		}
		if (state == PlayerState.OUT) {
			game.out(target, false);
			
			C.bMain("Game", C.fElem(target.getName()) + " was removed from the game by " + C.fElem(player.getName()));
		} else {
			game.setState(target, state);
			game.assignTeam(target);
			game.respawn(target);
			
			C.bMain("Game", C.fElem(target.getName()) + " was added to the game by " + C.fElem(player.getName()));
		}
	}
	
}
