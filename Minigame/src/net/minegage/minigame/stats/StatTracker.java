package net.minegage.minigame.stats;


import com.google.common.collect.Lists;
import net.minegage.common.java.SafeMap;
import net.minegage.common.module.PluginModule;
import net.minegage.common.util.UtilEvent;
import net.minegage.core.combat.KillAssistEvent;
import net.minegage.core.combat.event.CombatDeathEvent;
import net.minegage.core.stats.StatManager;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.game.Game.GameState;
import net.minegage.minigame.team.GameTeam;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;


/**
 * Acts as an interface for StatManager, but also caches stats on a per game basis. Not limited to
 * thing like kills, deaths, etc; this can be used for storing any information relating to a player.
 */
public class StatTracker
		extends PluginModule {

	private List<String> sqlStats;

	private SafeMap<GameTeam, SafeMap<String, Integer>> teamStats = new SafeMap<>();
	private SafeMap<Player, SafeMap<String, Integer>> playerStats = new SafeMap<>();

	private SafeMap<String, Integer> defaultTeamValues = new SafeMap<>();
	private SafeMap<String, Integer> defaultPlayerValues = new SafeMap<>();
	
	private String table;
	private Game game;
	
	public StatTracker(Game game, String table, String... statNames) {
		super("Stat Tracker", game);
		
		this.game = game;
		this.table = table;
		sqlStats = Lists.newArrayList(statNames);
		
		StatManager statManager = StatManager.instance;
		statManager.addTable(table, sqlStats);
	}
	
	@Override
	protected void onEnable() {
		StatManager statManager = StatManager.instance;
		statManager.scheduleSaveTask();
	}
	
	@Override
	protected void onDisable() {
		StatManager statManager = StatManager.instance;
		statManager.removeTable(table);
		playerStats.clear();
		teamStats.clear();
		defaultTeamValues.clear();
		defaultPlayerValues.clear();
	}

	public void setDefaultTeamValue(String stat, int value) {
		defaultTeamValues.put(stat, value);
	}

	public void setDefaultPlayerValue(String stat, int value) {
		defaultPlayerValues.put(stat, value);
	}

	public SafeMap<String, Integer> getStats(GameTeam team) {
		SafeMap<String, Integer> stats = teamStats.get(team);
		if (stats == null) {
			stats = new SafeMap<>();
			teamStats.put(team, stats);
		}
		return stats;
	}
	
	public SafeMap<String, Integer> getStats(Player player) {
		SafeMap<String, Integer> stats = playerStats.get(player);
		if (stats == null) {
			stats = new SafeMap<>();
			playerStats.put(player, stats);
		}
		
		return stats;
	}

	public Integer get(Player player, String stat) {
		int defaultValue = defaultPlayerValues.getOrDefault(stat, 0);
		return getStats(player).getOrDefault(stat, defaultValue);
	}
	
	public Integer get(GameTeam team, String stat) {
		int defaultValue = defaultTeamValues.getOrDefault(stat, 0);
		return getStats(team).getOrDefault(stat, defaultValue);
	}

	public void set(Player player, String stat, int value) {
		int oldValue = get(player, stat);
		getStats(player).put(stat, value);
		
		UpdatePlayerStatEvent event = new UpdatePlayerStatEvent(player, stat, oldValue, value);
		UtilEvent.call(event);
		
		int difference = value - oldValue;
		GameTeam team = game.getTeam(player);
		increment(team, stat, difference);
	}
	
	public void set(GameTeam team, String stat, int value) {
		int oldValue = get(team, stat);
		
		getStats(team).put(stat, value);
		
		UpdateTeamStatEvent event = new UpdateTeamStatEvent(team, stat, oldValue, value);
		UtilEvent.call(event);
	}

	public int increment(Player player, String stat, int increment) {
		int oldValue = get(player, stat);
		int newValue = oldValue + increment;
		set(player, stat, newValue);

		increment(game.getTeam(player), stat, increment);
		return newValue;
	}
	
	public int increment(GameTeam team, String stat, int increment) {
		int value = get(team, stat);
		value += increment;
		set(team, stat, value);
		
		return value;
	}

	public void reset(Player player, String stat) {
		set(player, stat, defaultPlayerValues.getOrDefault(stat, 0));
	}

	public void reset(GameTeam team, String stat) {
		set(team, stat, defaultTeamValues.getOrDefault(stat, 0));
	}

	/* Default stat tracking */
	@EventHandler(priority = EventPriority.LOWEST)
	public void recordCombat(CombatDeathEvent event) {
		if (!isRecording() || !event.isPlayerKilled()) {
			return;
		}
		
		Player killed = event.getKilledPlayer();
		
		if (game.canInteract(killed)) {
			increment(killed, Stat.DEATHS, 1);
		}
		
		if (event.isPlayerKiller()) {
			Player pKiller = event.getKillerPlayer();
			increment(pKiller, Stat.KILLS, 1);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void recordAssist(KillAssistEvent event) {
		if (!isRecording()) {
			return;
		}

		OfflinePlayer offPlayer = event.getPlayer();
		if (!offPlayer.isOnline()) {
			return;
		}
		
		Player player = offPlayer.getPlayer();
		increment(player, Stat.ASSISTS, 1);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		playerStats.put(player, new SafeMap<>());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void clearReference(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		playerStats.remove(player);
	}

	private boolean isRecording() {
		return game.getState() == GameState.PLAYING;
	}

	public List<String> getSqlStats() {
		return sqlStats;
	}
	
}
