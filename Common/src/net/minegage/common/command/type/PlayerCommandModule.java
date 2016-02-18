package net.minegage.common.command.type;

import net.minegage.common.module.PluginModule;

public abstract class PlayerCommandModule<T extends PluginModule>
		extends PlayerCommand {

	protected T plugin;

	public PlayerCommandModule(T plugin, String name, String... aliases) {
		super(name, aliases);

		this.plugin = plugin;
	}

}