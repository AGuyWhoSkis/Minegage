package net.minegage.core.mob.command.manager;


import net.minegage.common.command.Flags;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandUnattach
		extends RankedCommand {
		
	private CommandMobManager<?, ?> manager;
	
	public CommandUnattach(CommandMobManager<?, ?> manager) {
		super(Rank.ADMIN, "unattach", "delete", "remove");
		
		this.manager = manager;
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		manager.bufferUnattach(player);
		C.pMain(player, manager.getName(), "Click an portal to unattach it");
	}
	
}
