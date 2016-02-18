package net.minegage.minigame.board.helper;

import net.minegage.common.board.Board;
import net.minegage.common.board.objective.ObjectiveSide;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.C;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.stats.StatTracker;
import net.minegage.minigame.stats.UpdateTeamStatEvent;
import net.minegage.minigame.team.GameTeam;
import net.minegage.minigame.winnable.TeamComparator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.Iterator;
import java.util.List;

public class RankedTeamStatHelper
		extends BoardHelper {

	private int startRow = -1;
	private boolean listUpdate = false;

	private TeamComparator comparator;

	public RankedTeamStatHelper(Game game, TeamComparator comparator) {
		super(game, "Ranked Team Stats");

		this.comparator = comparator;
	}

	@Override
	public void giveBoard(Player player, Board board) {
		ObjectiveSide side = board.getSideObjective();
		startRow = side.nextRowNum();
		updateBoard(player, side);
	}

	@EventHandler
	public void tickSeconds(TickEvent event) {
		if (event.isNot(Tick.SEC_1)) {
			return;
		}

		Game game = getGame();

		if (listUpdate && game.inMap()) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				updateBoard(player, getBoardManager().getBoard(player).getSideObjective());
			}
			listUpdate = false;
		}
	}

	@EventHandler
	public void update(UpdateTeamStatEvent event) {
		if (comparator.getStat().equals(event.getStat())) {
			listUpdate = true;
		}
	}

	private void updateBoard(Player player, ObjectiveSide side) {
		Game game = getGame();
		if (game == null) {
			return;
		}

		List<GameTeam> teams = game.getTeamsVisible();
		teams.sort(comparator);

		for (int i = startRow; i > 0; i--) {
			side.removeRow(i);
		}

		StatTracker stats = game.getStatTracker();

		Iterator<GameTeam> teamsIt = teams.iterator();
		while (side.hasRoom() && teamsIt.hasNext()) {
			GameTeam  next  = teamsIt.next();

			Integer score = stats.get(next, comparator.getStat());

			if (score > 0) {
				String content = score + " " + next.getName();
				side.addRow(content);
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
