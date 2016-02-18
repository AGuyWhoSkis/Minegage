package net.minegage.common.server;


import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class EventPluginMessageReceive
		extends Event {
	
	private static final HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	private byte[] message;
	
	public EventPluginMessageReceive(byte[] message) {
		this.message = message;
	}
	
	public ByteArrayDataInput getInput() {
		return ByteStreams.newDataInput(message);
	}
	
}
