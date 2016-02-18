
package net.minegage.hub;


import net.minegage.common.log.L;
import net.minegage.common.module.PluginModule;
import net.minegage.common.server.ServerManager;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.util.UtilEntity;
import net.minegage.common.util.UtilServer;
import net.minegage.common.util.UtilSound;
import net.minegage.common.util.UtilUI;
import net.minegage.common.util.UtilZip;
import net.minegage.common.C;
import net.minegage.core.condition.VisibilityManager;
import net.minegage.core.combat.event.CombatDeathEvent;
import net.minegage.core.rank.Rank;
import net.minegage.core.rank.RankManager;
import net.minegage.hub.board.HubBoardManager;
import net.minegage.hub.command.CommandDebug;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class HubManager
		extends PluginModule {
		
	/* Players which have reduced visibility (can't see non-donators) */
	private Set<Player> vanishedOthers = new HashSet<>();
	
	private HubPlugin hub;
	private HubBoardManager boardManager;
	
	public HubManager(HubPlugin hub) {
		super("Hub Manager", hub);

		this.hub = hub;
		this.boardManager = new HubBoardManager(plugin);

		addCommand(new CommandDebug(hub));

		setupLobby();
	}

	private void setupLobby() {
		// Unzip lobby to world directory
		
		logInfo("Extracting lobby zip...");
		
		File worldsDir = ServerManager.getWorldsDir();
		File hubZip = new File(worldsDir, "hublobby.zip");
		
		if (!hubZip.exists()) {
			L.severe("Hub lobby file \"" + hubZip.getAbsolutePath() + "\" not found!");
			return;
		}
		
		File lobbyDir = new File(Bukkit.getWorldContainer(), "world");
		try {
			UtilZip.extract(hubZip, lobbyDir);
		} catch (IOException ex) {
			L.error(ex, "Unable to extract hub lobby");
		}
	}
	
	@EventHandler
	public void onTrade(InventoryOpenEvent event) {
		if (event.getInventory()
				.getType() == InventoryType.MERCHANT) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void preventStorm(WeatherChangeEvent event) {
		if (event.toWeatherState()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void preventHunger(TickEvent event) {
		if (event.getTick() != Tick.MIN_1) {
			return;
		}
		
		for (Player player : plugin.getServer()
				.getOnlinePlayers()) {
			player.setFoodLevel(20);
			player.setSaturation(20F);
			player.setExhaustion(0F);
		}
	}
	
	@EventHandler
	public void handleJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Player player = event.getPlayer();
		
		for (Player other : UtilServer.playersList()) {
			player.showPlayer(other);
		}
		
		// If this player is not a donator, hide them from players with the reduced player setting
		if (isVanishable(player)) {
			for (Player other : vanishedOthers) {
				other.hidePlayer(player);
			}
		}
		
		boolean allowFlight = RankManager.instance.hasPermission(player, Rank.MVP);
		player.setAllowFlight(allowFlight);

		UtilEntity.clearPotionEffects(player);
		
		UtilUI.sendTabText(player, C.cBold + "Minegage", " " + C.cBold + "Check out " + C.sOut + C.cBold + "www.minegage.net" + C.cWhite + C.cBold
		                                                 + " for shop, forums, and more! ");
	}
	
	@EventHandler
	public void handleQuit(PlayerQuitEvent event) {
		event.setQuitMessage(null);
		vanishedOthers.remove(event.getPlayer());
	}
	
	@EventHandler
	public void handleDeath(CombatDeathEvent event) {
		event.setRespawnLocation(event.getKilled()
				.getWorld()
				.getSpawnLocation());
				
		// Prevent hurt sound from playing
		event.getCause()
				.setCancelled(true);
	}
	
	/* Vanish handling */
	
	public boolean isVanishable(Player player) {
		return !RankManager.instance.hasPermission(player, Rank.PRO);
	}
	
	public void vanishOthers(Player player) {
		for (Player other : getServer().getOnlinePlayers()) {
			if (isVanishable(other)) {
				player.hidePlayer(other);
			}
		}
		
		vanishedOthers.add(player);
		C.pMain(player, "Visibility", "Players are now " + C.sOut + "reduced");
	}
	
	public void unvanishOthers(Player player) {
		for (Player other : player.spigot()
				.getHiddenPlayers()) {
			player.showPlayer(other);
		}
		
		vanishedOthers.remove(player);
		C.pMain(player, "Visibility", "Players are now " + C.sOut + "visible");
	}
	
	/* General rules */
	
	@EventHandler
	public void onVoidDamage(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		
		if (entity.getType() != EntityType.PLAYER) {
			return;
		}
		
		Player player = (Player) entity;
		
		if (event.getCause() == DamageCause.VOID) {
			UtilSound.playLocal(player, Sound.CAT_MEOW, 1F, 1F);
			event.setDamage(5000);
		} else {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void stopBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (!RankManager.instance.hasPermission(player, Rank.ADMIN)) {
			event.setCancelled(true);
			return;
		}
		
		if (player.getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void stopBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!RankManager.instance.hasPermission(player, Rank.ADMIN)) {
			event.setCancelled(true);
			return;
		}
		
		if (player.getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void stopBlockDamage(BlockDamageEvent event) {
		Player player = event.getPlayer();
		if (!RankManager.instance.hasPermission(player, Rank.ADMIN)) {
			event.setCancelled(true);
			return;
		}
		
		if (player.getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
		}
	}
	
	public VisibilityManager getVisibilityManager() {
		return hub.getVisibilityManager();
	}
	
	public HubBoardManager getBoardManager() {
		return boardManager;
	}
	
}
