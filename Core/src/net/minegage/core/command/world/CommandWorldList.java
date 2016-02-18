package net.minegage.core.command.world;

import net.minegage.common.C;
import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilWorld;
import net.minegage.core.command.RankedCommand;
import net.minegage.core.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;

public class CommandWorldList
		extends RankedCommand {
	
	public CommandWorldList() {
		super(Rank.BUILDER, "list");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		
		List<String> unloadedList = UtilWorld.getUnloadedWorlds();
		
		Iterator<String> unloadedIt = unloadedList.iterator();
		StringBuilder unloadedBuild = new StringBuilder();
		while (unloadedIt.hasNext()) {
			unloadedBuild.append(unloadedIt.next());
			if (unloadedIt.hasNext()) {
				unloadedBuild.append(", ");
			}
		}

		List<World> loadedList = Bukkit.getWorlds();

		loadedList.sort((World a, World b) -> a.getName().compareTo(b.getName()));

		Iterator<World> loadedIt = loadedList.iterator();
		StringBuilder loadedBuild = new StringBuilder();
		while (loadedIt.hasNext()) {
			World world = loadedIt.next();
			
			boolean inside = player.getWorld().equals(world);
			if (inside) {
				loadedBuild.append(C.cPink);
			}
			
			loadedBuild.append(world.getName());
	
			int players = world.getPlayers().size();
			if (players > 0) {
				loadedBuild.append(" (" + players + ")");
			}
			
			if (loadedIt.hasNext()) {
				loadedBuild.append(", ");
			}
			if (inside) {
				loadedBuild.append(C.cGreen);
			}
		}
		
		String unloaded = C.cGray + unloadedBuild.toString();
		String loaded = C.cGreen + loadedBuild.toString();
		
		C.pMain(player, "World", "Listing worlds...");
		C.pRaw(player, "");
		C.pRaw(player, C.sOut + "Unloaded (" + unloadedList.size() + "):");
		C.pRaw(player, unloaded);
		C.pRaw(player, C.sOut + "Loaded (" + loadedList.size() + "):");
		C.pRaw(player, loaded);
		C.pRaw(player, "");
	}
	
}
