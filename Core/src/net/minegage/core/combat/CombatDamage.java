package net.minegage.core.combat;


import net.minegage.core.combat.event.CombatEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;


/**
 * Stores damage event information
 */
public class CombatDamage {
	
	public double damage;
	public DamageCause cause;
	
	/* Tokens are used instead of direct references, as they will be kept in memory even if an
	 * Entity is removed (player logging off, portal dying, etc.) */
	public DamagerToken directDamager;
	public DamagerToken indirectDamager;
	
	public long timestamp = System.currentTimeMillis();
	
	public CombatDamage(CombatEvent event) {
		damage = event.getFinalDamage();
		cause = event.getCause();
		
		Entity direct = event.getDirectDamager();
		LivingEntity indirect = event.getIndirectDamager();
		
		if (direct != null) {
			directDamager = new DamagerToken(direct);
		}
		
		if (indirect != null) {
			indirectDamager = new DamagerToken(indirect);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!( obj instanceof CombatDamage )) {
			return false;
		}
		
		CombatDamage other = (CombatDamage) obj;
		return ( this.timestamp == other.timestamp ) && ( this.damage == other.damage ) && ( this.cause == other.cause )
				&& ( this.directDamager.equals(other.directDamager) ) && ( this.indirectDamager.equals(other.indirectDamager) );
				
	}
	
	
}
