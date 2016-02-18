package net.minegage.minigame.winnable;


import net.minegage.minigame.stats.StatTracker;
import net.minegage.minigame.team.GameTeam;


public class TeamComparator
		extends WinnableComparator<GameTeam> {
		
	public TeamComparator(StatTracker statTracker, String stat) {
		super(statTracker, stat);
	}
	
	@Override
	public int getStat(StatTracker statTracker, String stat, GameTeam team) {
		return statTracker.get(team, stat);
	}
	
}
