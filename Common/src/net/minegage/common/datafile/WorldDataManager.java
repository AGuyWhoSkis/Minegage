package net.minegage.common.datafile;

import net.minegage.common.log.L;
import net.minegage.common.module.PluginModule;
import net.minegage.common.util.UtilEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class WorldDataManager
		extends PluginModule {

	public WorldDataManager(JavaPlugin plugin) {
		super("World Data", plugin);


		for (World world : Bukkit.getWorlds()) {
			loadWorld(world);
		}
	}


	@EventHandler(priority = EventPriority.LOWEST)
	public void loadWorld(WorldLoadEvent event) {
		loadWorld(event.getWorld());
	}

	private void loadWorld(World world) {
		File file = new File(world.getWorldFolder(), DataFile.FILE_NAME);
		if (!file.exists()) {
			return;
		}

		DataFile dataFile = new DataFile(file);

		try {
			dataFile.loadFile();

			WorldDataLoadEvent event = new WorldDataLoadEvent(world, dataFile);
			UtilEvent.call(event);

		} catch (IOException e) {
			L.error(e, "Unable to load world data");
		}
	}

}
