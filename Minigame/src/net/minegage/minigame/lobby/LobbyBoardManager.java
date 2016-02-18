package net.minegage.minigame.lobby;


import net.minegage.common.board.Board;
import net.minegage.common.board.objective.ObjectiveSide;
import net.minegage.common.datafile.WorldDataLoadEvent;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.util.UtilServer;
import net.minegage.core.board.BoardManager;
import net.minegage.common.C;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.game.Game.GameState;
import net.minegage.minigame.game.event.GameStateChangeEvent;
import net.minegage.minigame.kit.Kit;
import net.minegage.minigame.kit.SelectKitEvent;
import net.minegage.minigame.map.MapToken;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;


/**
 * Handles lobby scoreboard. Is only loaded when the game is in the lobby.
 */
public class LobbyBoardManager
		extends BoardManager {
		
	private LobbyManager lobbyManager;
	
	// Placeholders
	private String header = "";
	private String playerCount = "";
	private String status = "";
	private String map = "";
	
	private int rowStatus;
	private int rowMap;
	private int rowKit;
	private int rowPlayers;
	
	public LobbyBoardManager(LobbyManager manager) {
		super(manager.getPlugin());
		
		this.lobbyManager = manager;
		setRankMode(true);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		giveBoard(player);
	}
	
	public void giveBoard(Player player) {
		Board board = new Board();
		
		ObjectiveSide side = board.setSideObjective();
		side.setHeader(header);
		
		side.addRow("");
		side.addRow(C.cGreen + C.cBold + "Status");
		rowStatus = side.addRow(status);
		side.addRow("");
		side.addRow(C.cYellow + C.cBold + "Players");
		rowPlayers = side.addRow(playerCount);
		side.addRow("");
		side.addRow(C.cGold + C.cBold + "Kit");
		rowKit = side.addRow("None");
		side.addRow("");
		side.addRow(C.cRed + C.cBold + "Map");
		rowMap = side.addRow(map);
		side.addRow("");
		side.addRow(C.cAqua + "minegage.net");
		
		setBoard(player, board);
	}
	
	@Override
	protected void onEnable() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			giveBoard(player);
		}

		setRankMode(true);
	}
	
	@Override
	protected void onDisable() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			removeBoard(player);
		}
	}
	
	@EventHandler
	public void updateMap(GameStateChangeEvent event) {
		if (event.getNewState() == GameState.WAITING) {
			updateHeader();
			updateStatus();
			updateCount();
		}
	}

	@EventHandler
	public void loadMap(WorldDataLoadEvent event) {
		Game game = getGame();
		if (game == null || game.getState() != GameState.LOADING) {
			return;
		}

		updateMap();
	}
	
	@EventHandler
	public void onKitSelect(SelectKitEvent event) {
		Player player = event.getPlayer();
		Kit kit = event.getKit();
		
		String name = kit.getName();
		getBoard(player).getSideObjective()
				.updateRow(rowKit, name);
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void updateStatus(TickEvent event) {
		if (event.getTick() != Tick.SEC_1) {
			return;
		}
		
		updateCount();
		updateStatus();
	}
	
	public void updateStatus() {
		Game game = getGame();
		
		// Changes the status depending on game state
		if (game == null) {
			status = "Loading game...";
		} else if (game.paused) {
			status = "Paused";
		} else {
			GameState state = game.getState();
			
			if (state == GameState.STARTING) {
				double stateSeconds = game.getStateSeconds();
				status = "Starting in " + (int) ( LobbyManager.START_SECONDS - stateSeconds );
			} else {
				status = "Waiting for players";
			}
		}
		
		for (Board board : getPlayerBoards()) {
			board.getSideObjective()
					.updateRow(rowStatus, status);
		}
	}
	
	
	public void updateCount() {
		Game game = getGame();
		if (game == null) {
			return;
		}
		
		int onlinePlayers = UtilServer.numPlayers();
		int max = game.maxPlayers;
		
		playerCount = onlinePlayers + "/" + max;

		for (Board board : getPlayerBoards()) {
			board.getSideObjective()
					.updateRow(rowPlayers, playerCount);
		}
	}
	
	public void updateHeader() {
		Game game = getGame();
		if (game == null) {
			return;
		}
		
		header = C.cGreen + C.cBold + game.getName();
		for (Board board : getPlayerBoards()) {
			board.getSideObjective()
					.setHeader(header);
		}
	}
	
	
	public void updateMap() {
		World map = getGame().getMap();
		MapToken token = getLobbyManager().getGameManager()
				.getMapManager()
				.getData(map);

		if (token == null) {
			return;
		}

		this.map = token.name;

		for (Board board : getPlayerBoards()) {
			board.getSideObjective()
					.updateRow(rowMap, this.map);
		}
		
	}
	
	public LobbyManager getLobbyManager() {
		return lobbyManager;
	}
	
	public Game getGame() {
		return lobbyManager.getGameManager()
				.getGame();
	}
	
	
	
}
