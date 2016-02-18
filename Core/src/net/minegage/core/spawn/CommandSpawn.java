package net.minegage.core.spawn;


import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilEvent;
import net.minegage.core.command.CommandModule;
import net.minegage.core.rank.Rank;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandSpawn
		extends CommandModule<SpawnManager> {
		
	public CommandSpawn(SpawnManager spawnManager) {
		super(spawnManager, Rank.DEFAULT, "sp");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		World world = player.getWorld();
		Location spawnpoint = getPlugin().getSpawnpoint(world);
		
		SpawnCommandEvent event = new SpawnCommandEvent(player);
		UtilEvent.call(event);
		
		if (!event.isCancelled()) {
			player.teleport(spawnpoint);
		}
	}
	
}
