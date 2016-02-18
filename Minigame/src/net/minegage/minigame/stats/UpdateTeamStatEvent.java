package net.minegage.minigame.stats;


import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import net.minegage.minigame.team.GameTeam;


public class UpdateTeamStatEvent
		extends Event {
		
	private static final HandlerList handlers = new HandlerList();
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	private GameTeam team;
	private String stat;
	private int oldValue;
	private int newValue;
	
	public UpdateTeamStatEvent(GameTeam team, String stat, int oldValue, int newValue) {
		this.team = team;
		this.stat = stat;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	public GameTeam getTeam() {
		return team;
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
