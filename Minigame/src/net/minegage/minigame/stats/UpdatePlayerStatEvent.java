package net.minegage.minigame.stats;


import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class UpdatePlayerStatEvent
		extends Event {
		
	private static final HandlerList handlers = new HandlerList();
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	private Player player;
	private String stat;
	private int oldValue;
	private int newValue;
	
	public UpdatePlayerStatEvent(Player player, String stat, int oldValue, int newValue) {
		this.player = player;
		this.stat = stat;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public String getStat() {
		return stat;
	}
	
	public int getOldValue() {
		return oldValue;
	}
	
	public int getNewValue() {
		return newValue;
	}
	
	
	
	
}
