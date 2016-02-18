package net.minegage.common.command.type;

import net.minegage.common.module.PluginModule;

public abstract class QuickCommandModule<T extends PluginModule>
		extends ConsoleCommand {

	protected T plugin;

	public QuickCommandModule(T plugin, String name, String... aliases) {
		super(name, aliases);

		this.plugin = plugin;
	}

}
