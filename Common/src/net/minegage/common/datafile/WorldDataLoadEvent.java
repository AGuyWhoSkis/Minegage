package net.minegage.common.datafile;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WorldDataLoadEvent
		extends Event {

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	private World world;
	private DataFile file;

	public WorldDataLoadEvent(World world, DataFile file) {
		this.world = world;
		this.file = file;
	}

	public World getWorld() {
		return world;
	}

	public DataFile getFile() {
		return file;
	}
}
