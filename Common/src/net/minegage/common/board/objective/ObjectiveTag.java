package net.minegage.common.board.objective;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ObjectiveTag 
		extends ObjectiveWrapper {
	
	public ObjectiveTag(Scoreboard board, String criteria) {
		super(board, criteria, DisplaySlot.BELOW_NAME);
		setDisplayName("");
	}
	
	public ObjectiveTag(Scoreboard board) {
		super(board, DisplaySlot.BELOW_NAME);
	}
	
	public ObjectiveTag(Objective other) {
		super(other);
	}
	
	public void setSuffix(String suffix) {
		setDisplayName(suffix);
	}
	
	public void setValue(Player player, int value) {
		setScore(player.getName(), value);
	}
	
}
