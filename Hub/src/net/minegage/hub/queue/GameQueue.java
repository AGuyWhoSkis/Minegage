package net.minegage.hub.queue;


import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.UUID;


public class GameQueue {
	
	private Queue queue;
	
	private String name;
	private TreeSet<GameServer> servers;
	private List<UUID> connectionQueue = new ArrayList<>();
	private List<String> names;
	private int minPlayers;
	
	public GameQueue(Queue queue, String name, List<String> names, TreeSet<GameServer> servers, int minPlayers) {
		this.queue = queue;
		this.name = name;
		this.names = names;
		this.servers = servers;
		this.minPlayers = minPlayers;
	}
	
	@EventHandler
	public void notifyTick(TickEvent event) {
		if (!event.getTick().equals(Tick.SEC_30)) {
			return;
		}
	}
	
	public TreeSet<GameServer> getServers() {
		return servers;
	}
	
	public String getName() {
		return name;
	}
	
	public void add(Player player) {
		connectionQueue.add(player.getUniqueId());
	}
	
	public List<UUID> getQueued() {
		return connectionQueue;
	}
	
	public void remove(Player player) {
		connectionQueue.remove(player.getUniqueId());
	}
	
	public List<String> getNames() {
		return names;
	}
	
	public int getMinPlayers() {
		return minPlayers;
	}
	
	public void attemptConnect() {
		List<GameServer> availServers = new ArrayList<GameServer>();
		
		for (GameServer server : servers) {
			if (!server.isPlaying() && server.getPercentageFilled() < 100) {
				availServers.add(server);
			}
		}
		
		for (GameServer server : availServers) {
			long freeSlots = server.getMaxPlayers() - server.getOnlinePlayers();
			long online = server.getOnlinePlayers();
			
			/*
			 * Get as many connections as possible, check if above limit
			 */
			long newSize = getQueued().size() + online;
			if (online < freeSlots && newSize >= minPlayers) {
				
				List<UUID> toConnect = new ArrayList<>();
				
				int index = 0;
				long newPlayers = online;
				
				// Find first open server closest to index 0 (sorted from
				// greatest to least in % filled)
				while (index < connectionQueue.size()) {
					UUID uid = connectionQueue.get(index);
					
					//TODO: if (party.size() <= freeSlots) {
						toConnect.add(uid);
						newPlayers += 1; //TODO: newPlayers += party.size()
					//}
					
					index++;
				}
				
				if (toConnect.size() > 0 && newPlayers >= getMinPlayers()) {
					
					for (UUID uid : toConnect) {
						connectionQueue.remove(uid);
						Player player = Bukkit.getPlayer(uid);
						
						if (player == null || !player.isOnline()) {
							continue;
						}
						
						connect(player, server);
					}
					
					// Re-add server in TreeSet to sort it again
					servers.remove(server);
					server.setOnlinePlayers(newPlayers);
					servers.add(server);
				}
				online = server.getOnlinePlayers();
				freeSlots = server.getMaxPlayers() - online;
			}
		}
	}
	
	public void connect(Player player, GameServer server) {
		queue.getServerManager().connect(player, server.getName());
	}
}
