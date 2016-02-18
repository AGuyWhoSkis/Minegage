package net.minegage.hub.queue;


import java.net.InetSocketAddress;


public class GameServer
		implements Comparable<GameServer> {
	
	private final static int PING_FAIL_MULTIPLIER = 2;
	private final static int PING_DEFAULT_THRESHOLD = 10;
	
	private String name;
	private InetSocketAddress host;
	private boolean playing = true;
	private long maxPlayers;
	private long onlinePlayers;
	private int pingFailCount = 0;
	private int pingFailModulo = PING_DEFAULT_THRESHOLD;
	
	public GameServer(String name, String address, int port) {
		this.name = name;
		this.host = new InetSocketAddress(address, port);
	}
	
	public boolean isPlaying() {
		return playing;
	}
	
	public void setPlaying(boolean playing) {
		this.playing = playing;
	}
	
	public InetSocketAddress getHost() {
		return host;
	}
	
	public long getMaxPlayers() {
		return maxPlayers;
	}
	
	public void setMaxPlayers(long maxPlayers) {
		this.maxPlayers = maxPlayers;
	}
	
	public long getOnlinePlayers() {
		return onlinePlayers;
	}
	
	public void setOnlinePlayers(long onlinePlayers) {
		this.onlinePlayers = onlinePlayers;
	}
	
	public double getPercentageFilled() {
		if (maxPlayers == 0) {
			return 100D;
		}
		double online = onlinePlayers;
		double max = maxPlayers;
		
		return ( online / max ) * 100D;
	}
	
	public String getName() {
		return name;
	}
	
	public int getPingFailCount() {
		return pingFailCount;
	}
	
	public void handlePingFailed() {
		this.pingFailCount = this.pingFailCount + 1;
		
		if (getPingFailCount() % pingFailModulo == 0) {
			System.out.println("Failed to ping " + getName() + " (" + getHost().toString() + ") " + getPingFailCount() + " times");
			
			pingFailModulo *= PING_FAIL_MULTIPLIER;
		}
	}
	
	public void handlePingSuccess() {
		this.pingFailCount = 0;
		this.pingFailModulo = PING_DEFAULT_THRESHOLD;
	}
	
	@Override
	public int compareTo(GameServer other) {
		
		if (playing) {
			return 1;
		}
		
		double thisPercent = getPercentageFilled();
		double thatPercent = other.getPercentageFilled();
		
		if (thisPercent > 99) {
			// Not sure why this is here?
		}
		
		return ( thisPercent < thatPercent ) ? 1 : -1;
	}
	
}
