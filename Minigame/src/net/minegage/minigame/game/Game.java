package net.minegage.minigame.game;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minegage.common.C;
import net.minegage.common.block.BlockManager;
import net.minegage.common.board.Board;
import net.minegage.common.datafile.DataFile;
import net.minegage.common.java.SafeMap;
import net.minegage.common.log.L;
import net.minegage.common.misc.Ordinal;
import net.minegage.common.module.PluginModule;
import net.minegage.common.server.ServerManager;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilMath;
import net.minegage.common.util.UtilPlayer;
import net.minegage.common.util.UtilServer;
import net.minegage.common.util.UtilSound;
import net.minegage.common.util.UtilTime;
import net.minegage.common.util.UtilUI;
import net.minegage.common.util.UtilWorld;
import net.minegage.common.util.UtilZip;
import net.minegage.core.combat.DeathMessenger.DeathMessageMode;
import net.minegage.core.condition.VisibilityManager;
import net.minegage.core.spawn.SpawnManager;
import net.minegage.minigame.GameManager;
import net.minegage.minigame.MinigameManager;
import net.minegage.minigame.board.GameBoardManager;
import net.minegage.minigame.game.event.GameStateChangeEvent;
import net.minegage.minigame.game.event.PlayerStateChangeEvent;
import net.minegage.minigame.item.ItemManager;
import net.minegage.minigame.kit.Kit;
import net.minegage.minigame.stats.Stat;
import net.minegage.minigame.stats.StatTracker;
import net.minegage.minigame.team.GameTeam;
import net.minegage.minigame.winnable.Winnable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;


