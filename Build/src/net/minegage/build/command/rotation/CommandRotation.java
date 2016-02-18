package net.minegage.build.command.rotation;


import net.minegage.common.command.Flags;
import net.minegage.core.command.CommandModule;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandRotation
		extends CommandModule<RotationManager> {
		
	public CommandRotation(RotationManager manager) {
		super(manager, Rank.ADMIN, "rotation", "rot", "ro");
		
		addSubCommand(new CommandRotationAdd(manager));
		addSubCommand(new CommandRotationRemove(manager));
		addSubCommand(new CommandRotationUpdate(manager));
		addSubCommand(new CommandRotationList(manager));
		addSubCommand(new CommandRotationReload(manager));
		addSubCommand(new CommandRotationGet(manager));
		addSubCommand(new CommandRotationSync(manager));
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		C.pHelp(player, "rotation add/register <gametype> [map]", "Adds a map to rotation");
		C.pHelp(player, "rotation remove/delete <gametype> [map]", "Removes a map from rotation");
		C.pHelp(player, "rotation update <gametype> [map]", "Updates a map in rotation");
		C.pHelp(player, "rtaotion get/fetch/download <gametype> <map>", "Gets a local copy of the map in rotation");
		C.pHelp(player, "rotation list/display/show <gametype>", "Lists maps in the rotation of the specified gametype");
		C.pHelp(player, "rotation reload/refresh", "Reloads the map rotation from disk");
		C.pHelp(player, "rotation sync/push", "Synchronizes the rotation to all other servers");
	}
	
}
