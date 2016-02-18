package net.minegage.common.config;


import net.minegage.common.log.L;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ConfigDefaults {
	
	private File file;
	private FileConfiguration config;
	
	private Map<String, Object> defaults = new HashMap<>();
	
	public ConfigDefaults(File file) {
		this.config = YamlConfiguration.loadConfiguration(file);
		this.file = file;
	}
	
	public void addDefault(String path, Object defaultObject) {
		if (!config.contains(path)) {
			defaults.put(path, defaultObject);
		}
	}
	
	public boolean saveIfChanged() {
		if (defaults.size() == 0) {
			return true;
		}
		
		for (String path : defaults.keySet()) {
			config.set(path, defaults.get(path));
		}
		
		try {
			config.save(file);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			L.warn("Unable to save changes made to config " + file + "; an IOException occurred.");
			return false;
		}
	}
	
	
	
}
