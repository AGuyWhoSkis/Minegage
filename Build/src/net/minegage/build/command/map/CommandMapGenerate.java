package net.minegage.build.command.map;


import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilWorld;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandMapGenerate
		extends RankedCommand {
		
	public CommandMapGenerate() {
		super(Rank.BUILDER, "generate", "gen");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		if (args.size() < 1) {
			C.pMain(player, "Map", "Please specify a map name");
			return;
		}
		
		String mapName = UtilJava.joinList(args, " ");
		if (UtilWorld.exists(mapName)) {
			C.pMain(player, "Map", "That world already exists!");
			return;
		}
		
		C.pMain(player, "Map", "Generating map...");
		
		WorldCreator creator = WorldCreator.name(mapName)
				.type(WorldType.FLAT)
				.generatorSettings("1;0")
				.generateStructures(false);
		World world = creator.createWorld();
		world.getBlockAt(0, 0, 0)
				.setType(Material.BEDROCK);
				
		C.pMain(player, "Map", "Map generated.");
	}
	
}
