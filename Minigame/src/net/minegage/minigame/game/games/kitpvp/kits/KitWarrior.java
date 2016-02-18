package net.minegage.minigame.game.games.kitpvp.kits;


import net.minegage.common.build.ItemBuild;
import net.minegage.common.util.UtilArmour;
import net.minegage.common.util.UtilArmour.ArmourType;
import net.minegage.common.util.UtilItem;
import net.minegage.minigame.game.games.kitpvp.KitPvpBase;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class KitWarrior
		extends KitPvpBase {
		
	ItemStack sword = ItemBuild.create(Material.DIAMOND_AXE)
			.item();
			
	ItemStack bow = ItemBuild.create(Material.BOW)
			.enchant(Enchantment.ARROW_INFINITE)
			.enchant(Enchantment.ARROW_DAMAGE, 2)
			.item();
			
	private ItemStack arrow = UtilItem.create(Material.ARROW);
	
	public KitWarrior() {
		super("Warrior", new String[] { "Normal melee, weak ranged, normal defense" });
		
		ItemStack[] mobItems = UtilArmour.getArmourSet(ArmourType.IRON, sword);
		
		setMobItems(mobItems);
		
		addArmourUpgrades(15, 20, 30);
		addWeaponUpgrades(sword.getType(), 30);
	}
	
	@Override
	protected void giveItems(PlayerInventory inv) {
		super.giveItems(inv);
		
		giveMobItems(inv);
		inv.setItem(1, bow);
		inv.setItem(28, arrow);
	}
	
}
