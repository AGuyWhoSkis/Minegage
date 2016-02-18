package net.minegage.core.command.world;


import net.minegage.common.command.Flags;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandWorld
		extends RankedCommand {
		
	public CommandWorld() {
		super(Rank.BUILDER, "world", "wo", "mgw");
		
		addSubCommand(new CommandWorldCreate());
		addSubCommand(new CommandWorldDelete());
		addSubCommand(new CommandWorldTeleport());
		addSubCommand(new CommandWorldUnload());
		addSubCommand(new CommandWorldList());
		addSubCommand(new CommandWorldPurge());
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		C.pHelp(player, "World", "world list: list all worlds");
		C.pHelp(player, "World", "world change/ch <world>");
		C.pHelp(player, "World", "world create <name> [-seed (seed)/-type (superflat,etc)/-set(settings)]");
		C.pHelp(player, "World", "world unload <world> [-force/-save]");
		C.pHelp(player, "World", "world purge: save and unload unpopulated worlds");
	}
	
}
