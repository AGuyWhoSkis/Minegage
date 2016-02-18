package net.minegage.core.spawn;


import net.minegage.common.command.Flags;
import net.minegage.common.datafile.DataFile;
import net.minegage.common.util.UtilPos;
import net.minegage.core.command.CommandModule;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class CommandSetspawn
		extends CommandModule<SpawnManager> {

	public CommandSetspawn(SpawnManager spawnManager) {
		super(spawnManager, Rank.ADMIN, new Rank[] {Rank.BUILDER}, "setspawnpoint", "setsp");
	}

	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		Location location = player.getLocation();
		World    world    = location.getWorld();

		File file = new File(world.getWorldFolder(), DataFile.FILE_NAME);

		if (args.contains("remove")) {
			if (!file.exists()) {
				C.pMain(player, "Spawn", "The spawnpoint isn't set");
			} else {
				DataFile dataFile = new DataFile(file);
				try {
					dataFile.loadFile();

					if (!dataFile.contains("spawnpoint")) {
						C.pMain(player, "Spawn", "The spawnpoint isn't set");
						return;
					}

					dataFile.delete("spawnpoint");
					dataFile.saveFile();

					C.pMain(player, "Spawn", "Spawnpoint removed");
					getPlugin().removeSpawnpoint(world);
				} catch (IOException e) {
					C.pErr(e, player, "Unable to set spawnpoint");
				}
			}

		} else {
			try {
				file.createNewFile();

				DataFile data = new DataFile(file);
				data.loadFile();

				data.write("spawnpoint", UtilPos.serializeLocation(location));
				getPlugin().setSpawnpoint(world, location);

				data.saveFile();

				C.pMain(player, "Spawn", "Spawnpoint set");

			} catch (IOException ex) {
				C.pErr(ex, player, "Unable to set spawnpoint");
			}
		}

	}

}
