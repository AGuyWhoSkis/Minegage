package net.minegage.minigame.game.games.paintball;


import net.minegage.common.C;
import net.minegage.common.board.Board;
import net.minegage.common.board.objective.ObjectiveSide;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.util.UtilUI;
import net.minegage.minigame.MinigameManager;
import net.minegage.minigame.board.helper.RankedTeamStatHelper;
import net.minegage.minigame.game.GameTDM;
import net.minegage.minigame.game.GameType;
import net.minegage.minigame.game.games.paintball.kit.KitDefault;
import net.minegage.minigame.stats.Stat;
import net.minegage.minigame.team.GameTeam;
import net.minegage.minigame.winnable.TeamComparator;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Team;

import java.util.List;


public class GamePaintball
		extends GameTDM {

	private final int TEAM_LIVES = 20;

	public GamePaintball(MinigameManager manager) {
		super(manager, GameType.PAINTBALL, new String[] { }, new KitDefault());
		
		this.timed = true;
		this.timeLimit = 60 * 3 + explainTime;

		getStatTracker().setDefaultTeamValue(Stat.LIVES, TEAM_LIVES);

		getBoardManager().addBoardHelper(new RankedTeamStatHelper(this, new TeamComparator(getStatTracker(), Stat.LIVES)));
	}
	
	@Override
	public boolean endCheck() {
		return super.endCheckSurvival();
	}
	
	@Override
	public void createTeams() {
		createTeam("Red", C.cRed).setArmourColour(Color.RED);
		createTeam("Blue", C.cAqua).setArmourColour(Color.AQUA);
	}
	
	private int timeRow;

	@Override
	public void giveBoard(Player player, Board board) {
		ObjectiveSide side = board.getSideObjective();
		
		side.addRow("");
		timeRow = side.addRow(""); // Time remaining
		side.addRow("");

		for (Team team : board.getBoard()
				.getTeams()) {
			team.setNameTagVisibility(NameTagVisibility.HIDE_FOR_OTHER_TEAMS);
		}
	}

	@EventHandler
	public void updateTime(TickEvent event) {
		if (event.isNot(Tick.SEC_1)) {
			return;
		}

		String displayTime = C.cYellow + UtilUI.getTimer((int) getRemainingSeconds());

		for (Board board : getBoardManager().getPlayerBoards()) {
			ObjectiveSide side = board.getSideObjective();
			side.updateRow(timeRow, displayTime);
		}
	}


	
	@EventHandler
	public void cancelTeleport(PlayerTeleportEvent event) {
		if (event.getCause() == TeleportCause.ENDER_PEARL) {
			event.setCancelled(true);
		}
	}
	
	@Override
	protected List<GameTeam> getWinners() {
		return super.getWinnersSurvival();
	}
	
}
