package net.minegage.common.util;


import net.minecraft.server.v1_8_R3.ExceptionWorldConflict;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.RegionFile;
import net.minecraft.server.v1_8_R3.RegionFileCache;
import net.minecraft.server.v1_8_R3.WorldServer;
import net.minegage.common.C;
import net.minegage.common.log.L;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.stream.Collectors;


public class UtilWorld {


	public static List<String> getWorldNames() {
		return Arrays.stream(Bukkit.getServer()
				                     .getWorldContainer()
				                     .listFiles())
				.filter(file -> exists(file.getName()))
				.map(file -> file.getName())
				.collect(Collectors.toList());
	}

	public static List<String> getUnloadedWorlds() {
		return getWorldNames().stream()
				.filter(name -> !isLoaded(name))
				.collect(Collectors.toList());
	}

	public static WorldCreator setGenerator(String worldName, String generatorName, String generatorId) {
		ChunkGenerator generator = WorldCreator.getGeneratorForName(worldName, generatorName, null);

		if (generator == null) {
			throw new IllegalArgumentException("Generator \"" + generator + "\" does not exist");
		}

		WorldCreator creator = WorldCreator.name(worldName)
				.generator(generator);
		FileConfiguration bukkitConfig = UtilBukkit.getBukkitConfig();

		String path            = "worlds." + worldName + ".generator";
		String generatorString = generatorName;
		if (generatorId != null) {
			generatorString += ":" + generatorId;
		}

		bukkitConfig.set(path, generatorString);

		UtilBukkit.saveBukkitConfig();

		return creator;
	}

	public static boolean delete(String worldName)
			throws IOException {
		World world = Bukkit.getWorld(worldName);
		if (world != null) {
			UtilWorld.unload(world, false);
		}

		File file = new File(Bukkit.getWorldContainer(), worldName);
		FileUtils.forceDelete(file);

		FileConfiguration bukkitConfig = UtilBukkit.getBukkitConfig();
		String            path         = "worlds." + worldName + ".generator";

		if (bukkitConfig.contains(path)) {
			bukkitConfig.set(path, null);
			UtilBukkit.saveBukkitConfig();
		}

		return true;
	}

	public static World load(String worldName) {
		return load(WorldCreator.name(worldName));
	}

	public static World load(String worldName, String generatorName) {
		UtilWorld.setGenerator(worldName, generatorName, null);
		WorldCreator creator = WorldCreator.name(worldName);
		return load(creator);
	}

	public static World load(WorldCreator creator) {
		String worldName = creator.name();

		L.info("Loading world \"" + worldName + "\"...");

		World world = creator.createWorld();
		if (world == null) {
			// World failed to load, possibly because of duplicate uid.dat
			// files. Deleting this should fix.

			File uidFile = new File(new File(Bukkit.getWorldContainer(), worldName), "uid.dat");
			uidFile.delete();

			world = creator.createWorld();

			// If the world is still null, there is a problem
			if (world == null) {
				throw new NullPointerException("World \"" + worldName + "\" failed to load");
			}
		}

		L.info("Loaded world \"" + worldName + "\"");
		return world;
	}

	public static boolean unload(World world, boolean save) {
		if (isMainWorld(world)) {
			throw new IllegalArgumentException("Cannot unload main world; force unload instead");
		}

		Location spawn = getFixedSpawnpoint(getMainWorld());
		for (Player player : world.getPlayers()) {
			player.teleport(spawn);
			C.pMain(player, "World", "The world you were in was unloaded");
		}

		WorldServer nmsWorld = ((CraftWorld) world).getHandle();
		nmsWorld.keepSpawnInMemory = false;

		String  worldName = world.getName();
		boolean unloaded  = Bukkit.unloadWorld(world, save);

		if (unloaded) {
			L.info("Unloaded world \"" + worldName + "\"");
		} else {
			L.warn("Failed to unload world \"" + worldName + "\"");
		}

		return unloaded;
	}

	private static Field worlds;
	private static Field console;


