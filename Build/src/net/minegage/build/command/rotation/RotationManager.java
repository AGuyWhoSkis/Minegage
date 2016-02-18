package net.minegage.build.command.rotation;


import com.google.common.collect.SetMultimap;
import net.minegage.common.datafile.DataFile;
import net.minegage.common.module.PluginModule;
import net.minegage.common.server.ServerManager;
import net.minegage.common.util.UtilWorld;
import net.minegage.common.util.UtilZip;
import net.minegage.common.C;
import net.minegage.minigame.game.GameType;
import net.minegage.minigame.map.MapManager;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Set;


public class RotationManager
		extends PluginModule {
		
	public static final String MAPS_PATH;
	
	static {
		String sep = File.separator;
		MAPS_PATH = sep + "assets" + sep + "maps";
	}
	
	public static File getMapsDir() {
		return new File(MAPS_PATH);
	}
	
	private MapManager mapManager;
	
	public MapManager getMapManager() {
		return mapManager;
	}
	
	public RotationManager(JavaPlugin plugin) {
		super("Rotation Manager", plugin);
		mapManager = new MapManager(plugin);
		mapManager.loadMapRotation();
		
		addCommand(new CommandRotation(this));
	}
	
	private boolean checkPermissions(File file, Player player) {
		boolean okay = file.canRead() && file.canWrite();
		if (file.exists() && okay) {
			return true;
		}
		
		C.pMain(player, "Rotation", "There is a problem with a file. Checking permissions...");
		
		if (!okay) {
			C.pMain(player, "Perm", "Permissions for file " + file.getAbsolutePath() + " are not set up correctly: Read " + file.canRead()
			                        + ", write " + file.canWrite());
		} else {
			C.pMain(player, "Perm", "File permissions OK, please reload the map rotation and try again");
		}
		
		return false;
	}
	
	public void addMap(GameType type, String map, Player player) {
		if (UtilWorld.isMainWorld(map)) {
			C.pMain(player, "Rotation", "You can't add the main world to rotation");
			return;
		}
		
		if (!UtilWorld.exists(map)) {
			C.pMain(player, "Rotation", "Map \"" + map + "\" does not exist");
			return;
		}
		
		MapManager mapManager = getMapManager();
		
		SetMultimap<GameType, String> rotation = mapManager.getMapRotation();
		Set<String> maps = rotation.get(type);
		if (maps.contains(map)) {
			C.pMain(player, "Rotation", C.fElem(map) + " is already in " + C.fElem(map) + " rotation");
			return;
		}
		
		File worldDir = new File(Bukkit.getWorldContainer(), map);
		
		File mapDataCheck = new File(worldDir, DataFile.FILE_NAME);
		if (!mapDataCheck.exists()) {
			C.pWarn(player, "Rotation", "No map data is specified");
			return;
		}
		
		World world = Bukkit.getWorld(map);
		if (world != null) {
			UtilWorld.forceUnload(world, true);
		}
		
		for (File child : worldDir.listFiles()) {
			String name = child.getName();
			if (name.equals("playerdata") || name.equals("session.lock") || name.equals("uid.dat") || name.equals("data") || name.equals(
					"level.dat_old")) {
				FileUtils.deleteQuietly(child);
			}
		}
		
		File zipTo = ServerManager.getMapZip(type.getName(), map);
		
		try {
			UtilZip.compress(zipTo, worldDir.listFiles());
			maps.add(map);
			
			C.pMain(player, "Rotation", C.fElem(map) + " added to " + C.fElem(type.getName()) + " rotation");
		} catch (IOException ex) {
			C.pErr(ex, player, "Unable to add " + C.fElem(map) + " to " + C.fElem(type.getName()) + " rotation");
		}
	}
	
	public void removeMap(GameType type, String map, Player player) {
		MapManager mapManager = getMapManager();
		
		SetMultimap<GameType, String> rotation = mapManager.getMapRotation();
		Set<String> maps = rotation.get(type);
		
		String game = type.getName();
		if (!maps.contains(map)) {
			C.pMain(player, "Rotation", C.fElem(game) + " rotation doesn't contain " + C.fElem(map));
			return;
		}
		
		File file = ServerManager.getMapZip(type.getName(), map);
		if (!checkPermissions(file, player)) {
			return;
		}
		
		boolean success = file.delete();
		if (success) {
			maps.remove(map);
			C.pMain(player, "Rotation", "Removed " + C.fElem(map) + " from " + C.fElem(game) + " rotation");
		} else {
			C.pMain(player, "Rotation", "Unable to remove " + C.fElem(map) + " from " + C.fElem(game) + " rotation");
		}
	}
	
	public void getMap(GameType type, String map, Player player) {
		File worldDir = new File(Bukkit.getWorldContainer(), map);
		if (worldDir.exists()) {
			C.pMain(player, "Rotation", "World " + C.fElem(map) + " already exists!");
			return;
		}
		
		File mapZip = ServerManager.getMapZip(type.getName(), map);
		
		if (!mapZip.exists()) {
			C.pMain(player, "Rotation", C.fElem(map) + " is not in " + C.fElem(type.getName()) + " rotation");
			return;
		}
		
		try {
			UtilZip.extract(mapZip, worldDir);
			C.pMain(player, "Rotation", "Map fetched");
		} catch (IOException ex) {
			C.pErr(ex, player, "Unable to fetch map");
		}
	}
	
	
}
