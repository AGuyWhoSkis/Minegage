package net.minegage.core.combat;


import net.minegage.common.module.PluginModule;
import net.minegage.common.token.EntityToken;
import net.minegage.common.util.UtilString;
import net.minegage.core.CorePlugin;
import net.minegage.common.C;
import net.minegage.core.combat.event.CombatDeathEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class DeathMessenger
		extends PluginModule {
		
	public static final String WEAPON_META = "weapon name";
	
	public static void setWeaponName(Projectile projectile, String name) {
		projectile.setMetadata(WEAPON_META, new FixedMetadataValue(CorePlugin.PLUGIN, name));
	}
	
	public DeathMessageMode mode = DeathMessageMode.SIMPLE;
	
	public DeathMessenger(CombatManager manager) {
		super("Death Messenger", manager);
	}
	
	public static enum DeathMessageMode {
		/**
		 * Disables death messages
		 */
		NONE,
		
		/**
		 * Only displays messages which are related to the player
		 */
		SIMPLE,
		
		/**
		 * Displays all death messages
		 */
		ALL
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void sendDeathMessage(CombatDeathEvent event) {
		if (mode == DeathMessageMode.NONE) {
			return;
		}

		LivingEntity deadEntity = event.getKilled();
		
		DamageHistory history = event.getDamageHistory();
		
		// Most recent damage dealt by a living portal
		CombatDamage deathCredit = history.getMostRecentLivingDamager();
		
		// Damager of deathCredit
		DamagerToken creditDamager = null;
		if (deathCredit != null) {
			creditDamager = deathCredit.indirectDamager;
		}
		
		// Most recent damage
		CombatDamage deathDamage = history.getMostRecentDamage();
		
		// The name of the killed portal
		String deadName = C.sOut;
		if (deadEntity.getType() == EntityType.PLAYER) {
			deadName = ( (Player) deadEntity ).getDisplayName();
		} else if (deadEntity.getCustomName() != null) {
			deadName += deadEntity.getCustomName();
		} else {
			deadName += deadEntity.getName();
		}
		
		deadName += C.sBody;
		
		int assistants = event.getAssists()
				.size();
				
		// The name of the killer, if any
		String killerName = null;
		String killerRawName = null;
		if (creditDamager != null) {
			EntityToken creditToken = creditDamager.getEntity();
			
			killerName = C.sOut;
			if (creditToken.entityCustomName != null) {
				killerName += creditToken.entityCustomName;
			} else {
				killerName += creditToken.entityName;
			}
			
			killerRawName = killerName;
			
			if (assistants > 0) {
				killerName += " +" + assistants;
			}
			
			killerRawName += C.sBody;
			killerName += C.sBody;
		}
		
		String weapon = null;
		if (creditDamager != null) {
			weapon = creditDamager.getWeapon();
		}
		
		if (creditDamager != null && weapon.equals("Air")) {
			if (creditDamager.isPlayer()) {
				weapon = "Fists";
			} else {
				weapon = null;
			}
		}
		
		DamageCause deathCause = deathDamage.cause;
		
		String action = "killed";
		String cause = null;
		String with = null;
		String causeName = NAMES.get(deathCause);
		
		
		if (killerName != null) {
			cause = killerName;
			
			if (deathCause == DamageCause.ENTITY_ATTACK) {
				with = weapon;
			} else if (deathCause == DamageCause.PROJECTILE) {
				Entity projEnt = deathCredit.directDamager.getEntity()
						.getEntity();
						
				if (projEnt != null) {
					action = "shot";
					
					if (projEnt.hasMetadata(WEAPON_META)) {
						weapon = projEnt.getMetadata(WEAPON_META)
								.get(0)
								.asString();
					} else {
						EntityType type = projEnt.getType();
						if (type == EntityType.ARROW) {
							weapon = "Bow";
						} else {
							weapon = UtilString.format(type.name());
						}
					}
				}
				
				with = weapon;
			} else if (weapon != null) {
				with = causeName;
			}
		} else {
			cause = C.fElem(causeName);
		}
		
		if (with != null) {
			with = "with " + C.sOut2 + with;
		}
		
		// Now send the message based on the death message mode
		if (mode == DeathMessageMode.SIMPLE) {
			/* Format: "You were killed by $CAUSE [with...]" and "You killed $KILLED_NAME [with...]"
			 * and "You helped $KILLER_NAME kill $KILLED_NAME */
			
			// Message the killed player
			if (deadEntity instanceof Player) {
				String killedMessage = C.sBody + "You were " + action + " by " + cause;
				
				if (with != null) {
					killedMessage += " " + with;
				}
				
				Player player = (Player) deadEntity;
				C.pRaw(player, killedMessage);
			}
			
			// Message the killer
			if (creditDamager != null && creditDamager.isPlayer()) {
				OfflinePlayer player = creditDamager.getEntity()
						.getPlayer();
				if (player.isOnline()) {
					String killerMessage = C.sBody + "You " + action + " " + C.fElem(deadName) + " " + with;
					C.pRaw(player.getPlayer(), killerMessage);
				}
			}
			
			// Message the assistants
			if (assistants > 0) {
				String assistMessage = C.sBody + "You helped " + killerRawName + " kill " + deadName;
				
				if (with != null) {
					assistMessage += " " + with;
				}
				
				for (Entry<OfflinePlayer, Double> entry : event.getAssists()
						.entrySet()) {
						
					OfflinePlayer offPlayer = entry.getKey();
					if (offPlayer.isOnline()) {
						Player player = offPlayer.getPlayer();
						C.pRaw(player, assistMessage);
					}
				}
			}
			
		} else {
			String message = deadName + " was " + action + " by " + cause;
			if (with != null) {
				message += " " + with;
			}
			
			C.bRaw(message);
		}
	}
	
	/* Display names of damage causes */
	public static Map<DamageCause, String> NAMES = new HashMap<>();
	
	static {
		NAMES.put(DamageCause.BLOCK_EXPLOSION, "Explosion");
		NAMES.put(DamageCause.CONTACT, "Cactus");
		NAMES.put(DamageCause.CUSTOM, "Custom");
		NAMES.put(DamageCause.DROWNING, "Drowning");
		NAMES.put(DamageCause.ENTITY_ATTACK, "Attack");
		NAMES.put(DamageCause.ENTITY_EXPLOSION, "Explosion");
		NAMES.put(DamageCause.FALL, "Falling");
		NAMES.put(DamageCause.FALLING_BLOCK, "Falling Block");
		NAMES.put(DamageCause.FIRE, "Fire");
		NAMES.put(DamageCause.FIRE_TICK, "Fire");
		NAMES.put(DamageCause.LAVA, "Lava");
		NAMES.put(DamageCause.LIGHTNING, "Lightning");
		NAMES.put(DamageCause.MAGIC, "Magic");
		NAMES.put(DamageCause.MELTING, "Melting");
		NAMES.put(DamageCause.POISON, "Poison");
		NAMES.put(DamageCause.PROJECTILE, "Projectile");
		NAMES.put(DamageCause.STARVATION, "Starvation");
		NAMES.put(DamageCause.SUFFOCATION, "Suffocation");
		NAMES.put(DamageCause.SUICIDE, "Suicide");
		NAMES.put(DamageCause.THORNS, "Thorns");
		NAMES.put(DamageCause.VOID, "World Boundary");
		NAMES.put(DamageCause.WITHER, "Wither Effect");
	}
	
	public static String getCauseName(DamageCause cause) {
		return NAMES.getOrDefault(cause, "null");
	}
	
}