	// Code from org.bukkit.craftbukkit.v1_8_R3.CraftServer.unloadWorld(World
	// world, boolean save)
	@SuppressWarnings ("unchecked")
	public static boolean forceUnload(World world, boolean save) {

		WorldUnloadEvent unloadEvent = new WorldUnloadEvent(world);
		UtilEvent.call(unloadEvent);

		if (unloadEvent.isCancelled()) {
			return false;
		}

		boolean main = UtilWorld.isMainWorld(world);
		for (Player player : world.getPlayers()) {
			if (main) {
				player.kickPlayer("World unloading");
			} else {
				C.pMain(player, "World", "The world you were in was unloaded");
				player.teleport(getMainWorld().getSpawnLocation());
			}
		}

		if (worlds == null) {
			worlds = UtilReflect.getField(CraftServer.class, "worlds");
			console = UtilReflect.getField(CraftServer.class, "console");
		}

		WorldServer handle = ((CraftWorld) world).getHandle();

		try {
			handle.save(save, null);
			handle.saveLevel();
		} catch (ExceptionWorldConflict ex) {
			L.error(ex, "Unable to save world \"" + world.getName() + "\"");
		}

		CraftServer server = (CraftServer) Bukkit.getServer();

		Map<String, net.minecraft.server.v1_8_R3.World> map;

		try {
			map = (Map<String, net.minecraft.server.v1_8_R3.World>) worlds.get(server);
			map.remove(world.getName()
					           .toLowerCase());

			MinecraftServer nmsServer = (MinecraftServer) console.get(server);
			nmsServer.worlds.remove(nmsServer.worlds.indexOf(handle));

			File parentFolder = world.getWorldFolder()
					.getAbsoluteFile();

			synchronized (RegionFileCache.class) {
				Iterator<Map.Entry<File, RegionFile>> i = RegionFileCache.a.entrySet()
						.iterator();

				File child;
				while (i.hasNext()) {
					Map.Entry<File, RegionFile> entry = i.next();
					child = entry.getKey()
							.getAbsoluteFile();

					if (child.equals(parentFolder)) {
						i.remove();
						try {
							entry.getValue()
									.c();
						} catch (IOException ex) {
							L.getLogger()
									.log(Level.SEVERE, null, ex);
						}
					}

					child = child.getParentFile();

					if (child == null) {
						break;
					}
				}
			}

		} catch (IllegalArgumentException | IllegalAccessException ex) {
			L.error(ex, "Unable to forcibly unload world \"" + world.getName() + "\"");
			return false;
		}

		return true;
	}

	public static boolean isLoaded(String worldName) {
		return Bukkit.getWorld(worldName) != null;
	}

	public static boolean exists(String worldName) {
		File worldFile = new File(Bukkit.getWorldContainer(), worldName);


		if (!worldFile.isDirectory()) {
			return false;
		}

		for (File file : worldFile.listFiles()) {
			if (isChunkFile(file)) {
				return true;
			}
		}

		return false;
	}

	private static boolean isChunkFile(File file) {
		// "region" holds overworld region files, "DIM-1" holds nether region files, "DIM1" holds The End region files
		return file.isDirectory() && (file.getName().equals("region") || file.getName().equals("DIM-1") || file.getName().equals("DIM1"));
	}

	public static Location getFixedSpawnpoint(World world) {
		ChunkGenerator generator = world.getGenerator();
		if (generator == null) {
			generator = ((CraftWorld) world).getHandle().generator;
		}

		if (generator == null) {
			return world.getSpawnLocation();
		} else {
			return generator.getFixedSpawnLocation(world, new Random());
		}
	}

	public static World getMainWorld() {
		return Bukkit.getWorld("world");
	}

	public static boolean isMainWorld(String worldName) {
		return getMainWorld().getName()
				.equals(worldName);
	}

	public static boolean isMainWorld(World world) {
		return getMainWorld().equals(world);
	}

	public static void applySettings(World world) {
		world.setThundering(false);
		world.setStorm(false);
		world.setWeatherDuration(Integer.MAX_VALUE);

		world.setGameRuleValue("doDaylightCycle", "false");
		world.setGameRuleValue("doMobSpawning", "false");

		world.setTime(6000L);

		for (LivingEntity entity : world.getLivingEntities()) {
			EntityType type = entity.getType();

			if (type == EntityType.PLAYER) {
				continue;
			}

			// Keep entities which have armor stands as names
			if (entity.getPassenger() == null && entity.getVehicle() == null) {
				entity.remove();
			}
		}
	}

	public static File getWorldFile(String world) {
		return new File(Bukkit.getWorldContainer(), world);
	}
}
