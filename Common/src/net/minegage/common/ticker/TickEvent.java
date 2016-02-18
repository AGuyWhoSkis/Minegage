package net.minegage.common.ticker;


import net.minegage.common.ticker.Ticker.Tick;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class TickEvent
		extends Event {
		
	private static final HandlerList handlers = new HandlerList();
	
	private Tick type;
	
	public TickEvent(Tick type) {
		this.type = type;
	}
	
	public Tick getTick() {
		return type;
	}
	
	public boolean is(Tick type) {
		return getTick() == type;
	}
	
	public boolean isNot(Tick type) {
		return !is(type);
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
