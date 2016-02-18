package net.minegage.hub.queue;


import net.minegage.common.C;
import net.minegage.common.module.LazyScheduler;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashSet;
import java.util.Set;


public class UpdateQueue
		extends LazyScheduler {
	
	private Queue queue;
	private final Connector connect;
	
	private Object gameQueueLock = new Object();
	
	public UpdateQueue(Queue queue) {
		super(queue.getPlugin());
		this.queue = queue;

		connect = new Connector();

		runAsyncTimer(20L, queue.REFRESH_INTERVAL, new BukkitRunnable() {
			@Override
			public void run() {
				synchronized(gameQueueLock) {
					for (GameQueue gameQueue : queue.getQueueManager().getGameQueues()) {
						for (GameServer server : gameQueue.getServers()) {

							connect.setServer(server);
							connect.setTimeout(queue.TIMEOUT);

							String data = connect.ping();

							if (data != null) {
								JSONParser parser = new JSONParser();
								JSONObject main;
								try {
									main = (JSONObject) parser.parse(data);

									String description = (String) main.get("description");
									description = C.strip(description);
									boolean playing = description.contains("In Game");

									JSONObject players = (JSONObject) main.get("players");

									long maxPlayers = (long) players.get("max");
									long onPlayers = (long) players.get("online");

									server.setPlaying(playing);
									server.setMaxPlayers(maxPlayers);
									server.setOnlinePlayers(onPlayers);
								} catch (ParseException e) {
									e.printStackTrace();
								}

							} else {
								server.setPlaying(true);
								server.setMaxPlayers(0);
								server.setOnlinePlayers(0);
							}
						}

						Set<GameServer> serversCopy = new HashSet<>(gameQueue.getServers());
						gameQueue.getServers().clear();
						gameQueue.getServers().addAll(serversCopy);

						gameQueue.attemptConnect();
					}
				}

			}
		});
	}
	
	public Object getGameQueueLock() {
		return gameQueueLock;
	}
	
	
	
}
