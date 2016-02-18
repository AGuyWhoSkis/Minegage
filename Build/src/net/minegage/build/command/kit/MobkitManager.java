package net.minegage.build.command.kit;


import net.minegage.common.module.PluginModule;
import net.minegage.minigame.kit.KitManager;
import org.apache.commons.io.FileUtils;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class MobkitManager
		extends PluginModule {
		
	public MobkitManager(JavaPlugin plugin) {
		super("Mobkit Manager", plugin);
		
		addCommand(new CommandKit(this));
	}
	
	public File getFile(World world) {
		return new File(world.getWorldFolder(), KitManager.FILE_NAME);
	}
	
	public List<String> readLines(World world) throws IOException {
		File file = getFile(world);
		
		// Create the file if it doesn't exist
		file.createNewFile();
		
		return FileUtils.readLines(file);
	}
	
	public void writeLines(World world, List<String> lines) throws IOException {
		File file = getFile(world);
		FileUtils.writeLines(file, lines);
	}
	
	
	
}
