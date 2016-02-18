package net.minegage.core.event;


import net.minegage.common.misc.Click;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EventClickEntity
		extends Event {
		
	private static final HandlerList handlers = new HandlerList();
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	private Entity clicked;
	private Player clicker;
	private Click click;
	
	public EventClickEntity(Entity clicked, Player clicker, Click click) {
		this.clicked = clicked;
		this.clicker = clicker;
		this.click = click;
	}
	
	public Entity getClicked() {
		return clicked;
	}
	
	public Player getClicker() {
		return clicker;
	}
	
	public Click getClick() {
		return click;
	}
	
}
