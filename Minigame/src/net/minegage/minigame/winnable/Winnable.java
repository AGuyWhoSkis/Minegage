package net.minegage.minigame.winnable;


import java.util.Set;

import org.bukkit.entity.Player;

import net.minegage.minigame.stats.StatTracker;


public abstract class Winnable<T> {
	
	protected T winnable;
	
	public Winnable(T winnable) {
		this.winnable = winnable;
	}
	
	public abstract Set<Player> getPlayers();
	
	public abstract Integer getScore(StatTracker statTracker, String stat);
	
	public abstract String getName();
	
	public abstract String getColour();
	
	public T getWinner() {
		return winnable;
	}
	
}
