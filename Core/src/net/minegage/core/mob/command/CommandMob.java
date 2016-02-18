package net.minegage.core.mob.command;

import net.minegage.common.command.Flags;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandMob
		extends RankedCommand {
	
	public CommandMob() {
		super(Rank.ADMIN, "mob", "portal");
		
		addSubCommand(new CommandMobSpawn());
		addSubCommand(new CommandMobClear());
		addSubCommand(new CommandMobList());
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		C.pHelp(player, "mob spawn", "spawns a mob");
		C.pHelp(player, "mob clear", "clears mobs");
		C.pHelp(player, "mob list", "lists mob types");
	}
	
}
