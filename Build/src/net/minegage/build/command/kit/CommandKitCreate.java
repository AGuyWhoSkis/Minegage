package net.minegage.build.command.kit;


import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilPos;
import net.minegage.core.command.CommandModule;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;


public class CommandKitCreate
		extends CommandModule<MobkitManager> {
		
	public CommandKitCreate(MobkitManager manager) {
		super(manager, Rank.BUILDER, "create", "cr", "c", "new", "n", "add", "a");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		if (args.size() < 1) {
			C.pMain(player, "Kit", "Please specify a kit name");
			return;
		}
		
		String kitName = args.get(0);
		String kitLoc = UtilPos.serializeLocation(player.getLocation());
		String fileString = kitName + ":" + kitLoc;
		
		
		World world = player.getWorld();
		
		List<String> lines;
		try {
			lines = plugin.readLines(world);
			
			for (String str : lines) {
				if (str.contains(kitName)) {
					C.pMain(player, "Kit", "Kit " + C.fElem(kitName) + " already exists");
					return;
				}
			}
			
			lines.add(fileString);
			
			plugin.writeLines(world, lines);
			C.pMain(player, "Kit", "Kit " + C.fElem(kitName) + " created");
			
		} catch (IOException ex) {
			C.pErr(ex, player, "Unable to create mobkit");
		}
		
	}
	
}
