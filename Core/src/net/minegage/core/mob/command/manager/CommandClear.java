package net.minegage.core.mob.command.manager;


import net.minegage.common.command.Flags;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandClear
		extends RankedCommand {
		
	private CommandMobManager<?, ?> manager;
	
	public CommandClear(CommandMobManager<?, ?> manager) {
		super(Rank.ADMIN, "clear");
		
		this.manager = manager;
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		manager.manager.clearMobs(player.getWorld());
		C.pMain(player, manager.getName(), "All entities cleared.");
	}
	
}
