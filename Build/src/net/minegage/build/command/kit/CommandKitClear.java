package net.minegage.build.command.kit;


import net.minegage.common.command.Flags;
import net.minegage.core.command.CommandModule;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandKitClear
		extends CommandModule<MobkitManager> {
		
	public CommandKitClear(MobkitManager manager) {
		super(manager, Rank.BUILDER, "clear");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		boolean success = plugin.getFile(player.getWorld())
				.delete();
				
		if (success) {
			C.pMain(player, "Kit", "Cleared all mobkits");
		} else {
			C.pMain(player, "Kit", "Unable to clear mobkits");
		}
		
	}
	
}
