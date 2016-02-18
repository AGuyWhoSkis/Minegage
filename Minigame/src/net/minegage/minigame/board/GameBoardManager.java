package net.minegage.minigame.board;


import net.minegage.common.board.Board;
import net.minegage.common.board.objective.ObjectiveSide;
import net.minegage.core.board.BoardManager;
import net.minegage.common.C;
import net.minegage.minigame.board.helper.BoardHelper;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.game.Game.GameState;
import net.minegage.minigame.game.Game.PlayerState;
import net.minegage.minigame.game.event.GameStateChangeEvent;
import net.minegage.minigame.game.event.PlayerStateChangeEvent;
import net.minegage.minigame.team.GameTeam;
import net.minegage.minigame.team.PlayerJoinTeamEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;


/**
 * Board manager which is used during the actual gameplay of the game
 */
public class GameBoardManager
		extends BoardManager {

	protected Game game;
	protected List<BoardHelper> boardHelpers = new ArrayList<>();

	public GameBoardManager(Game game) {
		super(game.getPlugin());
		this.game = game;
	}

	@Override
	protected void onDisable() {
		super.onDisable();

		for (BoardHelper helper : boardHelpers) {
			helper.disable();
		}

		boardHelpers.clear();

		game = null;
	}


	@EventHandler
	public void setDisplay(PlayerJoinTeamEvent event) {
		if (!game.inMap()) {
			return;
		}

		setDisplayName(event.getPlayer(), event.getTeam());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void giveBoard(GameStateChangeEvent event) {
		if (event.getNewState() == GameState.PLAYING) {

			for (Player player : Bukkit.getOnlinePlayers()) {
				giveBoard(player);
			}

			for (BoardHelper helper : boardHelpers) {
				helper.enable();
			}
		}
	}

	// Low priority so that the player will be assigned a team, given spectator, etc. before the
	// board is given.
	@EventHandler (priority = EventPriority.LOW)
	public void giveBoard(PlayerJoinEvent event) {
		if (!game.inMap()) {
			return;
		}

		giveBoard(event.getPlayer());
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onOut(PlayerStateChangeEvent event) {
		if (!game.inMap()) {
			return;
		}

		if (event.getNewState() == PlayerState.OUT) {
			Player player = event.getPlayer();

			GameTeam team = game.getTeam(player);

			setDisplayName(player, team);
		}
	}

	public void setDisplayName(Player player, GameTeam gameTeam) {
		Board board = getBoard(player);

		String prefix = gameTeam.getPrefix();

		if (game.getState(player) == PlayerState.OUT) {
			prefix = C.cGray + "DEAD " + prefix;
		}

		setPrefix(player, prefix);
		player.setDisplayName(prefix + player.getName() + C.cReset);
	}

	// Called through game manager, or when a player joins
	public void giveBoard(Player player) {
		Board board = new Board();
		setBoard(player, board);

		ObjectiveSide side = board.setSideObjective();
		side.setHeader(C.cGreen + C.cBold + game.getName());

		for (GameTeam gameTeam : game.getTeams()) {
			Team team = board.createTeam();

			for (Player p : gameTeam.getPlayers()) {
				team.addEntry(p.getName());
			}

			team.setPrefix(gameTeam.getPrefix());
		}

		game.giveBoard(player, board);

		for (BoardHelper helper : boardHelpers) {
			helper.giveBoard(player, board);
		}
	}

	public void addBoardHelper(BoardHelper helper) {
		boardHelpers.add(helper);

		if (game.inLobby()) {
			helper.disable();
		}
	}


	public Game getGame() {
		return game;
	}

}
