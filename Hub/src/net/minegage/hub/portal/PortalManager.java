package net.minegage.hub.portal;

import net.minegage.common.module.PluginModule;
import net.minegage.common.server.ping.ServerCounter;
import net.minegage.hub.portal.entity.PortalMobManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PortalManager
		extends PluginModule {

	private ServerCounter serverCounter;
	private PortalMobManager mobManager;

	public PortalManager(JavaPlugin plugin) {
		super("Portal Manager", plugin);

		serverCounter = new ServerCounter(plugin);
		mobManager = new PortalMobManager(serverCounter);
	}

}
