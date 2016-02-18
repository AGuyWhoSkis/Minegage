package net.minegage.build.command.map;


import net.minegage.common.C;
import net.minegage.common.command.Flags;
import org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class CommandMapRemove
		extends CommandMapAction {
		
	public CommandMapRemove() {
		super("remove", "delete", "del", "rem");
	}

	@Override
	protected void doAction(Player player, List<String> args, Flags flags, String world, File asset) {
		if (!asset.exists()) {
			C.pMain(player, "Map", "The file for " + C.fElem(world) + " doesn't exist!");
			return;
		}

		try {
			FileUtils.forceDelete(asset);
		} catch (IOException ex) {
			C.pErr(ex, player, "Unable to remove file");
			return;
		}

		C.pMain(player, "Map", "Removed file " + C.fElem(world) + "!");
	}

}
