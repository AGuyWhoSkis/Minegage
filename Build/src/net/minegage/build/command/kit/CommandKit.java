package net.minegage.build.command.kit;


import net.minegage.common.command.Flags;
import net.minegage.core.command.CommandModule;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandKit
		extends CommandModule<MobkitManager> {
		
	public CommandKit(MobkitManager manager) {
		super(manager, Rank.BUILDER, "kit", "k");
		
		addSubCommand(new CommandKitCreate(manager));
		addSubCommand(new CommandKitDelete(manager));
		addSubCommand(new CommandKitClear(manager));
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		C.pHelp(player, "kit add/create <kit name> [mob type]");
		C.pHelp(player, "kit remove/delete <kit name>");
		C.pHelp(player, "kit clear");
	}
	
}
