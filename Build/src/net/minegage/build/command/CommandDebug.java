package net.minegage.build.command;


import net.minegage.build.BuildManager;
import net.minegage.common.command.Flags;
import net.minegage.common.log.L;
import net.minegage.common.server.ServerManager;
import net.minegage.common.util.UtilZip;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import net.minegage.minigame.game.GameType;
import net.minegage.minigame.map.MapManager;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CommandDebug
		extends RankedCommand {

	private BuildManager buildManager;

	public CommandDebug(BuildManager buildManager) {
		super(Rank.ADMIN, "bd");
		this.buildManager = buildManager;
	}

	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		int i = Integer.parseInt(args.get(0));

		if (i == 0) {
			L.d(Bukkit.getWorldContainer()
					    .getAbsolutePath());
		} else if (i == 1) {
			MapManager manager = buildManager.getRotationManager().getMapManager();

			for (GameType gameType : manager.getMapRotation().keySet()) {
				for (String map : manager.getMapRotation().get(gameType)) {

					File zipFile = ServerManager.getMapZip(gameType.getName(), map);

					File unzipped = new File(ServerManager.getMapsDir(gameType.getName()), map + " temp");
					unzipped.mkdirs();

					try {
						UtilZip.extract(zipFile, unzipped);

						File mergedFile = new File(unzipped, "mapdata.dat");

						if (!mergedFile.exists()) {
							File mapDataFile  = new File(unzipped, "map data.txt");
							File gameDataFile = new File(unzipped, "game data.txt");

							List<String> lines = new ArrayList<>();

							if (mapDataFile.exists()) {
								lines.addAll(FileUtils.readLines(mapDataFile));
							}

							if (gameDataFile.exists()) {
								lines.addAll(FileUtils.readLines(gameDataFile));
							}

							mergedFile.createNewFile();

							FileUtils.writeLines(mergedFile, lines);

							mapDataFile.delete();
							gameDataFile.delete();
						} else {

							List<String> newLines = new ArrayList<>();
							List<String> mapDataLines = FileUtils.readLines(mergedFile);
							for (String line : mapDataLines) {
								try {
									if (line.length() > 0 && !line.contains(":")) {
										line = "spawnpoint:" + line;
									}
								} catch (IllegalArgumentException ex) {
									// fail silent
								}

								newLines.add(line);
							}

							FileUtils.writeLines(mergedFile, newLines);


							player.sendMessage("Fixed spawn of " + gameType.getName() + " " + map);
						}

						boolean deleted = zipFile.delete();

						if (!deleted) {
							player.sendMessage("failed to delete " + gameType.getName() + " " + map);
							continue;
						}

						File newZip = ServerManager.getMapZip(gameType.getName(), map);
						newZip.createNewFile();

						UtilZip.compress(newZip, unzipped.listFiles());


						player.sendMessage("Fixed " + gameType.getName() + " " + map);

					} catch (IOException e) {
						C.pErr(e, player, "Unable to fix maps");
						return;
					} finally {
						try {
							FileUtils.deleteDirectory(unzipped);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}
			}


		}

	}

}
