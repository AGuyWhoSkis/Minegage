package net.minegage.minigame.event;


import net.minegage.minigame.game.Game;
import net.minegage.minigame.stats.Stat;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


/* Controls whether or not the player should be removed from the game on death */
public class GameDeathEvent
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
	private Player player;
	private boolean playerOut;
	
	public GameDeathEvent(Game game, Player player) {
		this.game = game;
		this.player = player;
		
		this.playerOut = game.deathOut && game.getStatTracker()
				.get(player, Stat.DEATHS) >= game.deathOutCount;
				
	}
	
	public Game getGame() {
		return game;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public boolean isPlayerOut() {
		return playerOut;
	}
	
	public void setPlayerOut(boolean playerOut) {
		this.playerOut = playerOut;
	}
	
}
