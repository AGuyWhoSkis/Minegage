package net.minegage.build;


import net.minegage.build.command.CommandDebug;
import net.minegage.build.command.kit.MobkitManager;
import net.minegage.build.command.map.CommandMap;
import net.minegage.build.command.rotation.RotationManager;
import net.minegage.build.mob.builders.KitBuilder;
import net.minegage.build.mob.builders.NPCBuilder;
import net.minegage.build.mob.builders.PortalBuilder;
import net.minegage.common.command.commands.CommandData;
import net.minegage.common.module.PluginModule;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class BuildManager
		extends PluginModule {
		
	private RotationManager rotationManager;
	private MobkitManager mobkitManager;
	
	private NPCBuilder npcBuilder;
	private KitBuilder kitBuilder;
	private PortalBuilder portalBuilder;
	
	public BuildManager(JavaPlugin plugin) {
		super("Build Manager", plugin);
		
		Bukkit.setDefaultGameMode(GameMode.CREATIVE);
		this.rotationManager = new RotationManager(plugin);
		
		addCommand(new CommandData());
		addCommand(new CommandMap(this));
		addCommand(new CommandDebug(this));
		
		this.npcBuilder = new NPCBuilder(plugin);
		this.kitBuilder = new KitBuilder(plugin);
		this.portalBuilder = new PortalBuilder(plugin);
	}
	
	public RotationManager getRotationManager() {
		return rotationManager;
	}
	
	public MobkitManager getMobkitManager() {
		return mobkitManager;
	}
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		if (event.toWeatherState()) {
			event.setCancelled(true);
		}
	}
	
	public NPCBuilder getNpcBuilder() {
		return npcBuilder;
	}
	
	public KitBuilder getKitBuilder() {
		return kitBuilder;
	}
	
	public PortalBuilder getPortalBuilder() {
		return portalBuilder;
	}
	
}
