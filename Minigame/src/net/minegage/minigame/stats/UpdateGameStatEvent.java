package net.minegage.minigame.stats;

import net.minegage.minigame.game.Game;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UpdateGameStatEvent
		extends Event {

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	private Game game;
	private String stat;
	private int oldValue;
	private int newValue;

	public UpdateGameStatEvent(Game game, String stat, int oldValue, int newValue) {
		this.game = game;
		this.stat = stat;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public Game getGame() {
		return game;
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
