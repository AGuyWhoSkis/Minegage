package net.minegage.common.board.objective;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ObjectiveTab 
		extends ObjectiveWrapper {
	
	public ObjectiveTab(Scoreboard board, String criteria) {
		super(board, criteria, DisplaySlot.PLAYER_LIST);
	}
	
	public ObjectiveTab(Scoreboard board) {
		super(board, DisplaySlot.PLAYER_LIST);
	}
	
	public ObjectiveTab(Objective other) {
		super(other);
	}	
	
	public void setScore(Player player, int score) {
		setScore(player.getName(), score);
	}
	
}
