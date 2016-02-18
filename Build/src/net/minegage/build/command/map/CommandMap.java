package net.minegage.build.command.map;


import net.minegage.build.BuildManager;
import net.minegage.common.command.Flags;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandMap
		extends RankedCommand {
		
	public CommandMap(BuildManager manager) {
		super(Rank.BUILDER, "map");
		
		addSubCommand(new CommandMapSetup(manager));
		addSubCommand(new CommandMapGenerate());
		addSubCommand(new CommandMapName());
		addSubCommand(new CommandMapAuthor());
		addSubCommand(new CommandMapBound());
		
		addSubCommand(new CommandMapAdd());
		addSubCommand(new CommandMapGet());
		addSubCommand(new CommandMapRemove());
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		C.pHelp(player, "map generate <name>");
		C.pHelp(player, "map setup: Sets gamerules, sets time, sets weather, clears mobs");
		C.pHelp(player, "map name <name>");
		C.pHelp(player, "map author <author>");
		C.pHelp(player, "map bound: sets a boundary of the map (face towards middle)");
	}
	
}
