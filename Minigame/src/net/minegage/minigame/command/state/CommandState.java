package net.minegage.minigame.command.state;


import net.minegage.common.command.Flags;
import net.minegage.core.command.CommandModule;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import net.minegage.minigame.GameManager;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandState
		extends CommandModule<GameManager> {
		
	public CommandState(GameManager manager) {
		super(manager, Rank.ADMIN, "state");
		
		addSubCommand(new CommandStateGame(manager));
		addSubCommand(new CommandStatePlayer(manager));
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		C.pHelp(player, "state player <player> <in/out>", "Sets the player state of a player");
		C.pHelp(player, "state game <loading/waiting/starting/playing/ending/dead", "Sets the game state");
	}
	
}
