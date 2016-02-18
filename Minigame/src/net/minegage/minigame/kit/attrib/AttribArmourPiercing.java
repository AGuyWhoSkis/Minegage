package net.minegage.minigame.kit.attrib;


import net.minegage.common.util.Rand;
import net.minegage.common.util.UtilPlayer;
import net.minegage.core.combat.event.CombatEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;


public class AttribArmourPiercing
		extends Attrib {
		
	private Material item;
	
	public AttribArmourPiercing(Material item, String... desc) {
		super("Armour piercing", desc);
		this.item = item;
	}
	
	@Override
	public void apply(Player player) {
		// Do nothing
	}
	
	@EventHandler
	public void onDamage(CombatEvent event) {
		if (!event.isPlayerDamager()) {
			return;
		}
		
		Player damager = event.getPlayerDamager();
		if (!appliesTo(damager)) {
			return;
		}
		
		if (!UtilPlayer.isHolding(damager, item)) {
			return;
		}
		
		double armourMod = event.getVanillaMods()
				.get(DamageModifier.ARMOR);
		armourMod *= Rand.rDouble(0.4, 0.6);
		
		event.getVanillaMods()
				.put(DamageModifier.ARMOR, armourMod);
	}
	
	
	
}
