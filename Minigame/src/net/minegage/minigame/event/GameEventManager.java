package net.minegage.minigame.event;


import net.minegage.common.log.L;
import net.minegage.common.module.PluginModule;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilItem;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilPos;
import net.minegage.common.util.UtilSound;
import net.minegage.core.combat.event.CombatDeathEvent;
import net.minegage.minigame.MinigameManager;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.game.Game.GameState;
import net.minegage.minigame.game.Game.PlayerState;
import net.minegage.minigame.team.GameTeam;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Chains events
 */
public class GameEventManager
		extends PluginModule {
		
	private MinigameManager manager;
	private Set<ItemStack> bloodItems = new HashSet<>();
	private Set<DamageCause> createBlood = new HashSet<>();
	
	@SuppressWarnings("deprecation")
	public GameEventManager(MinigameManager manager) {
		super("Game Event Manager", manager);
		this.manager = manager;

		ItemStack dyeBlood  = UtilItem.create(Material.INK_SACK, DyeColor.RED.getDyeData());
		ItemStack beefBlood = UtilItem.create(Material.RAW_BEEF);
		ItemStack wartBlood = UtilItem.create(Material.NETHER_STALK);

		bloodItems.add(dyeBlood);
		bloodItems.add(beefBlood);
		bloodItems.add(wartBlood);

		createBlood.add(DamageCause.ENTITY_ATTACK);
		createBlood.add(DamageCause.PROJECTILE);
		createBlood.add(DamageCause.CUSTOM);
		createBlood.add(DamageCause.THORNS);
		createBlood.add(DamageCause.BLOCK_EXPLOSION);
		createBlood.add(DamageCause.ENTITY_EXPLOSION);
		createBlood.add(DamageCause.FALL);
		createBlood.add(DamageCause.LIGHTNING);
		createBlood.add(DamageCause.LAVA);
		createBlood.add(DamageCause.WITHER);
		createBlood.add(DamageCause.STARVATION);
		createBlood.add(DamageCause.FALLING_BLOCK);
		createBlood.add(DamageCause.FIRE);
		createBlood.add(DamageCause.FIRE_TICK);
		createBlood.add(DamageCause.MAGIC);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void createBlood(CombatDeathEvent event) {
		Game game = getGame();
		if (game == null) {
			return;
		}
		
		if (event.isPlayerKilled()) {
			Player player = event.getKilledPlayer();
			
			if (createBlood.contains(event.getCause()
					.getCause())) {
				createBlood(player, event.getDirectKiller());
			} else {
				// Prevent the sound from playing
				event.getCause()
						.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void handleDeath(CombatDeathEvent event) {
		Game game = getGame();
		if (game == null) {
			return;
		}
		
		LivingEntity entity = event.getKilled();
		
		if (entity instanceof Player) {
				Player player = (Player) entity;
				L.d(player.getName() + " combat death event");

			if (game.getState(player) == PlayerState.IN) {
				GameDeathEvent gameDeath = new GameDeathEvent(game, player);
				UtilEvent.call(gameDeath);
				
				GameTeam team = game.getTeam(player);
				
				if (gameDeath.isPlayerOut()) {
					L.d("player is now out");
					game.out(player, true);
				} else if (team.respawnSeconds > 0) {
					L.d("calling delayed respawn");
					game.respawn(player, team.respawnSeconds);
					
					if (isInstaRespawn(event.getCause()
							.getCause())) {
						player.teleport(player.getWorld()
								.getSpawnLocation());
					}
					
				} else {
					L.d("calling instant respawn");
					// Cancel to prevent knockback
					event.getCause()
							.setCancelled(true);
							
					UtilSound.playPhysical(player.getLocation(), Sound.HURT_FLESH, 1F, 1F);
					
					game.respawn(player);
				}
				
				if (game.getState() == GameState.PLAYING && !game.explaining && game.endCheck()) {
					L.d("calling game end");
					game.setState(GameState.ENDING);
				}
				
			} else {
				game.respawn(player);
			}
		}
		
	}
	
	/**
	 * @return If the player has limited visibility
	 */
	private boolean isInstaRespawn(DamageCause cause) {
		return cause == DamageCause.VOID || cause == DamageCause.LAVA || cause == DamageCause.DROWNING
				|| cause == DamageCause.SUFFOCATION;
	}
	
	public void createBlood(Entity entity, Entity directKiller) {
		Vector bloodDir = new Vector();
		
		if (directKiller != null && ( directKiller instanceof LivingEntity )) {
			LivingEntity livingKiller = (LivingEntity) directKiller;
			bloodDir = livingKiller.getLocation()
					.getDirection()
					.multiply(0.1);
		}
		
		Location loc = entity.getLocation()
				.add(0.0, 1, 0.0);
				
		List<Item> items = new ArrayList<>();
		World world = entity.getWorld();
		
		for (int i = 0; i < 12; i++) {
			Vector posRand = UtilPos.createRand(0.1, 0.5);
			Location bloodLoc = loc.add(posRand);
			
			Vector velRand = UtilPos.createRand(0.1);
			Vector velAdd = bloodDir.clone()
					.add(bloodDir.clone()
							.add(velRand));
							
			ItemStack bloodStack = UtilJava.getRandIndex(bloodItems);
			
			Item item = world.dropItemNaturally(bloodLoc, bloodStack);
			item.setPickupDelay(100);
			
			Vector velocity = item.getVelocity()
					.add(velAdd);
			item.setVelocity(velocity);
			
			items.add(item);
		}
		
		runSyncDelayed(20L, new Runnable() {
			@Override
			public void run() {
				for (Item item : items) {
					item.remove();
				}
			}
		});
	}
	
	private Game getGame() {
		return manager.getGame();
	}
	
}
