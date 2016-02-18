package net.minegage.minigame.team;


import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class PlayerJoinTeamEvent
		extends Event {
		
	private static final HandlerList handlers = new HandlerList();
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	private Player player;
	private GameTeam team;
	
	public PlayerJoinTeamEvent(Player player, GameTeam team) {
		this.player = player;
		this.team = team;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public GameTeam getTeam() {
		return team;
	}
	
}
