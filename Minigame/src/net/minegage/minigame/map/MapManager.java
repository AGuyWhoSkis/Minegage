package net.minegage.minigame.map;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import net.minegage.common.datafile.DataFile;
import net.minegage.common.datafile.WorldDataLoadEvent;
import net.minegage.common.java.SafeMap;
import net.minegage.common.log.L;
import net.minegage.common.module.PluginModule;
import net.minegage.common.server.ServerManager;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilString;
import net.minegage.minigame.GameManager;
import net.minegage.minigame.game.GameType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Map.Entry;


public class MapManager
		extends PluginModule {

	private SetMultimap<GameType, String> mapRotation = HashMultimap.create();

	private SafeMap<World, MapToken> data = new SafeMap<>();
	private GameManager gameManager;

	public MapManager(JavaPlugin plugin) {
		super("Map Manager", plugin);
	}

	@Override
	protected void onEnable() {
		runSyncTimer(20L, 20L, new BukkitRunnable() {
			@Override
			public void run() {
				checkBounds();
			}
		});
	}

	@Override
	protected void onDisable() {
		data.clear();
	}

	public void loadMapRotation() {
		mapRotation.clear();

		L.info("Loading map rotation...");

		File mapsDir = ServerManager.getMapsDir();
		mapsDir.mkdirs();

		for (File gameType : mapsDir.listFiles()) {
			if (!gameType.isDirectory()) {
				continue;
			}

			String   gameName = gameType.getName();
			GameType type     = UtilJava.parseEnum(GameType.class, gameName);

			if (type == null) {
				L.warn("Skipping directory \"" + gameName + "\"; not a valid game type");
				continue;
			}

			int count = 0;
			for (File zip : gameType.listFiles()) {
				String name = zip.getName();

				if (!name.endsWith(".zip")) {
					continue;
				}

				name = UtilString.removeLast(name, ".zip");
				if (mapRotation.put(type, name)) {
					count++;
				}
			}

			if (count == 0) {
				L.warn("No maps in rotation");
			} else {
				L.info("Found " + count + " map(s) for " + gameName);
			}
		}
	}

	public SetMultimap<GameType, String> getMapRotation() {
		return mapRotation;
	}

	public void checkBounds() {
		for (Entry<World, MapToken> entry : data.entrySet()) {

			World   world = entry.getKey();
			MapToken map   = entry.getValue();

			for (Player player : world.getPlayers()) {
				Location loc = player.getLocation();

				double x = loc.getX();
				double y = loc.getY();
				double z = loc.getZ();

				if (x > map.maxX || x < map.minX || y < map.minY || y > map.maxY || z > map.maxZ || z < map.minZ) {
					EventPlayerOutOfBounds event = new EventPlayerOutOfBounds(player);
					UtilEvent.call(event);
				}
			}

		}
	}

	@EventHandler
	public void unloadMap(WorldUnloadEvent event) {
		removeData(event.getWorld());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void loadMapData(WorldDataLoadEvent event) {
		DataFile file = event.getFile();

		MapToken token = new MapToken();

		token.name = file.read("name").defaults("Unknown")
				.asString();
		token.author = file.read("author").defaults("Unknown")
				.asString();

		try {
			token.minX = file.read("minx").defaults(-1000)
					.asInt();
			token.maxX = file.read("maxx").defaults(1000)
					.asInt();

			token.minZ = file.read("minz").defaults(-1000)
					.asInt();
			token.maxZ = file.read("maxz").defaults(1000)
					.asInt();

			token.minY = file.read("miny").defaults(0)
					.asInt();
			token.maxY = file.read("maxy").defaults(255)
					.asInt();

		} catch (Exception ex) {
			L.warn("Unable to parse all bounds; Bad map data format in map data file of world " + event.getWorld().getName());
		}

		data.put(event.getWorld(), token);
	}

	public void removeData(World world) {
		data.remove(world);
	}

	public MapToken getData(World world) {
		return data.get(world);
	}

	public GameManager getGameManager() {
		return gameManager;
	}

}
