package net.minegage.minigame.team;


import java.util.Comparator;


public class GameTeamSizeComparator
		implements Comparator<GameTeam> {
	
	@Override
	public int compare(GameTeam t1, GameTeam t2) {
		return Integer.compare(t1.getPlayers()
				.size(), t2.getPlayers()
				.size());
	}
	
}
