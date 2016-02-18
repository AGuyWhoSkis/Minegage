package net.minegage.core.command.world;

import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilWorld;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;

public class CommandWorldDelete
		extends RankedCommand {
	
	public CommandWorldDelete() {
		super(Rank.ADMIN, "d");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		if (args.size() == 0) {
			C.pMain(player, "World", "Please specify a world name");
			return;
		}
		
		String worldName = UtilJava.joinList(args, " ");
		if (UtilWorld.isMainWorld(worldName)) {
			C.pMain(player, "World", "You can't delete the main world");
			return;
		}
		
		if (!UtilWorld.exists(worldName)) {
			C.pMain(player, "World", "World \"" + worldName + "\" does not exist");
			return;
		}
		
		C.pMain(player, "World", "Deleting world...");
		
		try {
			UtilWorld.delete(worldName);
		} catch (IOException ex) {
			C.pErr(ex, player, "Unable to delete world");
			return;
		}
		
		C.pMain(player, "World", "Done");
	}
}