public abstract class Game
		extends PluginModule {

	public static String DATA_FILE = "mapdata.dat";

	protected GameManager gameManager;
	protected GameBoardManager boardManager;

	protected GameType type;
	protected String[] description;
	protected List<Kit> globalKits;
	public Kit defaultKit = null;

	public boolean teamUniqueKits = false;

	protected SafeMap<Player, PlayerState> states = new SafeMap<>();
	private List<GameTeam> teams = Lists.newArrayList();
	private SafeMap<Player, Kit> selectedKits = new SafeMap<>();
	protected StatTracker stats;

	public boolean paused = false;
	protected int totalTicks = 0;
	protected int stateTicks = 0;

	protected String mapName;

	public boolean spawnCommand = false;

	public boolean lobby = true;
	public boolean skippedWaiting = false;

	/* Players required to start the game */
	public int minPlayers = 2;
	/* Players required to play the game - game will end when player count is less than this */
	public int minPlayersAbsolute = 1; // TODO: Change to 2 for release
	/* When there are this many players, players without permissions will not be allowed to join */
	public int maxPlayers = 12;
	/* When there are this many players, player which are not admin will not be allowed to join */
	public int maxPlayersAbsolute = 20;

	public boolean joinMessageLive = true;
	public boolean quitMessageLive = true;

	/* Enviro is a damage source which isn't a player */
	public boolean damage = true;
	public boolean damageVsPlayer = true;
	public boolean damageVsEnviro = true;

	public boolean damagePlayerVsPlayer = true;
	public boolean damageEnviroVsPlayer = true;

	public boolean damageEnviroVsEnviro = true;
	public boolean damagePlayerVsEnviro = true;

	public boolean damageFallVsPlayer = false;
	public boolean damageFallVsEnviro = true;

	public boolean damagePlayerVsSelf = false;
	public boolean damagePlayerVsSelfTeam = false;

	public DeathMessageMode deathMessageMode = DeathMessageMode.ALL;

	public int invincibleRespawnTicks = 20;

	public boolean projectiles = true;
	public boolean removeArrows = true;

	public boolean timed = false;
	public double timeLimit = 60 * 3;

	public boolean hunger = false;
	public float spawnSaturation = 5.0F;
	public float spawnExhaustion = 0.0F;
	public int spawnHunger = 20;

	public boolean blockBreak = false;
	public Set<MaterialData> blockBreakAllow = Sets.newHashSet();
	public Set<MaterialData> blockBreakDeny = Sets.newHashSet();

	public boolean blockPlace = false;
	public Set<MaterialData> blockPlaceAllow = Sets.newHashSet();
	public Set<MaterialData> blockPlaceDeny = Sets.newHashSet();

	public boolean explosions = true;
	public boolean explodeRegen = false;
	public boolean explodeDebris = true;

	public boolean itemDropDeath = false;
	public boolean itemDrop = false;
	public boolean arrowStay = false;
	public Set<MaterialData> itemDropAllow = Sets.newHashSet();
	public Set<MaterialData> itemDropDeny = Sets.newHashSet();

	public boolean armourMove = false;
	public boolean itemMove = true;
	public Set<MaterialData> itemMoveAllow = Sets.newHashSet();
	public Set<MaterialData> itemMoveDeny = Sets.newHashSet();

	public boolean itemPickup = false;
	public Set<MaterialData> itemTakeAllow = Sets.newHashSet();
	public Set<MaterialData> itemPickupDeny = Sets.newHashSet();

	public SafeMap<Location, Double> safeZones = new SafeMap<>();

	public boolean itemDamage = true;

	public boolean inAllowFlight = false;
	public boolean inFlight = false;
	public boolean boundaryDamage = true;

	public boolean deathSpecItem = true;
	public boolean deathOut = true;
	public int deathOutCount = 1;

	/* If true, sets player state to OUT when joining a game in progress */
	public boolean joinOut = true;

	protected SafeMap<Player, Integer> respawnTimes = new SafeMap<>();

	private GameState state = null;

	public boolean weather = false;

	public boolean explain = true;
	public boolean explainFreeze = true;
	public double explainTime = 10.0;
	public boolean explainBlockEdit = false;

	public boolean explaining = false;

	public Set<InventoryType> restrictedMenus = Sets.newHashSet();

	public Game(MinigameManager manager, GameType type, String[] description, Kit... kits) {
		super(type.getName(), manager);

		this.gameManager = manager.getGameManager();
		this.type = type;
		this.description = description;

		this.globalKits = Lists.newArrayList(kits);
		for (Kit kit : kits) {
			if (defaultKit == null) {
				defaultKit = kit;
			}

			kit.setGame(this);
		}

		createTeams();
		createTeam("Spectator", C.cGray).setVisible(false);

		this.stats = new StatTracker(this, getStatTable(), getStats());
		this.boardManager = new GameBoardManager(this);

		for (Player player : Bukkit.getOnlinePlayers()) {
			setState(player, PlayerState.IN);
			giveSpectator(player, false);
		}
	}

	@Override
	protected void onDisable() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			clearReference(player);
		}

		// Unregister kits and attributes
		for (Kit kit : globalKits) {
			kit.dispose();
		}

		for (GameTeam team : getTeams()) {
			team.dispose();
		}

		if (stats != null) {
			stats.disable();
		}
		if (boardManager != null) {
			boardManager.disable();
		}
	}


	public abstract boolean endCheck();

	public abstract void createTeams();

	/**
	 Called when a player joins late, and {@link Game#joinOut} is false
	 */
	public abstract void assignTeam(Player player);

	protected abstract List<Winnable<?>> getWinnerPlaces();

	/**
	 Loads all relevant information from the specified world. Assumes that the world file exists

	 @param mapName The world name to load from the global world folder
	 */
	public void load(String mapName)
			throws IOException {
		if (mapName == null) {
			throw new NullPointerException("Map cannot be null");
		}

		this.mapName = mapName;

		File zip       = ServerManager.getMapZip(type.getName(), mapName);
		File worldFile = new File(Bukkit.getWorldContainer(), mapName);

		UtilZip.extract(zip, worldFile);

		World world    = UtilWorld.load(mapName);
		File  worldDir = world.getWorldFolder();
	}

	public void loadWorldData(DataFile worldData) {
		World world = getMap();

		for (GameTeam team : getTeams()) {
			if (!team.isVisible()) {
				continue;
			}

			List<Location> spawns = worldData.read("spawns," + team.getName()
					.toLowerCase())
					.asLocations(world);

			team.getSpawns()
					.addAll(spawns);
		}
	}

	@EventHandler
	public final void gameTick(TickEvent event) {
		if (event.isNot(Tick.TICK_1)) {
			return;
		}

		totalTicks += 1;
		if (!paused) {
			stateTicks += 1;
		}

		// Copy to prevent concurrent modification exceptions
		SafeMap<Player, Integer> respawnTimesCopy = new SafeMap<>(respawnTimes);

		for (Entry<Player, Integer> entry : respawnTimesCopy.entrySet()) {
			Player  player       = entry.getKey();
			Integer respawnTicks = entry.getValue();

			int timeLeft = UtilTime.timeLeft(respawnTicks);

			if (timeLeft <= 0) {
				respawn(player);
			} else {
				double respawnSeconds = UtilTime.toSeconds(timeLeft);
				sendRespawnTitle(player, respawnSeconds);
			}
		}

	}

	public void sendRespawnTitle(Player player, double respawnSeconds) {
		if (getState() == GameState.PLAYING && respawnSeconds > 0) {
			respawnSeconds = UtilMath.round(respawnSeconds, 1);
			UtilUI.sendSubtitle(player, C.cGreen + "Respawning in " + respawnSeconds + "s...");
		}
	}

	/**
	 Called when state is changed to {@link GameState#PLAYING}
	 */
	public void start() {
		if (explain) {
			explaining = true;

			for (Player player : getPlayersIn()) {
				C.pRaw(player, "");
				C.pRaw(player, "");
				C.pGeneral(player, C.cBold + "Game", C.cGreen + C.cBold + getName());
				C.pRaw(player, "");
				for (String str : description) {
					C.pRaw(player, C.t1 + str);
				}
				C.pRaw(player, "");
			}

			int freezeTicks = UtilTime.toTicks(explainTime);
			int startTicks  = UtilServer.currentTick();

			runSyncTimer(0L, 1L, new BukkitRunnable() {
				int tick = 0;

				@Override
				public void run() {
					tick++;

					int    passedTicks   = UtilServer.currentTick() - startTicks;
					double secondsPassed = UtilTime.toSeconds(passedTicks);

					double displaySeconds = UtilMath.round(explainTime - secondsPassed, 1);
					String message        =
							C.cBold + "Game starts in " + C.sOut + C.cBold + displaySeconds + "s" + C.cWhite + C.cBold +
							"...";

					if (tick > freezeTicks) {
						UtilSound.playGlobal(Sound.NOTE_PLING, 1F, net.minegage.common.misc.Note.O3_D);
						explaining = false;
						this.cancel();
					} else {
						if (secondsPassed != 0.0 && displaySeconds % 1 == 0) {
							UtilSound.playGlobal(Sound.NOTE_STICKS, 1F, 1.2F);
						}
					}

					for (Player player : UtilServer.players()) {
						UtilUI.sendTitles(player, "", message, 0, 5, 10);
					}
				}
			});
		}
	}

	public void end() {
		gameManager.getMinigame().getChatManager().silence(5000L);

		List<Winnable<?>> winners = getWinnerPlaces();

		C.bRaw("");
		C.bRaw("");

		String title;
		String subtitle;
		if (winners == null || winners.size() == 0) {
			title = "Nobody won!";
			subtitle = " ";
			C.bRaw("Nobody won!");
		} else {
			Winnable<?> winner = winners.get(0);

			String colour = winner.getColour();

			title = colour + winner.getName();
			subtitle = colour + "won the game!";

			if (winners.size() > 1) {
				List<String> colours = C.rainbow(C.cGreenD);
				Collections.reverse(colours);

				int numWinners = Math.min(3, winners.size());

				for (int i = 0; i < numWinners; i++) {
					String name = winners.get(i)
							.getName();
					String placeColour = UtilJava.getWrappedIndex(colours, i) + C.cBold;
					String placeWord   = Ordinal.shortForm(i + 1);

					String outPrefix = placeColour + placeWord;
					String outName   = placeColour + name;

					String out = C.t1 + outPrefix + " - " + outName;

					C.bRaw(out);
				}
			}
		}

		C.bRaw("");
		C.bRaw("");

		UtilServer.players()
				.forEach(player -> UtilUI.sendTitles(player, title, subtitle, 5, 100, 10));

		if (winners != null) {
			recordWinLossStats(winners.stream()
					                   .findFirst()
					                   .orElse(null));
		}
	}

	private void recordWinLossStats(Winnable<?> winner) {
		if (winner == null) {
			return;
		}

		Set<Player> winners = winner.getPlayers();

		if (winners == null) {
			return;
		}

		for (Player player : winners) {
			stats.increment(player, Stat.WINS, 1);
		}

		Set<Player> losers = UtilServer.playersSet();
		losers.removeAll(winners);

		for (Player player : losers) {
			stats.increment(player, Stat.LOSSES, 1);
		}
	}

	/**
	 Resets the inventory, hunger, potion effects, fly/walk speed, visibility, etc. Teleports the player to respawn
	 point
	 */
	public void respawn(Player player) {
		L.d("respawning " + player.getName());
		if (inLobby()) {
			player.teleport(UtilWorld.getMainWorld()
					                .getSpawnLocation());
			return;
		}

		Location respawnLoc = null;
		GameTeam team       = getTeam(player);
		if (team == null) {
			respawnLoc = getSpecSpawn();
		} else {
			respawnLoc = team.nextSpawn();
		}

		if (respawnLoc == null) {
			respawnLoc = player.getWorld()
					.getSpawnLocation();
		}

		// Vanish before teleporting to avoid weird teleport/flying animation
		VisibilityManager vis = gameManager.getMinigame()
				.getVisibilityManager();
		vis.setVanished(player, true);

		player.teleport(respawnLoc);

		if (getState(player) == PlayerState.IN) {
			giveSpectator(player, false);

			Kit kit = getKit(player);
			if (kit != null) {
				kit.equip(player);
			}

			player.setNoDamageTicks(invincibleRespawnTicks);
		}

		respawnTimes.remove(player);
	}

	public void respawn(Player player, double respawnSeconds) {
		giveDeathEffects(player);

		int ticks = UtilTime.toTicks(respawnSeconds);
		int time  = UtilServer.currentTick() + ticks;
		respawnTimes.put(player, time);

		int titleTime = Math.min(ticks - 10, 50);

		UtilUI.sendTitle(player, C.cRed + "You died!", 0, titleTime, 10);
		sendRespawnTitle(player, respawnSeconds);
	}

	private PotionEffect respawnBlind = new PotionEffect(PotionEffectType.BLINDNESS, 30, 0, false, false);
	private PotionEffect respawnSlow = new PotionEffect(PotionEffectType.SLOW, 20, 0, false, false);

	public void giveDeathEffects(Player player) {
		giveSpectator(player, true);

		if (deathSpecItem) {
			player.getInventory()
					.setItem(4, ItemManager.specItem);
		}

		player.setVelocity(new Vector(0, 0.5, 0));

		respawnBlind.apply(player);
		respawnSlow.apply(player);
	}

	public void out(Player player, boolean death) {
		if (death) {
			giveDeathEffects(player);
			UtilUI.sendTitles(player, C.cRed + "You died!", C.cGold + C.cBold + "You will respawn next game!", 0, 60,
			                  10);
		} else {
			GameTeam specTeam = getSpectatorTeam();
			specTeam.addPlayer(player);

			giveSpectator(player, true);
		}

		player.getInventory()
				.setItem(8, ItemManager.lobbyItem);

		setState(player, PlayerState.OUT);
	}

	public void giveSpectator(Player player, boolean spectating) {
		UtilPlayer.reset(player);

		if (spectating) {
			player.setAllowFlight(true);
			player.setFlying(true);

			// Remove the player as a target to creatures
			for (Entity entity : player.getWorld()
					.getEntities()) {

				if (entity instanceof Creature) {
					Creature creature = (Creature) entity;

					if (creature.getTarget() != null && creature.getTarget()
							.equals(player)) {
						creature.setTarget(null);
					}
				}
			}


		} else {
			player.setAllowFlight(inAllowFlight);
			player.setFlying(inFlight);
		}

		UtilPlayer.setCollides(player, !spectating);

		gameManager.getMinigame()
				.getVisibilityManager()
				.setVanished(player, spectating);
	}

	public StatTracker getStatTracker() {
		return stats;
	}

	public List<Player> getPlayers(PlayerState state) {
		return UtilServer.players()
				.stream()
				.filter(player -> getState(player) == state)
				.collect(Collectors.toList());
	}

	public List<Player> getPlayersIn() {
		return getPlayers(PlayerState.IN);
	}

	public List<Player> getPlayersOut() {
		return getPlayers(PlayerState.OUT);
	}

	public List<Player> getPlayersNotSpectating() {
		return UtilServer.players()
				.stream()
				.filter(player -> !isSpectating(player))
				.collect(Collectors.toList());
	}

	public String[] getStats() {
		return new String[] {Stat.WINS, Stat.LOSSES, Stat.EARNED};
	}

	public List<GameTeam> getTeams() {
		return teams;
	}

	public List<GameTeam> getTeamsVisible() {
		return getTeams().stream()
				.filter(GameTeam::isVisible)
				.collect(Collectors.toList());
	}

	public GameTeam getSpectatorTeam() {
		return getTeam("Spectator");
	}

	public GameTeam createTeam(String name, String prefix, Kit... kits) {
		GameTeam team = new GameTeam(this, name, prefix, kits);
		teams.add(team);
		return team;
	}

	/**
	 @return The GameTeam with the specified name. Case sensitive.
	 */
	public GameTeam getTeamExact(String name) {
		return teams.stream()
				.filter(team -> team.getName()
						.equals(name))
				.findFirst()
				.orElse(null);
	}

	/**
	 @return The GameTeam with the specified name. Case insensitive.
	 */
	public GameTeam getTeam(String name) {
		return teams.stream()
				.filter(team -> team.getName()
						.equalsIgnoreCase(name))
				.findFirst()
				.orElse(null);
	}

	public GameTeam getTeam(Player player) {
		return teams.stream()
				.filter(team -> team.getPlayers()
						.contains(player))
				.findFirst()
				.orElse(null);
	}

	/**
	 @return Tick value since the last GameState change
	 */
	public int getStateTicks() {
		return stateTicks;
	}

	public double getStateSeconds() {
		return UtilTime.toSeconds(getStateTicks());
	}

	public double getRemainingSeconds() {
		double playTime = timeLimit;

		if (explain) {
			if (explaining) {
				playTime -= explainTime;
			}
		}

		return playTime - getStateSeconds();
	}

	public void setTicks(int ticks) {
		this.totalTicks = ticks;
	}

	public boolean isRespawning(Player player) {
		return respawnTimes.containsKey(player);
	}

	public boolean isSpectating(Player player) {
		return getState(player) == PlayerState.OUT || isRespawning(player);
	}

	public boolean canInteract(Player player) {
		return !isSpectating(player) && !(explainFreeze && explaining);

	}

	public Location getSpecSpawn() {
		return SpawnManager.instance.getSpawnpoint(getMap());
	}

	public List<Kit> getGlobalKits() {
		return globalKits;
	}

	public Kit getKit(String name) {
		if (teamUniqueKits) {
			for (GameTeam team : getTeams()) {
				Kit kit = team.getKit(name);
				if (kit != null) {
					return kit;
				}
			}

			return null;
		} else {
			return globalKits.stream()
					.filter(kit -> kit.getName()
							.equalsIgnoreCase(name))
					.findFirst()
					.orElse(null);
		}
	}

	public Kit getKit(Player player) {
		if (teamUniqueKits) {
			GameTeam team = getTeam(player);
			if (team == null) {
				return null;
			}

			Kit kit = team.getKit(player);

			if (kit == null) {
				kit = team.getKits().get(0);
			}

			return kit;
		} else {
			Kit kit = selectedKits.get(player);

			if (kit == null) {
				kit = getGlobalKits().get(0);
			}

			return kit;
		}


	}

	public void setKit(Player player, Kit kit) {
		if (teamUniqueKits) {

			for (GameTeam team : getTeams()) {
				if (team.getKits()
						.contains(kit)) {
					team.setKit(player, kit);
					return;
				}
			}

			throw new NullPointerException("Can't find host of kit " + kit.getName());

		} else {
			selectedKits.put(player, kit);
		}
	}



	public void clearReference(Player player) {
		for (GameTeam team : teams) {
			team.removePlayer(player);
		}

		selectedKits.remove(player);
		states.remove(player);
		respawnTimes.remove(player);
	}

	public void setState(final GameState state) {
		final GameState oldState = this.state;
		this.state = state;
		this.stateTicks = 0;

		if (state == GameState.ENDING) {
			L.d("ending game");
		}

		GameStateChangeEvent event = new GameStateChangeEvent(oldState, state);
		UtilEvent.call(event);
	}

	public GameState getState() {
		return state;
	}

	public boolean inLobby() {
		return !inMap();
	}

	public boolean inMap() {
		return getState() == GameState.PLAYING || getState() == GameState.ENDING;
	}

	public boolean isPlaying() {
		return getState() == GameState.PLAYING;
	}

	public SafeMap<Player, PlayerState> getPlayerStates() {
		return states;
	}

	public PlayerState getState(Player player) {
		return getPlayerStates().get(player);
	}

	public void setState(Player player, PlayerState state) {
		PlayerState prevState = states.get(player);
		getPlayerStates().put(player, state);

		PlayerStateChangeEvent event = new PlayerStateChangeEvent(player, prevState, state);
		UtilEvent.call(event);
	}

	public void addSafezone(Location location, double radius) {
		safeZones.put(location, radius);
	}

	public GameType getType() {
		return type;
	}

	@Override
	public String getName() {
		return getType().getName();
	}

	public BlockManager getFallingBlockManager() {
		return gameManager.getFallingBlockManager();
	}

	public World getMap() {
		return Bukkit.getWorld(mapName);
	}

	public GameBoardManager getBoardManager() {
		return boardManager;
	}

	public void giveBoard(Player player, Board board) {
		// Optional override
	}

	public enum GameState {
		LOADING,
		WAITING,
		STARTING,
		PLAYING,
		ENDING,
		DEAD
	}


	public enum PlayerState {
		IN,
		OUT
	}

	public String getStatTable() {
		return getName().replaceAll(" ", "_");
	}

}
