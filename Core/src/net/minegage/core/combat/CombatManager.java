package net.minegage.core.combat;


import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minegage.common.log.L;
import net.minegage.common.module.PluginModule;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilSound;
import net.minegage.core.combat.event.CombatDeathEvent;
import net.minegage.core.combat.event.CombatEvent;
import net.minegage.core.combat.modify.DamageBase;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.WeakHashMap;


/**
 *
 */
public class CombatManager
		extends PluginModule {

	private final Field entityLivingDrops;

	private WeakHashMap<LivingEntity, DamageHistory> trackers = new WeakHashMap<>();
	private long expireMillis = 30000L; // 30 seconds
	private DeathMessenger deathMessenger;
	private boolean assists = true;
	
	public CombatManager(JavaPlugin plugin) {
		super("Combat Manager", plugin);

		Field entityLivingDropsTemp;
		try {
			entityLivingDropsTemp = EntityLiving.class.getDeclaredField("drops");
			entityLivingDropsTemp.setAccessible(true);
		} catch (NoSuchFieldException e) {
			L.severe("Failed to locate drops field in " + EntityLiving.class.getName() + " ");
			entityLivingDropsTemp = null;
		}

		this.entityLivingDrops = entityLivingDropsTemp;

		this.deathMessenger = new DeathMessenger(this);
	}
	
	@EventHandler
	public void onTick(TickEvent event) {
		if (event.isNot(Tick.SEC_1)) {
			return;
		}
		
		for (DamageHistory damageHistory : trackers.values()) {
			damageHistory.expireOld();
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void customDamage(EntityDamageEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		Entity hitEntity = event.getEntity();
		
		if (!( hitEntity instanceof LivingEntity )) {
			return;
		}
		
		LivingEntity entity = (LivingEntity) hitEntity;
		
		CombatEvent combat = new CombatEvent(event);
		UtilEvent.call(combat);
		
		if (combat.isCancelled()) {
			event.setCancelled(true);
		} else {
			// Save the damage event information
			CombatDamage damage = new CombatDamage(combat);
			DamageHistory history = trackers.get(entity);
			if (history == null) {
				history = new DamageHistory(expireMillis);
				trackers.put(entity, history);
			}
			
			history.track(damage);
			
			// Remove the damage modifiers
			for (DamageModifier mod : DamageModifier.values()) {
				if (event.isApplicable(mod)) {
					event.setDamage(mod, 0.0);
				}
			}
			
			// Set the new damage modifiers
			for (Entry<DamageModifier, Double> source : combat.getVanillaMods()
					.entrySet()) {
				DamageModifier mod = source.getKey();
				Double val = source.getValue();
				event.setDamage(mod, val);
			}
			
			// Modify the base damage with the log modifiers
			double baseDamage = event.getDamage(DamageModifier.BASE);
			for (DamageBase mod : combat.getModifiers()) {
				baseDamage = mod.modify(baseDamage);
			}
			
			event.setDamage(DamageModifier.BASE, baseDamage);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void preDeath(EntityDamageEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		Entity hitEntity = event.getEntity();
		
		if (!( hitEntity instanceof LivingEntity )) {
			return;
		}
		
		LivingEntity entity = (LivingEntity) hitEntity;
		
		double finalDamage = event.getFinalDamage();
		double health = entity.getHealth();
		
		if (health - finalDamage <= 0.0) {
			// Set the last damage cause prematurely so that it can be used in death processing
			entity.setLastDamageCause(event);
			
			// Remove the damage history
			DamageHistory damageHistory = trackers.remove(entity);

			List<ItemStack> playerDrops = new ArrayList<>();

			if (entity instanceof Player) {
				PlayerInventory inv = ((Player) entity).getInventory();
				for (ItemStack item : inv.getContents()) {
					playerDrops.add(item);
				}
				for (ItemStack item : inv.getArmorContents()) {
					playerDrops.add(item);
				}
			}

			CombatDeathEvent customDeathEvent = new CombatDeathEvent(event, damageHistory, playerDrops);
			UtilEvent.call(customDeathEvent);
			
			if (this.assists) {
				for (Entry<OfflinePlayer, Double> assists : customDeathEvent.getAssists()
						.entrySet()) {
						
					OfflinePlayer offPlayer = assists.getKey();
					Double damage = assists.getValue();
					
					KillAssistEvent assistEvent = new KillAssistEvent(offPlayer, damage, customDeathEvent.getDamageHistory()
							.getDamage());
							
					UtilEvent.call(assistEvent);
				}
			}

			// Prevent the respawn screen from showing
			if (entity instanceof Player) {
				Player player = (Player) entity;
				
				/* Fake the player death */
				PlayerDeathEvent deathEvent = new PlayerDeathEvent(player, playerDrops, 0, null);
				UtilEvent.call(deathEvent);
				
				/* Fake the hurt sound, as it won't normally be sent */
				UtilSound.playPhysical(player.getLocation(), Sound.HURT_FLESH, 1F, 1F);

				for (ItemStack item : playerDrops) {
					if (item != null && item.getType() != Material.AIR) {
						player.getWorld().dropItemNaturally(player.getLocation(), item);
					}
				}

				Location respawnLoc = customDeathEvent.getRespawnLocation();
				
				if (respawnLoc != null) {
					player.teleport(respawnLoc);
				}
				
				/* After all death related processing is done, prevent real player death */
				event.setDamage(0.0);
			}
		}
	}
	
	public WeakHashMap<LivingEntity, DamageHistory> getHistory() {
		return trackers;
	}
	
	public long getExpireMillis() {
		return expireMillis;
	}
	
	public void setExpireMillis(long expireMillis) {
		this.expireMillis = expireMillis;
	}
	
	public DeathMessenger getDeathMessenger() {
		return deathMessenger;
	}
	
	public boolean isAssistsEnabled() {
		return assists;
	}
	
	public void setAssistsEnabled(boolean assistsEnabled) {
		this.assists = assistsEnabled;
	}
	
	
	
}
