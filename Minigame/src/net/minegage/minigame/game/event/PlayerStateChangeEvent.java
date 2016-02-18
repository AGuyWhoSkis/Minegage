package net.minegage.minigame.game.event;


import net.minegage.minigame.game.Game.PlayerState;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class PlayerStateChangeEvent
		extends Event {
		
	private static final HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
		
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	private Player player;
	private PlayerState prevState;
	private PlayerState newState;
	
	public PlayerStateChangeEvent(Player player, PlayerState prevState, PlayerState newState) {
		this.player = player;
		this.prevState = prevState;
		this.newState = newState;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public PlayerState getPrevState() {
		return prevState;
	}
	
	public PlayerState getNewState() {
		return newState;
	}
}
