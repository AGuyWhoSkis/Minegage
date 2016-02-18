package net.minegage.minigame.game;


import java.util.Comparator;
import java.util.List;


public class GameTypeComparator
		implements Comparator<GameType> {
		
	private List<GameType> prevTypes;
	
	public GameTypeComparator(List<GameType> prevTypes) {
		this.prevTypes = prevTypes;
	}
	
	@Override
	public int compare(GameType g1, GameType g2) {
		int index1 = prevTypes.indexOf(g1);
		int index2 = prevTypes.indexOf(g2);
		
		return Integer.compare(index1, index2);
	}
	
}
