package net.minegage.common.config;


import net.minegage.common.log.L;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;


public abstract class Config {
	
	protected File file;
	protected FileConfiguration config;
	
	protected JavaPlugin plugin;
	
	public Config(JavaPlugin plugin, File file) {
		this.plugin = plugin;
		this.file = file;
		
		file.getParentFile().mkdirs();
		
		try {
			file.createNewFile();
			reload();
		} catch (IOException ex) {
			L.severe("Unable to load config " + file.getPath() + "; An IOException occurred.");
			ex.printStackTrace();
		}
	}
	
	public abstract void addDefaults(ConfigDefaults configDefaults);
	
	public abstract void loadValues(FileConfiguration config);
	
	private void defaults() {
		ConfigDefaults configDefaults = new ConfigDefaults(getFile());
		addDefaults(configDefaults);
		configDefaults.saveIfChanged();
	}
	
	public void reload() {
		defaults();
		
		config = YamlConfiguration.loadConfiguration(file);
		loadValues(getConfig());
	}
	
	public File getFile() {
		return file;
	}
	
	public FileConfiguration getConfig() {
		return config;
	}
	
	public void save() throws IOException {
		config.save(file);
	}
	
}
