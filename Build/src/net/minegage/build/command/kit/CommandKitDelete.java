package net.minegage.build.command.kit;


import net.minegage.common.command.Flags;
import net.minegage.core.command.CommandModule;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;


public class CommandKitDelete
		extends CommandModule<MobkitManager> {
		
	public CommandKitDelete(MobkitManager manager) {
		super(manager, Rank.BUILDER, "delete", "del", "d", "remove", "rem", "r");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		if (args.size() < 1) {
			C.pMain(player, "Kit", "Please specify a kit name");
			return;
		}
		
		String kitName = args.get(0);
		World world = player.getWorld();
		
		try {
			List<String> lines = plugin.readLines(world);
			
			Iterator<String> linesIt = lines.iterator();
			while (linesIt.hasNext()) {
				String line = linesIt.next();
				if (kitName.equalsIgnoreCase("all") || line.contains(kitName)) {
					linesIt.remove();
				}
			}
			
			String removed = ( kitName.equalsIgnoreCase("all") ) ? C.sOut + "all kits" : "kit " + C.fElem(kitName);
			
			plugin.writeLines(world, lines);
			C.pMain(player, "Kit", "Removed " + removed);
			
		} catch (IOException ex) {
			C.pErr(ex, player, "Unable to remove kit(s)");
		}
	}
	
}
