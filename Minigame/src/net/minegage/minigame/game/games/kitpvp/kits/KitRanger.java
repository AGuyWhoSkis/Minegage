package net.minegage.minigame.game.games.kitpvp.kits;


import net.minegage.common.build.ItemBuild;
import net.minegage.common.util.UtilArmour;
import net.minegage.common.util.UtilArmour.ArmourType;
import net.minegage.minigame.game.games.kitpvp.KitPvpBase;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class KitRanger
		extends KitPvpBase {
		
	public KitRanger() {
		super("Ranger", new String[] { "Strong melee, weak ranged, weak defense" });
		
		addArmourUpgrades(10, 15);
		addSwordUpgrades(Material.IRON_SWORD, 10, 15);
		
		ItemStack sword = ItemBuild.create(Material.IRON_SWORD)
				.enchant(Enchantment.DAMAGE_ALL, 1)
				.item();
				
		setMobItems(UtilArmour.getArmourSet(ArmourType.GOLD, sword));
	}
	
	@Override
	protected void giveItems(PlayerInventory inv) {
		super.giveItems(inv);
		
		giveMobItems(inv);
		
		ItemStack bow = ItemBuild.create(Material.BOW)
				.enchant(Enchantment.ARROW_INFINITE)
				.enchant(Enchantment.ARROW_DAMAGE, 2)
				.item();
				
		ItemStack arrow = new ItemStack(Material.ARROW);
		
		inv.setItem(1, bow);
		inv.setItem(28, arrow);
	}
	
}
