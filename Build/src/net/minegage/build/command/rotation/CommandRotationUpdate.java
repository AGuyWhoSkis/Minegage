package net.minegage.build.command.rotation;


import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilJava;
import net.minegage.core.command.CommandModule;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import net.minegage.minigame.game.GameType;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandRotationUpdate
		extends CommandModule<RotationManager> {
		
	public CommandRotationUpdate(RotationManager manager) {
		super(manager, Rank.ADMIN, "update", "reregister", "replace");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		if (args.size() < 1) {
			C.pMain(player, "Rotation", "Please specify a gametype");
			return;
		}
		
		String typeString = args.get(0);
		GameType type = UtilJava.parseEnum(GameType.class, typeString);
		if (type == null) {
			C.pMain(player, "Rotation", "Invalid gametype \"" + typeString + "\"");
			return;
		}
		
		String map = player.getWorld()
				.getName();
		if (args.size() > 1) {
			map = UtilJava.joinList(args, " ", 1);
		}
		
		plugin.removeMap(type, map, player);
		plugin.addMap(type, map, player);
	}
	
}
