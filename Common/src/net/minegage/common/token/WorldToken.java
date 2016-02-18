package net.minegage.common.token;


import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.UUID;


public class WorldToken {
	
	public UUID worldUID;
	public String worldName;
	
	public WorldToken(World world) {
		this.worldUID = world.getUID();
		this.worldName = world.getName();
	}
	
	public World getWorld() {
		return Bukkit.getWorld(worldUID);
	}
	
}
