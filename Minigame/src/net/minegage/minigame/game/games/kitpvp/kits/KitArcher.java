package net.minegage.minigame.game.games.kitpvp.kits;


import net.minegage.common.util.UtilArmour;
import net.minegage.common.util.UtilArmour.ArmourType;
import net.minegage.common.util.UtilItem;
import net.minegage.core.mob.MobType;
import net.minegage.minigame.game.games.kitpvp.KitPvpBase;
import net.minegage.minigame.kit.attrib.AttribEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class KitArcher
		extends KitPvpBase {
		
	public KitArcher() {
		super("Archer", new String[] { "Weak melee, strong ranged, weak defense" }, new AttribEffect("Speed boost", new PotionEffect(
				PotionEffectType.SPEED, Integer.MAX_VALUE, 0)));
				
		mobType = MobType.SKELETON;
		setMobItems(UtilArmour.getArmourSet(ArmourType.CHAINMAIL));
		mobHand = UtilItem.create(Material.BOW);
		mobHand.addEnchantment(Enchantment.ARROW_DAMAGE, 3);
		mobHand.addEnchantment(Enchantment.ARROW_INFINITE, 1);
		
		addBowUpgrades(15, 25);
		addArmourUpgrades(10, 20);
	}
	
	@Override
	protected void giveItems(PlayerInventory inv) {
		super.giveItems(inv);
		
		ItemStack sword = UtilItem.create(Material.IRON_SWORD);
		ItemStack arrow = new ItemStack(Material.ARROW, 1);
		
		giveMobItems(inv);
		
		inv.setItem(0, sword);
		inv.setItem(1, mobHand);
		inv.setItem(28, arrow);
	}
	
}
