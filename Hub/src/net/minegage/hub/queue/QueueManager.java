package net.minegage.hub.queue;


import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;


public class QueueManager 
		implements Listener {
	
	private List<GameQueue> gameQueues = new ArrayList<>();
	
	private Queue queue;
	
	public QueueManager(Queue queue) {
		this.queue = queue;
		
		queue.registerEvents(this);
	}
	
	public void queue(String game, Player player) {
		GameQueue gameQueue = getGameQueue(game);
		gameQueue.add(player);
	}
	
	public List<GameServer> getServers() {
		List<GameServer> ret = new ArrayList<GameServer>();
		for (GameQueue queue : gameQueues) {
			for (GameServer server : queue.getServers()) {
				ret.add(server);
			}
		}
		return ret;
	}
	
	public GameQueue getGameQueue(Player player) {
		UUID uid = player.getUniqueId();
		for (GameQueue gameQueue : gameQueues) {
			if (gameQueue.getQueued().contains(uid)) {
				return gameQueue;
			}
		}
		
		return null;
	}
	
	public GameQueue getGameQueue(String game) {
		for (GameQueue gameQueue : gameQueues) {
			for (String name : gameQueue.getNames()) {
				if (name.equalsIgnoreCase(game)) {
					return gameQueue;
				}
			}
		}
		
		return null;
	}
	
	public void reloadGameQueues() {
		FileConfiguration config = queue.getAddressConfig();
		
		gameQueues.clear();
		
		if (!config.contains("queue.games")) {
			return;
		}
		
		ConfigurationSection games = config.getConfigurationSection("queue.games");
		
		for (String queueName : games.getKeys(false)) {
			ConfigurationSection game = games.getConfigurationSection(queueName);
			
			List<String> names = game.getStringList("names");	
			int minPlayers = game.getInt("min players");
			
			TreeSet<GameServer> servers = new TreeSet<>();
			
			for (String string : game.getStringList("servers")) {
				String[] split = string.split(":");
				String name = split[0];
				String address = split[1];
				int port = Integer.parseInt(split[2]);
				
				GameServer gameServer = new GameServer(name, address, port);
				servers.add(gameServer);
			}
			
			GameQueue gameQueue = new GameQueue(queue, queueName, names, servers, minPlayers);
			gameQueues.add(gameQueue);
		}
	}
	
	public List<GameQueue> getGameQueues() {
		return gameQueues;
	}
	
}
