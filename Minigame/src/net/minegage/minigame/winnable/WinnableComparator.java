package net.minegage.minigame.winnable;


import net.minegage.minigame.stats.StatTracker;

import java.util.Comparator;


public abstract class WinnableComparator<T>
		implements Comparator<T> {
		
	private StatTracker statTracker;
	private String stat;
	
	public WinnableComparator(StatTracker statTracker, String stat) {
		this.statTracker = statTracker;
		this.stat = stat;
	}
	
	@Override
	public int compare(T e1, T e2) {
		int val1 = -1;
		int val2 = -1;
		
		if (e1 != null) {
			val1 = getStat(statTracker, stat, e1);
		}
		if (e2 != null) {
			val2 = getStat(statTracker, stat, e2);
		}
		
		return Integer.compare(val2, val1);
	}
	
	public abstract int getStat(StatTracker statTracker, String stat, T e);

	public String getStat() {
		return stat;
	}

}
