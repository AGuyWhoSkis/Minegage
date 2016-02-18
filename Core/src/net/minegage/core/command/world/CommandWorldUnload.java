package net.minegage.core.command.world;

import net.minegage.common.command.Flags;
import net.minegage.common.data.Data;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilWorld;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandWorldUnload
		extends RankedCommand {
	
	public CommandWorldUnload() {
		super(Rank.ADMIN, "unload");
		addFlag("save", Data.NULL);
		addFlag("force", Data.NULL);
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		if (args.size() < 1) {
			C.pMain(player, "World", "Please specify a world name");
			return;
		}
		
		String worldName = UtilJava.joinList(args, " ");
		
		if (!UtilWorld.exists(worldName)) {
			C.pMain(player, "World", "World \"" + worldName + "\" does not exist");
			return;
		}
		
		World world = Bukkit.getWorld(worldName);
		if (world == null) {
			C.pMain(player, "World", "World \"" + worldName + "\" is not loaded");
			return;
		}
		
		boolean save = true;
		if (flags.has("save")) {
			save = false;
		}
		
		boolean unloaded;
		String message;
		if (flags.has("force")) {
			unloaded = UtilWorld.forceUnload(world, save);
			message = unloaded ? "forcibly unloaded" : "failed to forcibly unload";
		} else {
			
			if (UtilWorld.isMainWorld(worldName)) {
				C.pMain(player, "World", "The -force flag is required to unload the main world");
				return;
			}
			
			unloaded = UtilWorld.unload(world, save);
			message = unloaded ? "unloaded" : "failed to unload";
		}
		
		C.pMain(player, "World", "World \"" + worldName + "\" " + message);
	}
	
}
