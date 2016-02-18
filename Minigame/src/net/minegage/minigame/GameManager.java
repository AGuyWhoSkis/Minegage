package net.minegage.minigame;


import com.google.common.collect.Lists;
import net.minegage.common.datafile.WorldDataLoadEvent;
import net.minegage.common.log.L;
import net.minegage.common.module.PluginModule;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilServer;
import net.minegage.common.util.UtilSound;
import net.minegage.common.util.UtilTime;
import net.minegage.common.util.UtilWorld;
import net.minegage.common.block.BlockManager;
import net.minegage.common.C;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.game.Game.GameState;
import net.minegage.minigame.game.GameToken;
import net.minegage.minigame.game.GameType;
import net.minegage.minigame.game.event.GameStateChangeEvent;
import net.minegage.minigame.kit.KitManager;
import net.minegage.minigame.lobby.LobbyManager;
import net.minegage.minigame.map.MapManager;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class GameManager
		extends PluginModule {
		
	public static final String FILE_NAME = "rotation.txt";
	
	private static final double LOAD_EXPIRE = 30.0;
	private static final double END_TIME = 12.0;
	
	private MinigameManager minigameManager;
	private RuleManager ruleManager;
	private KitManager kitManager;
	
	private Game game;
	
	public boolean tick = true;
	private String mapName;
	
	private List<GameType> gameRotation = Lists.newArrayList();
	
	// Index corresponds to how many games ago the map was played
	private LinkedList<GameToken> gameHistory = new LinkedList<>();
	
	// Maximum size of game history
	private final int HISTORY_SIZE = 10;
	
	public GameManager(MinigameManager minigameManager) {
		super("Game Manager", minigameManager);
		
		this.minigameManager = minigameManager;

		this.ruleManager = new RuleManager(this);
		this.kitManager = new KitManager(this);

		reloadRotations();

		runSyncDelayed(1L, new Runnable() {
			@Override
			public void run() {
				createNextGame();
			}
		});
	}

	/**
	 * Reloads game and map rotation from disk
	 */
	public void reloadRotations() {
		gameRotation.clear();
		
		try {
			File rotationFile = new File(Bukkit.getWorldContainer(), FILE_NAME);
			rotationFile.createNewFile();
			
			List<String> gameTypes = FileUtils.readLines(rotationFile);
			
			for (String type : gameTypes) {
				GameType gameType = UtilJava.parseEnum(GameType.class, type);
				if (gameType == null) {
					L.warn("Invalid game type \"" + type + "\" in rotation list; skipping");
				} else {
					gameRotation.add(gameType);
				}
			}
			
			if (gameRotation.size() == 0) {
				L.severe("No games in rotation! This is really bad");
			}
			
			String rotation = "";
			
			Iterator<GameType> gamesIt = gameRotation.iterator();
			if (!gamesIt.hasNext()) {
				rotation = "No games!";
			}
			
			while (gamesIt.hasNext()) {
				rotation += gamesIt.next()
						.getName();
				if (gamesIt.hasNext()) {
					rotation += ", ";
				}
			}
			
			L.info("Loaded game rotation: [" + rotation + "]" + " (" + gameRotation.size() + ")");
			
		} catch (IOException ex) {
			L.error(ex, "Unable to load game rotation");
		}
		
		getMapManager().loadMapRotation();
	}
	
	/**
	 * Behaviour for state changing based on other factors (player count, time passed, etc)
	 */
	@EventHandler
	public void tickState(TickEvent event) {
		if (!tick || event.getTick() != Tick.SEC_1) {
			return;
		}
		
		Game game = getGame();
		
		if (game == null) {
			return;
		}
		
		GameState state = game.getState();
		
		int online = UtilServer.numPlayers();
		int playing = game.getPlayersIn()
				.size();
				
		double stateSeconds = game.getStateSeconds();
		
		if (state == GameState.LOADING) {
			if (stateSeconds >= LOAD_EXPIRE) {
				L.severe("Game loading expired");
				game.setState(GameState.DEAD);
			}
			
		} else if (state == GameState.WAITING) {
			
			if (online >= game.minPlayers) {
				game.setState(GameState.STARTING);
			}
			
		} else if (state == GameState.STARTING) {
			
			// Reset game if there's not enough players
			if (online < game.minPlayersAbsolute) {
				game.skippedWaiting = false;
				game.setState(GameState.WAITING);
			} else if (!game.skippedWaiting && online >= game.maxPlayers) {
				game.setTicks(UtilTime.toTicks(LobbyManager.SKIP_SECONDS));
				game.skippedWaiting = true;
			} else if (stateSeconds >= LobbyManager.START_SECONDS) {
				game.setState(GameState.PLAYING);
			} else if (!game.paused && LobbyManager.START_SECONDS - stateSeconds < 10) {
				UtilSound.playGlobal(Sound.NOTE_PLING, 1F, 1F);
			}
			
		} else if (state == GameState.PLAYING) {
			
			if (!game.explaining && game.endCheck()) {
				game.setState(GameState.ENDING);
			} else if (game.timed && game.getRemainingSeconds() <= 0) {
				game.setState(GameState.ENDING);
			} else if (playing < game.minPlayersAbsolute) {
				C.bWarn("Game", "Not enough players are online to play. Please wait for more players.");
				game.setState(GameState.DEAD);
			}
			
		} else if (state == GameState.ENDING) {
			if (stateSeconds > END_TIME) {
				game.setState(GameState.DEAD);
			}
		}
	}
	
	/**
	 * Determines what happens when the game state is changed. Monitor priority is used so that
	 * states are changed last, and by extension state change events with a lower priority happen in
	 * the correct order
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void handleStateChange(GameStateChangeEvent event) {
		GameState newState = event.getNewState();
		
		if (newState == GameState.LOADING) {
			// Reload map rotation
			getMapManager().loadMapRotation();
			
			GameType type = game.getType();
			
			try {
				game.load(mapName);
				
				if (game.lobby) {
					game.setState(GameState.WAITING);
				} else {
					game.setState(GameState.PLAYING);
				}
				
			} catch (IOException ex) {
				L.error(ex, "Unable to load map \"" + mapName + "\" for " + type.getName());
			}
		} else if (newState == GameState.PLAYING) {
			List<Player> players = UtilServer.playersList();
			Collections.shuffle(players);

			for (Player player : players) {
				game.assignTeam(player);
				game.respawn(player);
			}
			
			game.start();
			
		} else if (newState == GameState.ENDING) {
			game.end();
		} else if (newState == GameState.DEAD) {
			// Unload and delete all worlds except for main
			
			killGame();
			createNextGame();
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void loadMapData(WorldDataLoadEvent event) {
		if (game == null) {
			return;
		}

		World world = event.getWorld();
		if (mapName != null && mapName.equals(world.getName())) {
			game.loadWorldData(event.getFile());
		}

	}
	
	public List<String> getMapHistory() {
		return gameHistory.stream()
				.map(token -> token.map)
				.collect(Collectors.toList());
	}
	
	public List<String> getMapHistory(GameType type) {
		return gameHistory.stream()
				.filter(token -> ( token.type == type ))
				.map(token -> token.map)
				.collect(Collectors.toList());
	}
	
	public List<GameType> getGameHistory() {
		return gameHistory.stream()
				.map(token -> token.type)
				.collect(Collectors.toList());
	}
	
	public void killGame() {
		World main = UtilWorld.getMainWorld();
		
		for (World world : Bukkit.getWorlds()) {
			if (UtilWorld.isMainWorld(world)) {
				continue;
			}

			for (Player player : world.getPlayers()) {
				player.teleport(main.getSpawnLocation());
			}
			
			UtilWorld.forceUnload(world, false);
		}
		
		for (String world : UtilWorld.getUnloadedWorlds()) {
			if (UtilWorld.isMainWorld(world)) {
				continue;
			}
			
			try {
				UtilWorld.delete(world);
			} catch (IOException ex) {
				L.error(ex, "Unable to delete world \"" + world + "\"");
			}
		}
		
		if (game != null) {
			game.disable();
			game = null;
		}
		
		this.mapName = null;
	}
	
	/**
	 * Creates a game of the least played gametype, on the least played map
	 */
	public void createNextGame() {
		GameType type = nextGameType();
		createGame(type, nextMap(type));
	}
	
	public void createGame(GameType type) {
		createGame(type, nextMap(type));
	}
	
	public void createGame(GameType type, String map) {
		if (game != null) {
			killGame();
		}
		
		game = initGame(type, map);
		if (game == null) {
			return;
		}
		
		this.mapName = map;
		
		game.setState(GameState.LOADING);
		kitManager.restore();
		kitManager.buildLobbyKitStands();
	}
	
	private String nextMap(GameType type) {
		List<String> maps = Lists.newArrayList(getMapManager().getMapRotation()
				.get(type));
				
		if (maps.isEmpty()) {
			L.severe("No maps in rotation; cannot create game");
			return null;
		}
		
		List<String> prevMaps = getMapHistory(type);
		if (prevMaps.size() > 0 && maps.size() > 1) {
			maps.remove(prevMaps.get(0));
		}

		Collections.shuffle(maps);
		
		return maps.get(0);
	}
	
	/**
	 * Instantiates a Game of the given type using the given map
	 */
	private Game initGame(GameType type, String map) {
		Class<? extends Game> clazz = type.getClazz();
		
		try {
			Constructor<? extends Game> struct = clazz.getConstructor(MinigameManager.class);
			struct.setAccessible(true);
			
			Game game = struct.newInstance(minigameManager);
			
			gameHistory.addFirst(new GameToken(type, map));
			if (gameHistory.size() > HISTORY_SIZE) {
				gameHistory.removeLast();
			}
			
			return game;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException ex) {
			L.error(ex, "Failed to instantiate game");

			return null;
		} catch (InvocationTargetException ex) {
			Throwable target = ex.getTargetException();
			L.error(target, "Failed to instantiate game");
			return null;
		}
	}
	
	private GameType nextGameType() {
		if (gameHistory.size() == 0) {
			return UtilJava.getRandIndex(gameRotation);
		}
		
		GameType lastType = gameHistory.get(0).type;
		int nextIndex = gameRotation.indexOf(lastType) + 1;
		
		return UtilJava.getWrappedIndex(gameRotation, nextIndex);
	}
	
	public List<GameType> getGameRotation() {
		return gameRotation;
	}
	
	public Game getGame() {
		return game;
	}
	
	public MinigameManager getMinigameManager() {
		return minigameManager;
	}
	
	public RuleManager getRuleManager() {
		return ruleManager;
	}
	
	public Minigame getMinigame() {
		return getMinigameManager().getMinigame();
	}
	
	public MapManager getMapManager() {
		return getMinigameManager().getMapManager();
	}
	
	public BlockManager getFallingBlockManager() {
		return getMinigameManager().getBlockManager();
	}
	
}

