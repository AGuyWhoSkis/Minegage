package net.minegage.minigame.winnable;


import org.bukkit.entity.Player;

import net.minegage.minigame.stats.StatTracker;


public class PlayerComparator
		extends WinnableComparator<Player> {
		
	public PlayerComparator(StatTracker statTracker, String stat) {
		super(statTracker, stat);
	}
	
	@Override
	public int getStat(StatTracker statTracker, String stat, Player player) {
		return statTracker.get(player, stat);
	}
	
}
