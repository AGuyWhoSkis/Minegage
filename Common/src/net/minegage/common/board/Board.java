package net.minegage.common.board;


import net.minegage.common.board.objective.ObjectiveSide;
import net.minegage.common.board.objective.ObjectiveTab;
import net.minegage.common.board.objective.ObjectiveTag;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;


public class Board {
	
	private int objCount = 1;
	private int teamCount = 1;
	
	protected Scoreboard board;
	protected ObjectiveSide sideObjective;
	protected ObjectiveTag tagObjective;
	protected ObjectiveTab tabObjective;
	
	public Board() {
		board = Bukkit.getScoreboardManager()
				.getNewScoreboard();
	}
	
	public Board(Board other) {
		this();
		
		Scoreboard otherBoard = other.getBoard();
		
		for (Team otherTeam : otherBoard.getTeams()) {
			Team team = board.registerNewTeam(otherTeam.getName());
			team.setDisplayName(otherTeam.getDisplayName());
			team.setPrefix(otherTeam.getPrefix());
			team.setSuffix(otherTeam.getSuffix());
			
			team.setAllowFriendlyFire(otherTeam.allowFriendlyFire());
			team.setCanSeeFriendlyInvisibles(otherTeam.canSeeFriendlyInvisibles());
			team.setNameTagVisibility(otherTeam.getNameTagVisibility());
		}
		
		ObjectiveSide otherSide = other.getSideObjective();
		ObjectiveTag otherTag = other.getTagObjective();
		ObjectiveTab otherTab = other.getTabObjective();
		
		for (Objective obj : otherBoard.getObjectives()) {
			Objective copy = board.registerNewObjective(obj.getName(), obj.getCriteria());
			copy.setDisplayName(obj.getDisplayName());
			copy.setDisplaySlot(obj.getDisplaySlot());
			
			for (String entry : otherBoard.getEntries()) {
				Score score = obj.getScore(entry);
				int value = score.getScore();
				
				if (value != 0) {
					copy.getScore(entry)
							.setScore(value);
				}
			}
			
			if (otherSide.getObjective()
					.equals(copy)) {
				sideObjective = new ObjectiveSide(copy);
			} else if (otherTag.getObjective()
					.equals(copy)) {
				tagObjective = new ObjectiveTag(copy);
			} else if (otherTab.getObjective()
					.equals(copy)) {
				tabObjective = new ObjectiveTab(copy);
			}
		}
	}

	public void setPrefix(Player player, String prefix) {
		if (prefix == null || prefix.equals("")) {
			// Remove prefix
			Team prefixTeam = getBoard().getEntryTeam(player.getName());
			if (prefixTeam != null) {
				prefixTeam.removeEntry(player.getName());
			}
			
			return;
		}

		String safePrefix = prefix;
		if (safePrefix.length() > 16) {
			safePrefix = safePrefix.substring(0, 17);
		}

		Team team = getBoard().getTeam(safePrefix);
		if (team == null) {
			team = createTeam(safePrefix);
			team.setPrefix(safePrefix);
		}
		
		if (!team.getEntries()
				.contains(player.getName())) {
			team.addEntry(player.getName());
		}
	}
	
	public Team createTeam() {
		return board.registerNewTeam("t" + teamCount++);
	}

	public Team createTeam(String name) {
		return board.registerNewTeam(name);
	}
	
	public Scoreboard getBoard() {
		return board;
	}
	
	public ObjectiveSide setSideObjective() {
		return ( sideObjective = new ObjectiveSide(board) );
	}
	
	public ObjectiveTag setTagObjective() {
		return ( tagObjective = new ObjectiveTag(board) );
	}
	
	public ObjectiveTab setTabObjective() {
		return ( tabObjective = new ObjectiveTab(board) );
	}
	
	public void setSideObjective(ObjectiveSide sideObjective) {
		this.sideObjective = sideObjective;
	}
	
	public void setTagObjective(ObjectiveTag tagObjective) {
		this.tagObjective = tagObjective;
	}
	
	public void setTabObjective(ObjectiveTab tabObjective) {
		this.tabObjective = tabObjective;
	}
	
	public ObjectiveSide getSideObjective() {
		return sideObjective;
	}
	
	public ObjectiveTag getTagObjective() {
		return tagObjective;
	}
	
	public ObjectiveTab getTabObjective() {
		return tabObjective;
	}
	
	int nextTeamCount() {
		return teamCount++;
	}
	
	int nextObjCount() {
		return objCount++;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Board) {
			return ((Board) obj).getBoard().equals(this.board);
		} else return false;
	}
}
