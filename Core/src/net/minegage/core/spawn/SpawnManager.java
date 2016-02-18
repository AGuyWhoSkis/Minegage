package net.minegage.core.spawn;


import net.minegage.common.datafile.DataFile;
import net.minegage.common.datafile.WorldDataLoadEvent;
import net.minegage.common.java.SafeMap;
import net.minegage.common.module.PluginModule;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.io.File;


public class SpawnManager
		extends PluginModule {

	public static SpawnManager instance;

	private SafeMap<World, Location> spawnpoints = new SafeMap<>();
	public boolean overrideSpawns = true;

	public SpawnManager(JavaPlugin plugin) {
		super("Spawn Manager", plugin);

		addCommand(new CommandSetspawn(this));
		addCommand(new CommandSpawn(this));

		for (World world : Bukkit.getWorlds()) {
			File file = new File(world.getWorldFolder(), DataFile.FILE_NAME);
			if (file.exists()) {
				DataFile data = new DataFile(file);
				loadSpawnpoint(world, data);
			}
		}

		instance = this;
	}

	@Override
	protected void onDisable() {
		spawnpoints.clear();
	}

	public Location getSpawnpoint(World world) {
		return spawnpoints.getOrDefault(world, world.getSpawnLocation());
	}

	public void setSpawnpoint(World world, Location location) {
		spawnpoints.put(world, location);
	}

	@EventHandler
	public void loadSpawnpoint(WorldDataLoadEvent event) {
		loadSpawnpoint(event.getWorld(), event.getFile());
	}

	private void loadSpawnpoint(World world, DataFile file) {
		if (file.contains("spawnpoint")) {
			Location location = file.read("spawnpoint").asLocation(world);
			spawnpoints.put(world, location);
		}
	}

	public void removeSpawnpoint(World world) {
		spawnpoints.remove(world);
	}

	@EventHandler
	public void unloadSpawnpoint(WorldUnloadEvent event) {
		removeSpawnpoint(event.getWorld());
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onSpawn(PlayerSpawnLocationEvent event) {
		if (!overrideSpawns) {
			return;
		}

		Location newSpawn = getNewSpawn(event.getSpawnLocation());

		if (newSpawn != null) {
			event.setSpawnLocation(newSpawn);
		}
	}

	// Set the join location to the world spawn by default. Low priority to allow overriding
	@EventHandler (priority = EventPriority.LOWEST)
	public void setJoinLocation(PlayerSpawnLocationEvent event) {
		if (!overrideSpawns) {
			return;
		}

		event.setSpawnLocation(event.getSpawnLocation()
				                       .getWorld()
				                       .getSpawnLocation());
	}

	@EventHandler (priority = EventPriority.HIGH)
	public void onTeleport(PlayerTeleportEvent event) {
		Location newSpawn = getNewSpawn(event.getTo());

		if (newSpawn != null) {
			event.setTo(newSpawn);
		}
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent event) {
		if (!overrideSpawns) {
			return;
		}

		Location newSpawn = getNewSpawn(event.getRespawnLocation());

		if (newSpawn != null) {
			event.setRespawnLocation(newSpawn);
		}
	}

	/**
	 @param targetSpawn The target spawnpoint for the event (PlayerSpawnLocationEvent or PlayerRespawnEvent)

	 @return If targetSpawn matches the spawn location of its world, it will return the log spawnpoint (if it exists).
	 Otherwise returns null.
	 */
	private Location getNewSpawn(Location targetSpawn) {
		if (targetSpawn == null) {
			return null;
		}

		World    targetWorld = targetSpawn.getWorld();
		Location worldSpawn  = targetWorld.getSpawnLocation();

		if (targetSpawn.equals(worldSpawn)) {
			Location realSpawn = spawnpoints.get(targetWorld);
			return realSpawn;
		}

		return null;
	}

}
