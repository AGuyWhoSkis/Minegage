package net.minegage.minigame.kit;


import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class SelectKitEvent
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
	private Kit kit;
	
	public SelectKitEvent(Player player, Kit kit) {
		this.player = player;
		this.kit = kit;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Kit getKit() {
		return kit;
	}
	
	
	
}
