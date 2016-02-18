package net.minegage.build.command.map;


import net.minegage.build.BuildManager;
import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilWorld;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandMapSetup
		extends RankedCommand {
		
	private BuildManager manager;
	
	public CommandMapSetup(BuildManager manager) {
		super(Rank.ADMIN, "setup", "s");
		
		this.manager = manager;
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		World world = player.getWorld();
		UtilWorld.applySettings(world);
		
		
		C.pMain(player, "Map", "Setup complete");
	}
	
}
