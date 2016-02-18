package net.minegage.core.command.world;

import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilWorld;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandWorldTeleport
		extends RankedCommand {
	
	public CommandWorldTeleport() {
		super(Rank.BUILDER, "teleport", "tp", "change", "ch");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		if (args.size() == 0) {
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
			C.pMain(player, "World", "Loading world \"" + worldName + "\"...");
			world = UtilWorld.load(worldName);

			if (world == null) {
				C.pWarn(player, "World", "Something went wrong when loading that world");
				return;
			}
		}
		
		World oldWorld = player.getWorld();
		
		player.teleport(world.getSpawnLocation());
		C.pMain(player, "World", "Teleported you to world \"" + worldName + "\"");
		
		if (!UtilWorld.isMainWorld(oldWorld) && oldWorld.getPlayers().size() == 0) {
			UtilWorld.unload(oldWorld, true);
		}
		
	}
	
}
