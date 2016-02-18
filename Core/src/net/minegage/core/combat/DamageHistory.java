package net.minegage.core.combat;


import net.minegage.common.java.SafeMap;
import net.minegage.common.token.EntityToken;
import net.minegage.common.util.UtilTime;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map.Entry;


/**
 * Stores past damage events
 */
public class DamageHistory {
	
	// Damage history, stored in order of newest to oldest
	public LinkedList<CombatDamage> history = new LinkedList<>();
	public long expireMillis;
	public long lastDamagedMillis = -1;
	
	public DamageHistory(long expireMillis) {
		this.expireMillis = expireMillis;
	}
	
	public void track(CombatDamage damage) {
		lastDamagedMillis = System.currentTimeMillis();
		history.addFirst(damage);
	}
	
	/**
	 * @return A map containing portal tokens and their total damage dealt
	 */
	public SafeMap<EntityToken, Double> getTotalDamage() {
		SafeMap<EntityToken, Double> totalDamage = new SafeMap<>();
		for (CombatDamage damage : history) {
			
			DamagerToken token = damage.indirectDamager;
			if (token == null) {
				continue;
			}
			
			EntityToken entity = token.getEntity();
			Double entityDamage = 0.0;
			
			// Create or update the total damage
			Iterator<Entry<EntityToken, Double>> damageIt = totalDamage.entrySet()
					.iterator();
			while (damageIt.hasNext()) {
				Entry<EntityToken, Double> entry = damageIt.next();
				EntityToken otherEntity = entry.getKey();
				
				if (otherEntity.entityUID.equals(entity.entityUID)) {
					entity = otherEntity;
					entityDamage = entry.getValue();
					break;
				}
			}
			
			entityDamage += damage.damage;
			totalDamage.put(entity, entityDamage);
		}
		
		return totalDamage;
	}
	
	public double getDamage() {
		return history.stream()
				.mapToDouble(damage -> damage.damage)
				.sum();
	}
	
	/**
	 * @return The most recently created CombatDamage with a valid LivingEntity
	 */
	public CombatDamage getMostRecentLivingDamager() {
		for (CombatDamage damage : history) {
			
			DamagerToken damagerToken = damage.indirectDamager;
			
			if (damagerToken != null) {
				Entity entity = damage.indirectDamager.getEntity()
						.getEntity();
				if (entity != null && entity instanceof LivingEntity) {
					return damage;
				}
			}
		}
		
		return null;
	}
	
	public CombatDamage getMostRecentDamage() {
		if (history.isEmpty()) {
			return null;
		} else {
			return history.getFirst();
		}
	}
	
	public void expireOld() {
		// Removes expired elements
		ListIterator<CombatDamage> i = history.listIterator(history.size());
		
		while (i.hasPrevious()) {
			CombatDamage last = i.previous();
			
			if (UtilTime.hasPassedSince(last.timestamp, expireMillis)) {
				i.remove();
			} else {
				break;
			}
		}
	}
	
}
