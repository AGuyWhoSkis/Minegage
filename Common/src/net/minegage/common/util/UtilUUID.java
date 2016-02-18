package net.minegage.common.util;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.UUID;

public class UtilUUID {
	
	public static Entity getEntity(UUID uid, Server server) {
		for (World world : server.getWorlds()) {
			Entity entity = getEntity(uid, world);
			if (entity != null) {
				return entity;
			}
		}
		return null;
	}
	
	public static Entity getEntity(UUID uid, World world) {
		for (Entity entity : world.getEntities()) {
			if (entity.getUniqueId().equals(uid)) {
				return entity;
			}
		}
		return null;
	}
	
}
