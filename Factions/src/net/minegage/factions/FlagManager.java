package net.minegage.factions;

import net.minegage.common.module.PluginModule;
import net.minegage.common.C;
import net.minegage.core.spawn.SpawnCommandEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class FlagManager
		extends PluginModule {

	public FlagManager(JavaPlugin plugin) {
		super("Flag Manager", plugin);
	}

	@EventHandler
	public void disableWeather(WeatherChangeEvent event) {
		if (event.toWeatherState()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void disableSpawnCommand(SpawnCommandEvent event) {
		event.setCancelled(true);
		C.pMain(event.getPlayer(), "Spawn", "You can't use this spawn command in factions");
	}


}
