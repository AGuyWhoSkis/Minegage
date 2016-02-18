package net.minegage.common.module;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

public abstract class BungeeModule
		extends PluginModule
		implements PluginMessageListener {

	public BungeeModule(String name, JavaPlugin plugin) {
		super(name, plugin);
	}

	@Override
	protected void onEnable() {
		super.onEnable();

		Messenger messenger = getServer()
				.getMessenger();

		messenger.registerOutgoingPluginChannel(plugin, "BungeeCord");
		messenger.registerIncomingPluginChannel(plugin, "BungeeCord", this);
	}

	@Override
	protected void onDisable() {
		super.onDisable();

		getServer().getMessenger().unregisterIncomingPluginChannel(plugin, "BungeeCord", this);
	}
}
