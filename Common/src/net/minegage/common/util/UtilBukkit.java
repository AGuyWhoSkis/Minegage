package net.minegage.common.util;


import net.minegage.common.log.L;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;


public class UtilBukkit {
	
	private static Field craftServerConfig;
	
	/**
	 * @return The FileConfiguration object stored in the CraftServer class
	 */
	public static FileConfiguration getBukkitConfig() {
		if (craftServerConfig == null) {
			try {
				craftServerConfig = CraftServer.class.getDeclaredField("configuration");
				craftServerConfig.setAccessible(true);
			} catch (NoSuchFieldException | SecurityException ex) {
				ex.printStackTrace();
			}
		}
		
		try {
			CraftServer server = (CraftServer) Bukkit.getServer();
			return (FileConfiguration) craftServerConfig.get(server);
		} catch (IllegalArgumentException | IllegalAccessException ex) {
			throw new Error("Unresolved compilation problem; configuration field not found in " + CraftServer.class.getName());
		}
	}
	
	public static void saveBukkitConfig() {
		try {
			getBukkitConfig().save(getBukkitConfigFile());
		} catch (IOException ex) {
			L.severe("Unable to save Bukkit config; an IOException occurred");
			ex.printStackTrace();
		}
	}
	
	public static File getBukkitConfigFile() {
		return new File(Bukkit.getWorldContainer(), "bukkit.yml");
	}
	
}
