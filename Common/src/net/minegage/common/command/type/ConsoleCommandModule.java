package net.minegage.common.command.type;

import net.minegage.common.module.PluginModule;

public abstract class ConsoleCommandModule<T extends PluginModule>
		extends ConsoleCommand {

	protected T plugin;

	public ConsoleCommandModule(T plugin, String name, String... aliases) {
		super(name, aliases);

		this.plugin = plugin;
	}

}
