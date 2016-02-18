package net.minegage.build.command.rotation;


import net.minegage.common.command.Flags;
import net.minegage.core.command.CommandModule;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandRotationReload
		extends CommandModule<RotationManager> {
		
	public CommandRotationReload(RotationManager manager) {
		super(manager, Rank.ADMIN, "reload", "refresh");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		plugin.getMapManager()
				.loadMapRotation();
		C.pMain(player, "Rotation", "Rotation reloaded");
	}
	
}
