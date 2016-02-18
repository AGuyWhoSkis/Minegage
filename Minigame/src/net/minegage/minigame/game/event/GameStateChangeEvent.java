package net.minegage.minigame.game.event;


import net.minegage.minigame.game.Game.GameState;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class GameStateChangeEvent
		extends Event {
		
	private static final HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	private GameState prevState;
	private GameState newState;
	
	public GameStateChangeEvent(GameState prevState, GameState newState) {
		this.prevState = prevState;
		this.newState = newState;
	}
	
	public GameState getOldState() {
		return prevState;
	}
	
	public GameState getNewState() {
		return newState;
	}
}
