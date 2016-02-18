package net.minegage.core.combat;


import net.minegage.common.token.EntityToken;
import net.minegage.common.util.UtilItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;


public class DamagerToken {
	
	private EntityToken entity = null;
	
	private String weapon = null;
	
	public DamagerToken(Entity damager) {
		entity = new EntityToken(damager);
		
		if (damager instanceof LivingEntity) {
			// Get the weapon used, if any
			LivingEntity livingEntity = (LivingEntity) damager;
			
			EntityEquipment equipment = livingEntity.getEquipment();
			if (equipment != null) {
				ItemStack hand = equipment.getItemInHand();
				
				if (hand != null) {
					weapon = UtilItem.getName(hand);
				}
			}
		}
		
	}
	
	public boolean isPlayer() {
		return entity.entityType == EntityType.PLAYER;
	}
	
	public EntityToken getEntity() {
		return entity;
	}
	
	public String getWeapon() {
		return weapon;
	}
	
	public boolean hasWeapon() {
		return weapon != null;
	}
	
}
