package net.minegage.core.event;


import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class EventCancellable
		extends Event
		implements Cancellable {
	
	protected boolean cancelled = false;
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	
}
