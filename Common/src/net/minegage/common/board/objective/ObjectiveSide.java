package net.minegage.common.board.objective;


import net.minegage.common.C;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Set;


public class ObjectiveSide
		extends ObjectiveWrapper {
		
	/* A sidebar objective can have up to 15 rows. Each row must have a score. Rows are
	 * automatically (and unavoidably) displayed in decreasing order of their score, from the top of
	 * the scoreboard to the bottom. Here, the score of a row is referred to as its row number */
	
	public ObjectiveSide(Scoreboard board, String criteria) {
		super(board, criteria, DisplaySlot.SIDEBAR);
	}
	
	public ObjectiveSide(Scoreboard board) {
		super(board, DisplaySlot.SIDEBAR);
	}
	
	public ObjectiveSide(Objective other) {
		super(other);
	}
	
	public void setHeader(String header) {
		setDisplayName(header);
	}
	
	public void setRow(int rowNum, String entry) {
		String padded = padEntry(entry);
		setScore(padded, rowNum);
	}
	
	public void updateRow(int rowNum, String content) {
		Score score = getScore(rowNum);

		String raw = content.replaceAll("&r", "");
		String rawScore = score.getEntry().replace("&r", "");

		if (raw.equals(rawScore)) {
			return;
		}

		String padded = padEntry(content);
		
		setScore(padded, rowNum);
		removeRow(score);
	}
	
	public int addRow(String content) {
		int rowNum = nextRowNum();
		setRow(rowNum, content);
		return rowNum;
	}
	
	public boolean hasRoom() {
		return getPositiveScores().size() < 15;
	}
	
	public void removeRow(Score score) {
		deleteScore(score);
	}
	
	public void removeRow(int rowNum) {
		Score score = getScore(rowNum);
		if (score != null) {
			removeRow(score);
		}
	}
	
	public int nextRowNum() {
		return 15 - getPositiveScores().size();
	}

	public int getAvailableRows() {
		return nextRowNum() - 1;
	}
	
	public String padEntry(String entry) {
		Set<String> entries = getPositiveEntries();
		
		entry = padEntry(entry, entries);
		
		if (!isSafeEntry(entry)) {
			throw new IllegalStateException("Padding sidebar objective entry failed");
		}
		
		return entry;
	}
	
	private String padEntry(String entry, Set<String> entries) {
		for (String otherEntry : entries) {
			if (entry.equals(otherEntry)) {
				entry += C.cReset; // Spacer
				entry = padEntry(entry, entries);
				break;
			}
		}
		
		return entry;
	}
	
}
