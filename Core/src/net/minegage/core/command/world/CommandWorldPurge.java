package net.minegage.core.command.world;


import net.minegage.common.command.Flags;
import net.minegage.common.data.Data;
import net.minegage.common.util.UtilWorld;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandWorldPurge
		extends RankedCommand {
	
	public CommandWorldPurge() {
		super(Rank.ADMIN, "purge");
		addFlag("all", Data.NULL);
		addFlag("save", Data.NULL);
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		
		boolean all = flags.has("all");
		boolean save = !flags.has("save");
		
		C.pMain(player, "World", "Purging worlds...");
		
		int count = 0;
		
		for (World world : Bukkit.getWorlds()) {
			if (UtilWorld.isMainWorld(world)) {
				continue;
			}
			
			//All flag means automatic unload
			//Otherwise, unload if more than 0 players
			
			if (!all && world.getPlayers().size() > 0) {
				continue;
			}
			
			boolean unloaded = UtilWorld.unload(world, save);
			String message = unloaded ? "unloaded" : "failed to unload";
			C.pMain(player, "World", "World \"" + world + "\"" + message);
			count++;
		}
		
		C.pMain(player, "World", count + " worlds unloaded");
	}
	
}
