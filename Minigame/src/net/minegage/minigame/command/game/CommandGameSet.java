package net.minegage.minigame.command.game;


import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilJava;
import net.minegage.common.C;
import net.minegage.minigame.GameManager;
import net.minegage.minigame.game.GameType;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandGameSet
		extends CommandGameBase {
		
	public CommandGameSet(GameManager manager) {
		super(manager, "set");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		if (args.size() < 1) {
			C.pMain(player, "Game", "Please specify a gametype");
			return;
		}
		
		String   typeString = UtilJava.joinList(args, " ");
		GameType type       = UtilJava.parseEnum(GameType.class, typeString);
		if (type == null) {
			C.pMain(player, "Game", "Gametype " + C.fElem(typeString) + " not found");
			return;
		}
		
		plugin.getGameRotation()
				.clear();
		plugin.getGameRotation()
				.add(type);
				
		plugin.killGame();
		plugin.createGame(type);
		
		C.bMain("Game", "The game was set to " + C.fElem(type.getName()) + " by " + C.fElem2(player.getName()));
	}
	
}
