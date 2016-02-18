package net.minegage.minigame.map;


import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;


public class EventPlayerOutOfBounds
		extends PlayerEvent {
	
	private static final HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
		
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public EventPlayerOutOfBounds(Player player) {
		super(player);
	}
	
}
