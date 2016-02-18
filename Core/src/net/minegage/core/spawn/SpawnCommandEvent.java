package net.minegage.core.spawn;


import net.minegage.core.event.EventCancellable;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;


public class SpawnCommandEvent
		extends EventCancellable {
		
	private static final HandlerList handlers = new HandlerList();
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	private Player player;
	
	public SpawnCommandEvent(Player player) {
		this.player = player;
	}
	
	public Player getPlayer() {
		return player;
	}
	
}
