package net.minegage.minigame.winnable;


import java.util.Set;

import net.minegage.minigame.stats.StatTracker;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import com.google.common.collect.Sets;

import net.minegage.common.C;


public class WinnablePlayer
		extends Winnable<Player> {
		
	public WinnablePlayer(Player player) {
		super(player);
	}
	
	@Override
	public Set<Player> getPlayers() {
		return Sets.newHashSet(winnable);
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
		if (winnable.getScoreboard() != null) {
			for (Team team : winnable.getScoreboard()
					.getTeams()) {
				if (team.getEntries()
						.contains(winnable.getName())) {
					return team.getPrefix();
				}
			}
		}
		
		return C.cWhite;
	}
	
}
