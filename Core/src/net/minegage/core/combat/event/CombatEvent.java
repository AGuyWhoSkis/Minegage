package net.minegage.core.combat.event;


import net.minegage.common.util.UtilEvent;
import net.minegage.core.combat.modify.DamageAdd;
import net.minegage.core.combat.modify.DamageBase;
import net.minegage.core.combat.modify.DamageMult;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class CombatEvent
		extends Event
		implements Cancellable {
		
	private static final HandlerList handlers = new HandlerList();
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	private boolean cancelled = false;
	
	private Entity damaged;
	private Entity directDamager;
	private LivingEntity indirectDamager;
	
	private EntityDamageEvent source;
	private DamageCause cause;
	
	private List<DamageBase> customMods = new ArrayList<>();
	private Map<DamageModifier, Double> vanillaMods = new HashMap<>();
	
	public CombatEvent(EntityDamageEvent event) {
		this.source = event;
		this.cause = event.getCause();
		
		this.damaged = event.getEntity();
		this.directDamager = UtilEvent.getDirectDamager(event);
		this.indirectDamager = UtilEvent.getIndirectDamager(event);
		
		for (DamageModifier mod : DamageModifier.values()) {
			if (event.isApplicable(mod)) {
				vanillaMods.put(mod, event.getDamage(mod));
			}
		}
	}
	
	public void addModIncrement(double mod) {
		this.customMods.add(new DamageAdd(mod));
	}
	
	public void addModMultiply(double mod) {
		this.customMods.add(new DamageMult(mod));
	}
	
	public double getFinalDamage() {
		double damage = 0.0D;
		
		for (Entry<DamageModifier, Double> ent : vanillaMods.entrySet()) {
			damage += ent.getValue();
		}
		
		for (DamageBase mod : customMods) {
			mod.modify(damage);
		}
		
		return damage;
	}
	
	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
	public Entity getDamaged() {
		return damaged;
	}
	
	public DamageCause getCause() {
		return cause;
	}
	
	public List<DamageBase> getModifiers() {
		return customMods;
	}
	
	public Map<DamageModifier, Double> getVanillaMods() {
		return vanillaMods;
	}
	
	public Entity getDirectDamager() {
		return directDamager;
	}
	
	public LivingEntity getIndirectDamager() {
		return indirectDamager;
	}
	
	public EntityDamageEvent getSource() {
		return source;
	}
	
	public boolean isPlayerDamager() {
		return indirectDamager != null && indirectDamager instanceof Player;
	}
	
	public Player getPlayerDamager() {
		return (Player) indirectDamager;
	}
	
	public boolean isPlayerDamaged() {
		return damaged != null && damaged instanceof Player;
	}
	
	public Player getPlayerDamaged() {
		return (Player) damaged;
	}
	
}
