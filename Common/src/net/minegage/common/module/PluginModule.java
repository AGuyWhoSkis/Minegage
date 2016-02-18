package net.minegage.common.module;


import net.minegage.common.command.BukkitCommandManager;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class PluginModule
		extends LazyScheduler
		implements Listener {

	private boolean enabled = false;

	protected String name;

	public PluginModule(String name, JavaPlugin plugin) {
		super(plugin);

		this.name = name;

		enable();
	}

	public PluginModule(String name, PluginModule module) {
		this(name, module.getPlugin());
	}

	public final void enable() {
		if (enabled) {
			return;
		}

		logInfo("Enabling");

		enabled = true;
		onEnable();
		registerEvents(this);
	}

	public final void disable() {
		if (!enabled) {
			return;
		}

		logInfo("Disabling");

		enabled = false;
		onDisable();
		unregisterEvents(this);
		cancelAllTasks();
	}

	@Deprecated
	protected void onEnable() {
		// Optional override
	}

	@Deprecated
	protected void onDisable() {
		// Optional override
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void d(Object m) {
		getServer().broadcastMessage(m.toString());
	}

	public void logInfo(Object info) {
		log(Level.INFO, info);
	}

	public void logWarn(Object warning) {
		log(Level.WARNING, warning);
	}

	public void logSevere(Object severe) {
		log(Level.SEVERE, severe);
	}

	public void log(Level level, Object message) {
		getLogger().log(level, name + " - " + message);
	}

	public Server getServer() {
		return plugin.getServer();
	}

	public void registerEvents(Listener listener) {
		getPluginManager().registerEvents(listener, plugin);
	}

	public void unregisterEvents(Listener listener) {
		HandlerList.unregisterAll(listener);
	}

	public void addCommand(Command command) {
		BukkitCommandManager.instance.registerCommand(command);
	}

	public PluginManager getPluginManager() {
		return getServer().getPluginManager();
	}

	public String getName() {
		return name;
	}

	public JavaPlugin getPlugin() {
		return plugin;
	}

	public Logger getLogger() {
		return getPlugin().getLogger();
	}

}
