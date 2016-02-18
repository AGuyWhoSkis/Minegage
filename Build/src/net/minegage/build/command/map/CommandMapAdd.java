package net.minegage.build.command.map;


import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilWorld;
import net.minegage.common.util.UtilZip;
import net.minegage.common.C;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class CommandMapAdd
		extends CommandMapAction {
		
	public CommandMapAdd() {
		super("add");
	}
	
	public void doAction(Player player, List<String> args, Flags flags, String world, File asset) {
		World bukkitWorld = Bukkit.getWorld(world);
		if (bukkitWorld != null) {
			UtilWorld.forceUnload(bukkitWorld, true);
		}

		File worldDir = UtilWorld.getWorldFile(world);
		
		// Delete unnecessary files
		for (File child : worldDir.listFiles()) {
			String name = child.getName();
			if (name.equals("playerdata") || name.equals("session.lock") || name.equals("uid.dat") || name.equals("data") || name.equals(
					"level.dat_old")) {
				FileUtils.deleteQuietly(child);
			}
		}
		
		if (asset.exists()) {
			try {
				FileUtils.forceDelete(asset);
			} catch (IOException ex) {
				C.pErr(ex, player, "Unable to overwrite file");
				return;
			}
		}
		
		try {
			UtilZip.compress(asset, worldDir.listFiles());
			C.pMain(player, "Rotation", "Map added!");
		} catch (IOException ex) {
			C.pErr(ex, player, "Unable to add map");
		}
	}

}
