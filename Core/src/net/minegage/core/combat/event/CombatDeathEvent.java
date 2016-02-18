package net.minegage.core.combat.event;


import net.minegage.common.java.SafeMap;
import net.minegage.common.token.EntityToken;
import net.minegage.common.util.UtilEvent;
import net.minegage.core.combat.CombatDamage;
import net.minegage.core.combat.DamageHistory;
import net.minegage.core.spawn.SpawnManager;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

import java.util.List;
import java.util.Map.Entry;


/**
 * Player death event wrapper. Facilitates getting the killed Player and direct/indirect killer
 */
public class CombatDeathEvent
		extends Event {
		
	private static final HandlerList handlers = new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	private List<ItemStack> playerDrops;
	private Location respawnLocation = null;
	
	private LivingEntity killed;
	private boolean killedPlayer;
	
	/* The "real" damager. Is the shooter of a projectile, or else it is the same as the direct
	 * killer. This should always be used to get the player killer */
	private ProjectileSource indirectKiller;
	
	/* The entity which dealt the damage to the player. Could be another entity, a projectile, etc. */
	private Entity directKiller;
	
	private boolean killerPlayer;
	
	private EntityDamageEvent event;
	private DamageHistory damageHistory;
	private LivingEntity creditedKiller;
	
	private SafeMap<OfflinePlayer, Double> assists = new SafeMap<>();
	
	public CombatDeathEvent(EntityDamageEvent event, DamageHistory damageHistory, List<ItemStack> playerDrops) {
		this.killed = (LivingEntity) event.getEntity();
		this.killedPlayer = ( killed instanceof Player );
		this.respawnLocation = SpawnManager.instance.getSpawnpoint(killed.getWorld());

		this.playerDrops = playerDrops;

		this.directKiller = UtilEvent.getDirectDamager(event);
		this.indirectKiller = UtilEvent.getIndirectDamager(event);
		this.killerPlayer = ( indirectKiller != null ) && ( indirectKiller instanceof Player );
		
		this.event = event;
		this.damageHistory = damageHistory;

		double totalDamage = damageHistory.getDamage();
		
		CombatDamage creditKiller = damageHistory.getMostRecentLivingDamager();
		if (creditKiller != null) {
			this.creditedKiller = (LivingEntity) creditKiller.indirectDamager.getEntity()
					.getEntity();
		}
		
		for (Entry<EntityToken, Double> entry : damageHistory.getTotalDamage()
				.entrySet()) {
				
			// Don't count assists from non-players
			EntityToken token = entry.getKey();
			if (!token.isPlayer()) {
				continue;
			}
			
			// Don't give the killer assists
			OfflinePlayer player = token.getPlayer();
			if (creditedKiller != null && creditedKiller.getUniqueId()
					.equals(player.getUniqueId())) {
				continue;
			}
			
			Double damage = entry.getValue();
			
			// Only count the assist if the player has done at least 10% of the damage
			double percentage = damage / totalDamage;
			if (percentage > 0.1) {
				assists.put(player, damage);
			}
		}

	}
	
	public SafeMap<OfflinePlayer, Double> getAssists() {
		return assists;
	}
	
	public LivingEntity getCreditedKiller() {
		return creditedKiller;
	}
	
	public boolean isPlayerCreditted() {
		return creditedKiller != null && creditedKiller.getType() == EntityType.PLAYER;
	}
	
	public Player getPlayerCreditted() {
		return (Player) creditedKiller;
	}
	
	public EntityDamageEvent getCause() {
		return event;
	}
	
	public List<ItemStack> getPlayerDrops() {
		return playerDrops;
	}
	
	public void setPlayerDrops(List<ItemStack> playerDrops) {
		this.playerDrops = playerDrops;
	}
	
	public Location getRespawnLocation() {
		return respawnLocation;
	}
	
	public void setRespawnLocation(Location respawnLocation) {
		this.respawnLocation = respawnLocation;
	}
	
	public LivingEntity getKilled() {
		return killed;
	}
	
	public boolean isPlayerKilled() {
		return killedPlayer;
	}
	
	public Player getKilledPlayer() {
		return (Player) killed;
	}
	
	public ProjectileSource getIndirectKiller() {
		return indirectKiller;
	}
	
	public Entity getDirectKiller() {
		return directKiller;
	}
	
	public boolean isPlayerKiller() {
		return killerPlayer;
	}
	
	public Player getKillerPlayer() {
		if (!killerPlayer) {
			return null;
		}
		return (Player) indirectKiller;
	}
	
	public DamageHistory getDamageHistory() {
		return damageHistory;
	}
	
	
	
}
