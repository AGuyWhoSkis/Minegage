package net.minegage.hub.queue;


import net.minegage.common.module.PluginModule;
import net.minegage.common.server.ServerManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

//TODO: Fix this atrocious code
public class Queue
		extends PluginModule {
	
	private QueueManager queueManager;
	private FileConfiguration config;
	private UpdateQueue updateQueue;
	private ServerManager serverManager;
	
	public int REFRESH_INTERVAL;
	public int REFRESH_UPDATES = 0;
	public int TIMEOUT = 100;
	
	public Queue(JavaPlugin plugin, ServerManager serverManager) {
		super("Queue Manager", plugin);
		this.serverManager = serverManager;
		
		addCommand(new CommandQueue(this));
		
	}
	
	@Override
	public void onEnable() {
		queueManager = new QueueManager(this);
		reload();
		updateQueue = new UpdateQueue(this);
	}
	
	public void reload() {
		File dir = plugin.getDataFolder();
		
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		File configFile = new File(plugin.getDataFolder(), "config.yml");
		
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		config = YamlConfiguration.loadConfiguration(configFile);
		
		config.addDefault("queue.settings.refresh-interval", 20);
		config.addDefault("queue.settings.timeout", 100);
		config.options().copyDefaults(true);
		
		REFRESH_INTERVAL = config.getInt("queue.settings.refresh-interval");
		REFRESH_UPDATES = config.getInt("queue.settings.refresh-updates");
		TIMEOUT = config.getInt("queue.settings.timeout");
		
		getQueueManager().reloadGameQueues();
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		synchronized(getUpdateQueue().getGameQueueLock()) {	
			GameQueue queue = queueManager.getGameQueue(player);
			if (queue != null) {
				queue.remove(player);
			}
		}
	}
	
	public QueueManager getQueueManager() {
		return queueManager;
	}
	
	public FileConfiguration getAddressConfig() {
		return config;
	}
	
	public UpdateQueue getUpdateQueue() {
		return updateQueue;
	}
	
	public ServerManager getServerManager() {
		return serverManager;
	}
	
}
