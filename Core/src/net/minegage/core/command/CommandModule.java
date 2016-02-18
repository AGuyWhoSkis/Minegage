package net.minegage.core.command;

import net.minegage.common.module.PluginModule;
import net.minegage.core.rank.Rank;

public abstract class CommandModule <T extends PluginModule>
		extends RankedCommand {
	
	protected T plugin;
	
	public CommandModule(T plugin, Rank minRank, String name, String... names) {
		super(minRank, name, names);

		this.plugin = plugin;
	}

	public CommandModule(T plugin, Rank minRank, Rank[] include, String name, String... aliases) {
		super(minRank, include, name, aliases);

		this.plugin = plugin;
	}
	
	public T getPlugin() {
		return plugin;
	}
	
}
