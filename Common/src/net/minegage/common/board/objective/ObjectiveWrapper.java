package net.minegage.common.board.objective;


import net.minegage.common.C;
import net.minegage.common.board.Criteria;
import net.minegage.common.util.UtilJava;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashSet;
import java.util.Set;


class ObjectiveWrapper {
	
	public static final int MAX_DISPLAY_LENGTH = 30;
	private static final int MAX_ENTRY_LENGTH = 40;
	
	protected Objective objective;
	
	public ObjectiveWrapper(Scoreboard board, String name, String criteria, DisplaySlot slot) {
		
		int i = 1;
		String safeName = name;
		
		outer:
		while (true) {
			for (Objective objective : board.getObjectives()) {
				if (objective.getName()
						.equals(safeName)) {
					safeName = name + i++;
					continue outer;
				}
			}
			break;
		}
		
		setObjective(board.registerNewObjective(safeName, criteria));
		setDisplaySlot(slot);
	}
	
	public ObjectiveWrapper(Scoreboard board, String criteria, DisplaySlot slot) {
		this(board, slot.name(), criteria, slot);
	}
	
	public ObjectiveWrapper(Scoreboard board, DisplaySlot slot) {
		this(board, Criteria.DUMMY, slot);
	}
	
	public ObjectiveWrapper(Objective other) {
		setObjective(other);
	}
	
	Score setScore(String entry, int score) {
		Score bScore = getObjective().getScore(entry);
		bScore.setScore(score);
		return bScore;
	}
	
	protected void deleteScore(Score score) {
		if (score != null) {
			getScoreboard().resetScores(score.getEntry());
		}
	}
	
	public Set<Score> getPositiveScores() {
		Set<Score> scores = new HashSet<>();
		
		for (String entry : getScoreboard().getEntries()) {
			Score score = getObjective().getScore(entry);
			if (score.getScore() > 0) {
				scores.add(score);
			}
		}
		
		return scores;
	}
	
	public Set<String> getPositiveEntries() {
		Set<String> entries = new HashSet<>();
		
		for (String entry : getScoreboard().getEntries()) {
			if (getObjective().getScore(entry)
					.getScore() > 0) {
				entries.add(entry);
			}
		}
		
		return entries;
	}
	
	protected Score getScore(int score) {
		for (Score bScore : getPositiveScores()) {
			if (bScore.getScore() == score && bScore.getObjective()
					.equals(getObjective())) {
				return bScore;
			}
		}
		return null;
	}
	
	protected Score getScore(String entry) {
		String processed = C.translate(entry);
		for (Score score : getPositiveScores()) {
			if (score.getEntry()
					.equals(processed)) {
				return score;
			}
		}
		return null;
	}
	
	public String getScoreEntry(Score score) {
		return score == null ? null : score.getEntry();
	}
	
	public int getScoreValue(Score score) {
		return score == null ? 0 : score.getScore();
	}
	
	public String getDisplayName() {
		return getObjective().getDisplayName();
	}
	
	protected void setDisplayName(String displayName) {
		String processed = C.translate(displayName);
		processed = getSafeDisplay(processed);
		getObjective().setDisplayName(processed);
	}
	
	public DisplaySlot getDisplaySlot() {
		return getObjective().getDisplaySlot();
	}
	
	protected void setDisplaySlot(DisplaySlot slot) {
		getObjective().setDisplaySlot(slot);
	}
	
	public String getCriteria() {
		return getObjective().getCriteria();
	}
	
	public boolean isSafeDisplay(String content) {
		return UtilJava.isSafe(content, MAX_DISPLAY_LENGTH);
	}
	
	public boolean isSafeEntry(String entry) {
		return UtilJava.isSafe(entry, MAX_ENTRY_LENGTH);
	}
	
	public String getSafeDisplay(String displayName) {
		return UtilJava.getSafe(displayName, MAX_DISPLAY_LENGTH);
	}
	
	public String getSafeEntry(String entry) {
		return UtilJava.getSafe(entry, MAX_ENTRY_LENGTH);
	}
	
	public Scoreboard getScoreboard() {
		return getObjective().getScoreboard();
	}
	
	public Objective getObjective() {
		return objective;
	}
	
	public void setObjective(Objective objective) {
		this.objective = objective;
	}
	
}
