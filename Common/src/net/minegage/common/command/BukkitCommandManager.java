package net.minegage.common.command;

import net.minegage.common.module.PluginModule;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.SpigotConfig;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class BukkitCommandManager
		extends PluginModule {

	public static BukkitCommandManager instance;
	private final Field commandMapField;

	private Set<String> hidden = new HashSet<>();

	public BukkitCommandManager(JavaPlugin plugin) {
		super("Command Manager", plugin);

		Field tempField;
		try {
			tempField = Bukkit.getServer().getClass().getDeclaredField("commandMap");
			tempField.setAccessible(true);
		} catch (NoSuchFieldException ex) {
			logSevere("Unable to initialize command manager; commandMap field not found in CraftServer instance. Commands will not register!");
			tempField = null;
		}

		commandMapField = tempField;
		BukkitCommandManager.instance = this;

		enable();
	}

	public void registerCommand(Command command) {
		if (commandMapField == null) {
			logWarn("Command Manager failed to initialize; can't register command " + command.getClass().getName());
			return;
		}

		try {
			CommandMap map = (CommandMap) commandMapField.get(Bukkit.getServer());
			map.register(command.getName(), command);
		} catch (IllegalAccessException ex) {
			logSevere("Unable to register command \"" + command.getName() + "\"; commandMap field not accessible in CraftServer instance.");
		}
	}

	public void hideCommand(String command) {
		hidden.add(command);
	}

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled()) {
			return;
		}

		String eventMessage = event.getMessage();
		String message = eventMessage.substring(1, eventMessage.length());

		String commandName = message.split(" ", 2)[0].toLowerCase();

		if (hidden.contains(commandName)) {
			event.getPlayer().sendMessage(SpigotConfig.unknownCommandMessage);
			event.setCancelled(true);
			return;
		}

		// Command not found, allow Bukkit to finish command processing
	}
}
