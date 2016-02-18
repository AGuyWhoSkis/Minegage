package net.minegage.minigame.board.helper;

import net.minegage.common.board.Board;
import net.minegage.common.board.objective.ObjectiveSide;
import net.minegage.common.log.L;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.C;
import net.minegage.minigame.event.GameDeathEvent;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.team.GameTeam;
import net.minegage.minigame.team.PlayerJoinTeamEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Iterator;


public class SurvivalHelper
		extends BoardHelper {


	// Only update the list when needed
	private boolean listUpdate = false;
	private int startRow = -1;

	public SurvivalHelper(Game game) {
		super(game, "Survival");
	}

	@Override
	public void giveBoard(Player player, Board board) {
		ObjectiveSide side = board.getSideObjective();
		startRow = side.nextRowNum();
		updateBoard(side);
	}

	@EventHandler
	public void tickSeconds(TickEvent event) {
		if (event.isNot(Tick.SEC_1)) {
			return;
		}

		Game game = getGame();

		if (game.inMap()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				Board board = getBoardManager().getBoard(player);
				updateBoard(board.getSideObjective());
			}
			listUpdate = false;
		}
	}


	@EventHandler
	public void joinTeam(PlayerJoinTeamEvent event) {
		listUpdate = true;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		listUpdate = true;
	}

	@EventHandler
	public void onDead(GameDeathEvent event) {
		if (event.isPlayerOut()) {
			listUpdate = true;
		}
	}

	private void updateBoard(ObjectiveSide side) {
		// Only update the team list when needed

		if (!listUpdate) {
			return;
		}

		Game game  = getGame();
		int  teams = game.getTeamsVisible().size();
		int players = game.getTeamsVisible().stream()
				.mapToInt(team -> team.getPlayersIn()
						.size())
				.sum();

		int expandedRows  = teams + players + (teams - 1);
		int minimizedRows = (2 * teams) + (teams - 1);

		int availRows = startRow - 1;

		boolean expanded;
		if (expandedRows <= availRows) {
			expanded = true;
		} else if (minimizedRows <= availRows) {
			expanded = false;
		} else {
			L.severe("Unable to update board; not enough room!");
			return;
		}

		for (int i = startRow; i > 0; i--) {
			side.removeRow(i);
		}

		Iterator<GameTeam> teamsIt = game.getTeamsVisible().iterator();
		while (teamsIt.hasNext()) {
			GameTeam team = teamsIt.next();

			String name = C.cBold + team.getName();
			if (team.getPlayersIn().size() == 0) {
				name = C.cStrike + name;
			}

			// Team name
			side.addRow(team.getPrefix() + name);

			if (expanded) {
				for (Player player : team.getPlayersIn()) {
					side.addRow(team.getPrefix() + player.getName());
				}
			} else {
				side.addRow(team.getPrefix() + team.getPlayersIn().size() + " alive");
			}

			// Spacer
			if (teamsIt.hasNext()) {
				side.addRow("");
			}
		}

		if (side.hasRoom()) {
			side.addRow("");

			if (side.hasRoom()) {
				side.addRow(C.cAqua + "minegage.net");
			}
		}

	}


}
