package net.minegage.minigame.winnable;


import java.util.HashSet;
import java.util.Set;

import net.minegage.minigame.stats.StatTracker;
import net.minegage.minigame.team.GameTeam;
import org.bukkit.entity.Player;


public class WinnableTeam
		extends Winnable<GameTeam> {
		
	public WinnableTeam(GameTeam team) {
		super(team);
	}
	
	@Override
	public Set<Player> getPlayers() {
		return new HashSet<>(winnable.getPlayers());
	}
	
	@Override
	public Integer getScore(StatTracker statTracker, String stat) {
		return statTracker.get(winnable, stat);
	}
	
	@Override
	public String getName() {
		return winnable.getName();
	}
	
	@Override
	public String getColour() {
		return winnable.getPrefix();
	}
	
}
