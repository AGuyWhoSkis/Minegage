package net.minegage.minigame;


import com.google.common.collect.Lists;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_8_R3.DamageSource;
import net.minegage.common.C;
import net.minegage.common.block.ExplosionManager;
import net.minegage.common.log.L;
import net.minegage.common.module.PluginModule;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.util.UtilArmour;
import net.minegage.common.util.UtilEntity;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilMat;
import net.minegage.common.util.UtilMath;
import net.minegage.common.util.UtilPlayer;
import net.minegage.common.util.UtilServer;
import net.minegage.common.util.UtilWorld;
import net.minegage.core.combat.DeathMessenger.DeathMessageMode;
import net.minegage.core.combat.event.CombatDeathEvent;
import net.minegage.core.rank.Rank;
import net.minegage.core.rank.RankManager;
import net.minegage.core.spawn.SpawnCommandEvent;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.game.Game.GameState;
import net.minegage.minigame.game.event.GameStateChangeEvent;
import net.minegage.minigame.map.EventPlayerOutOfBounds;
import net.minegage.minigame.team.GameTeam;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.projectiles.ProjectileSource;
import org.spigotmc.SpigotWorldConfig;

import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;


/**
 Enforces rules set by Game
 */
public class RuleManager
		extends PluginModule {

	private GameManager gameManager;

	public RuleManager(GameManager gameManager) {
		super("Rule Manager", gameManager);

		this.gameManager = gameManager;
		Bukkit.setDefaultGameMode(GameMode.SURVIVAL);

		for (World world : Bukkit.getWorlds()) {
			setRules(world);
		}
	}

	@EventHandler
	public void worldSettings(WorldLoadEvent event) {
		setRules(event.getWorld());
	}

	public void setRules(World world) {
		net.minecraft.server.v1_8_R3.World nmsWorld = ((CraftWorld) world).getHandle();
		SpigotWorldConfig                  config   = nmsWorld.spigotConfig;

		config.itemMerge = 0.0;
	}

	private boolean canBypass(Player player) {
		return player.getGameMode() == GameMode.CREATIVE && player.isOp();
	}

	@EventHandler
	public void filterWeather(WeatherChangeEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (event.toWeatherState() && UtilWorld.isMainWorld(event.getWorld())) {
			event.setCancelled(true);
		} else {
			Game game = gameManager.getGame();

			if (event.toWeatherState() && (game == null || !game.weather)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void setJoinMessage(PlayerJoinEvent event) {
		event.setJoinMessage(null);

		Game game = getGame();
		if (game == null) {
			return;
		}

		Player player = event.getPlayer();

		if (game.inLobby()) {
			C.bMain("Join", player.getName() + " " + C.sOut + UtilServer.numPlayers() + C.sBody + "/" + C.sOut +
			                game.maxPlayers);
		} else if (game.inMap() && game.joinMessageLive) {
			C.bMain("Join", player.getName());
		}
	}

	@EventHandler
	public void setQuitMessage(PlayerQuitEvent event) {
		event.setQuitMessage(null);

		Game game = getGame();
		if (game == null) {
			return;
		}

		Player player = event.getPlayer();

		if (game.inLobby()) {
			C.bMain("Quit", player.getName());
		} else if (game.inMap() && game.quitMessageLive) {
			C.bMain("Quit", player.getName());
		}
	}


	@EventHandler
	public void filterJoin(PlayerLoginEvent event) {
		Player player = event.getPlayer();

		RankManager rankManager = RankManager.instance;

		Game game = gameManager.getGame();
		if (game == null) {
			return;
		}

		int  newOnline = UtilServer.numPlayers() + 1;
		Rank rank      = rankManager.getRank(player);

		if (newOnline <= game.maxPlayers) {
			event.allow();
		} else if (newOnline > game.maxPlayers && !rankManager.hasPermission(rank, Rank.PRO)) {
			event.disallow(Result.KICK_FULL, ChatColor.YELLOW + "Server full! Pros can join full games: " + C.cAqua +
			                                 "http://donate.minegage.com");
		} else if (newOnline > game.maxPlayersAbsolute && !rankManager.hasPermission(rank, Rank.MODERATOR)) {
			event.disallow(Result.KICK_FULL, ChatColor.YELLOW + "That game cannot fit any more players.");
		} else {
			UUID uid = player.getUniqueId();
			runSyncDelayed(20L, new Runnable() {
				@Override
				public void run() {
					Player pl = Bukkit.getPlayer(uid);
					if (pl != null && pl.isOnline()) {
						C.pWarn(pl, "Notice", "You have bypassed the hard maximum player limit on this server.");
					}
				}
			});
		}

	}

	@EventHandler
	public void enforceBounds(EventPlayerOutOfBounds event) {
		Player player = event.getPlayer();
		World  world  = player.getWorld();

		Game game = gameManager.getGame();

		if (game == null || game.inLobby() || UtilWorld.isMainWorld(world)) {
			player.setFallDistance(0F);
			player.teleport(UtilWorld.getMainWorld().getSpawnLocation());
		} else if (!game.boundaryDamage || !game.canInteract(player)) {
			player.setFallDistance(0F);
			player.teleport(world.getSpawnLocation());
		} else {
			UtilEntity.damage(player, 500.0, DamageSource.OUT_OF_WORLD);
		}
	}

	@EventHandler
	public void enforceProjectileShoot(ProjectileLaunchEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Projectile projectile = event.getEntity();

		ProjectileSource source = projectile.getShooter();
		if (source == null || !(source instanceof Player)) {
			return;
		}

		Game game = gameManager.getGame();

		if (game == null || !game.isPlaying() || game.explaining) {
			event.setCancelled(true);
			return;
		}

		Player player = (Player) source;
		if (canBypass(player)) {
			return;
		}

		if (!game.canInteract(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void enforceArrowHit(ProjectileHitEvent event) {
		Game game = getGame();
		if (game == null) {
			return;
		}

		if (game.inLobby() || !game.arrowStay) {
			Projectile projectile = event.getEntity();
			if (projectile.getType() == EntityType.ARROW) {
				projectile.remove();
			}
		}
	}

	@EventHandler
	public void enforceHunger(TickEvent event) {
		if (event.isNot(Tick.SEC_5)) {
			return;
		}

		Game game = gameManager.getGame();

		List<Player> reset = Lists.newArrayList();
		if (game == null || game.inLobby() || !game.hunger) {
			reset = UtilServer.playersList();
		} else {
			reset = game.getPlayersOut();
		}

		reset.stream()
				.forEach(player -> UtilPlayer.resetHunger(player));
	}

	@EventHandler
	public void enforceBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Game   game   = gameManager.getGame();
		Player player = event.getPlayer();

		if (canBypass(player)) {
			return;
		}

		if (game == null || !game.isPlaying()) {
			event.setCancelled(true);
			return;
		}

		if (!game.canInteract(player)) {
			event.setCancelled(true);
			return;
		}

		MaterialData data = UtilMat.getData(event.getBlock());

		if (game.blockPlace && game.blockPlaceDeny.contains(data)) {
			event.setCancelled(true);
		} else if (!game.blockPlace && !game.blockPlaceAllow.contains(data)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void enforceBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Game   game   = gameManager.getGame();
		Player player = event.getPlayer();

		if (canBypass(player)) {
			return;
		}

		if (game == null || !game.isPlaying()) {
			event.setCancelled(true);
			return;
		}

		if (!game.canInteract(player)) {
			event.setCancelled(true);
			return;
		}

		MaterialData data = UtilMat.getData(event.getBlock());
		if (game.blockBreak && game.blockBreakDeny.contains(data)) {
			event.setCancelled(true);
		} else if (!game.blockBreak && !game.blockBreakAllow.contains(data)) {
			event.setCancelled(true);
		}
	}

	/**
	 Same as {@link RuleManager#enforceBlockBreak(BlockBreakEvent)}}}
	 */
	@EventHandler
	public void enforceBlockDamage(BlockDamageEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Game game = getGame();
		if (game == null || !game.isPlaying()) {
			event.setCancelled(true);
			return;
		}

		Player player = event.getPlayer();
		if (canBypass(player)) {
			return;
		}

		if (!game.canInteract(player)) {
			event.setCancelled(true);
			return;
		}

		MaterialData data = UtilMat.getData(event.getBlock());
		if (game.blockBreak && game.blockBreakDeny.contains(data)) {
			event.setCancelled(true);
		} else if (!game.blockBreak && !game.blockBreakAllow.contains(data)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void enforceItemDrop(PlayerDropItemEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Game game = gameManager.getGame();
		if (game == null) {
			return;
		}

		Player player = event.getPlayer();
		if (canBypass(player)) {
			return;
		}

		if (!game.canInteract(player)) {
			event.setCancelled(true);
			return;
		}

		Item         itemEntity = event.getItemDrop();
		ItemStack    item       = itemEntity.getItemStack();
		MaterialData data       = item.getData();

		if (game.itemDrop && game.itemDropDeny.contains(data)) {
			event.setCancelled(true);
		} else if (!game.itemDrop && !game.itemDropAllow.contains(data)) {
			event.setCancelled(true);
		}

		runSyncDelayed(1L, new Runnable() {
			@Override
			public void run() {
				player.updateInventory();
			}
		});
	}

	@EventHandler
	public void enforeItemTake(PlayerPickupItemEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Game game = gameManager.getGame();
		if (game == null) {
			return;
		}

		Player player = event.getPlayer();
		if (canBypass(player)) {
			return;
		}

		if (!game.canInteract(player)) {
			event.setCancelled(true);
			return;
		}

		Item         itemEntity = event.getItem();
		ItemStack    item       = itemEntity.getItemStack();
		MaterialData data       = item.getData();

		if (game.itemPickup && game.itemPickupDeny.contains(data)) {
			event.setCancelled(true);
		} else if (!game.itemPickup && !game.itemTakeAllow.contains(data)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void enforceItemMove(InventoryClickEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Game game = gameManager.getGame();
		if (game == null) {
			return;
		}

		Player player = (Player) event.getWhoClicked();
		if (canBypass(player)) {
			return;
		}

		if (!game.canInteract(player)) {
			event.setCancelled(true);
			return;
		}

		ItemStack item = event.getCurrentItem();
		if (item == null) {
			return;
		}

		if (UtilArmour.isArmour(event.getSlot())) {
			if (!game.armourMove) {
				event.setCancelled(true);
				return;
			}
		}

		MaterialData data = item.getData();

		if (game.itemMove && game.itemMoveDeny.contains(data)) {
			event.setCancelled(true);
		} else if (!game.itemMove && !game.itemMoveAllow.contains(data)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void enforceDamage(EntityDamageEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Entity damagedEntity = event.getEntity();
		if (!(damagedEntity instanceof LivingEntity)) {
			return;
		}

		LivingEntity damaged = (LivingEntity) damagedEntity;

		boolean damagedPlayer = isPlayer(damaged);
		boolean damagedEnviro = !damagedPlayer;
		Player  pDamaged      = (damagedPlayer) ? ((Player) damaged) : (null);

		Game game = gameManager.getGame();
		
		/* Don't check for null game yet - the damage must be allowed even without a game */

		DamageCause cause = event.getCause();
		if (damagedPlayer && cause == DamageCause.VOID) {
			Player player = (Player) damaged;

			if (game == null || game.inLobby()) {
				event.setCancelled(true);
				player.setFallDistance(0F);
				player.teleport(player.getWorld()
						                .getSpawnLocation());
			} else if (game.isSpectating(player) || !game.boundaryDamage) {
				// Teleport the player to spawn
				event.setCancelled(true);
				player.setFallDistance(0F);
				player.teleport(game.getSpecSpawn());
			} else {
				// Allow the damage
				return;
			}
		}
		
		/* Don't allow damage outside of games, and don't allow damage if the game doesn't exist */
		if (game == null || !game.isPlaying()) {
			event.setCancelled(true);
			return;
		}

		if (damagedPlayer && !game.canInteract(pDamaged)) {
			pDamaged.setFireTicks(0);
			event.setCancelled(true);
			return;
		}
		
		/* Don't allow damage while the game is being explained. Note that this is only applicable
		 * when explaining is enabled and explain freeze is disabled. If explain freeze is enabled,
		 * the damage will be prevented above, with Game#canInteract(Player) */
		if (game.explaining) {
			event.setCancelled(true);
			return;
		}

		if (!game.damage) {
			event.setCancelled(true);
			return;
		}

		if (!game.damageVsPlayer && damagedPlayer) {
			event.setCancelled(true);
			return;
		}

		if (!game.damageVsEnviro && damagedEnviro) {
			event.setCancelled(true);
			return;
		}

		if (!game.damageFallVsPlayer && damagedPlayer && cause == DamageCause.FALL) {
			event.setCancelled(true);
			return;
		}

		if (!game.damageFallVsEnviro && damagedEnviro && cause == DamageCause.FALL) {
			event.setCancelled(true);
			return;
		}

		ProjectileSource damager = UtilEvent.getIndirectDamager(event);

		if (damager != null) {
			boolean damagerPlayer = isPlayer(damager);
			boolean damagerEnviro = !damagerPlayer;
			Player  pDamager      = (damagerPlayer) ? (Player) damager : null;

			// Cancel damage caused by spectators, unless caused by projectile
			if (damagerPlayer && cause != DamageCause.PROJECTILE && game.isSpectating(pDamager)) {
				event.setCancelled(true);
				return;
			}

			if (!game.damagePlayerVsPlayer && damagerPlayer && damagedPlayer) {
				event.setCancelled(true);
				return;
			}

			if (!game.damageEnviroVsPlayer && damagerEnviro && damagedPlayer) {
				event.setCancelled(true);
				return;
			}

			if (!game.damageEnviroVsEnviro && damagerEnviro && damagedEnviro) {
				event.setCancelled(true);
				return;
			}

			if (!game.damagePlayerVsEnviro && damagerPlayer && damagedEnviro) {
				event.setCancelled(true);
				return;
			}

			if (!game.damagePlayerVsSelf && damager.equals(damaged)) {
				event.setCancelled(true);
				return;
			}

			if (!game.damagePlayerVsSelfTeam && damagerPlayer && damagedPlayer && game.getTeam(pDamager)
					.equals(game.getTeam(pDamaged))) {
				event.setCancelled(true);
				return;
			}

		}
	}

	@EventHandler (priority = EventPriority.LOW)
	public void handleDeath(CombatDeathEvent event) {
		Game game = getGame();
		if (game == null || !game.isPlaying()) {
			event.getPlayerDrops().clear();
			return;
		}

		if (!event.isPlayerKilled()) {
			return;
		}

		Player player = event.getKilledPlayer();
		if (game.getState() != GameState.PLAYING || !game.canInteract(player) || !game.itemDropDeath) {
			event.getPlayerDrops().clear();
		}

		// Remove respawn location
		event.setRespawnLocation(null);

		GameTeam team           = game.getTeam(player);
		double   respawnSeconds = team.respawnSeconds;
		if (respawnSeconds <= 0) {
			game.respawn(player);
			return;
		}

	}

	@EventHandler
	public void filterEntityExplode(EntityExplodeEvent event) {
		filterExplosion(event);
	}

	@EventHandler
	public void filterBlockExplode(BlockExplodeEvent event) {
		L.d(event.getBlock().getType().name() + " explode");
		filterExplosion(event);
	}

	public <T extends Event & Cancellable> void filterExplosion(T explodeEvent) {
		if (explodeEvent.isCancelled()) {
			return;
		}

		Game game = getGame();
		if (game == null || !game.isPlaying() || !game.explosions) {
			explodeEvent.setCancelled(true);
		}
	}

	@EventHandler
	public void setOtherRules(GameStateChangeEvent event) {
		Game game = getGame();
		if (game == null) {
			return;
		}

		if (event.getNewState() == GameState.PLAYING) {
			ExplosionManager manager = gameManager.getMinigameManager().getExplosionManager();

			manager.regenerate = game.explodeRegen;
			manager.debris = game.explodeDebris;

			gameManager.getMinigameManager().getCombatManager().getDeathMessenger().mode = game.deathMessageMode;

		} else if (event.getNewState() == GameState.ENDING) {
			ExplosionManager manager = gameManager.getMinigameManager().getExplosionManager();

			manager.regenerate = true;
			manager.debris = false;

			gameManager.getMinigameManager().getCombatManager().getDeathMessenger().mode = DeathMessageMode.NONE;
		}
	}

	@EventHandler (priority = EventPriority.LOW)
	public void filterSpawnCommand(SpawnCommandEvent event) {
		Game game = getGame();
		if (game == null) {
			return;
		}

		if (game.inLobby()) {
			return;
		}

		if (!game.inLobby() && !game.spawnCommand) {
			event.setCancelled(true);
			C.pMain(event.getPlayer(), "Spawn", "The spawn command is disabled right now");
		}
	}

	@EventHandler
	public void filterItemDamage(PlayerItemDamageEvent event) {
		Game game = getGame();
		if (game == null) {
			return;
		}

		if (game.inLobby() || !game.itemDamage || !game.canInteract(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void filterHangingBreak(HangingBreakByEntityEvent event) {
		Game game = getGame();
		if (game == null) {
			return;
		}

		Entity remover = event.getRemover();
		if (!(remover instanceof Player)) {
			return;
		}

		Player player = (Player) remover;

		if (!game.canInteract(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void filterInteract(PlayerInteractEvent event) {
		Game game = getGame();
		if (game == null) {
			return;
		}

		Player player = event.getPlayer();

		if (!game.canInteract(player)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void filterTargetting(EntityTargetLivingEntityEvent event) {
		Game game = getGame();
		if (game == null) {
			return;
		}

		LivingEntity target = event.getTarget();
		if (target == null || target.getType() != EntityType.PLAYER) {
			return;
		}

		Player player = (Player) target;
		if (!game.canInteract(player)) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler
	public void preventExplainMovement(PlayerMoveEvent event) {
		Game game = getGame();
		if (game == null) {
			return;
		}

		if (game.explaining && game.explainFreeze) {
			if (UtilMath.offset2D(event.getFrom(), event.getTo()) > 0) {
				event.setTo(event.getFrom());
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		Entity damaged = event.getEntity();

		// Allow for /kill command damage
		if (event.getCause() == DamageCause.VOID) {
			return;
		}

		if (isInSafeZone(damaged)) {
			event.setCancelled(true);
		} else {
			Entity damager = UtilEvent.getIndirectDamager(event);
			if (damager != null) {
				if (isInSafeZone(damager)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onBowShoot(ProjectileLaunchEvent event) {
		ProjectileSource source = event.getEntity()
				.getShooter();
		if (source != null && source instanceof Entity) {
			Entity entity = (Entity) source;
			if (isInSafeZone(entity)) {
				event.setCancelled(true);
			}
		}
	}

	private boolean isInSafeZone(Entity entity) {
		Game game = getGame();
		if (game == null) {
			return false;
		}

		Location entityLoc = entity.getLocation();

		for (Entry<Location, Double> safeZone : game.safeZones.entrySet()) {
			if (UtilMath.offset(entityLoc, safeZone.getKey()) <= safeZone.getValue()) {
				return true;
			}
		}

		return false;

	}

	private Game getGame() {
		return gameManager.getGame();
	}

	private boolean isPlayer(ProjectileSource damager) {
		if (damager == null) {
			return false;
		}

		return damager instanceof Player;
	}

}
