package net.minegage.minigame.game.games.xpwars.kits;


import net.minegage.common.util.UtilArmour;
import net.minegage.common.util.UtilArmour.ArmourType;
import net.minegage.common.util.UtilItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class KitXPWarrior
		extends XPWarsKitBase {
		
	public KitXPWarrior() {
		super("XPWarrior", new String[] { "The classic XPWars kit!", "Standard sword, bow and 16 arrows" });
		setMobItems(UtilArmour.getArmourSet(ArmourType.LEATHER, UtilItem.create(Material.STONE_SWORD)));
	}
	
	@Override
	protected void giveItems(PlayerInventory inv) {
		super.giveItems(inv);
		
		giveMobItems(inv);
		inv.setItem(1, UtilItem.create(Material.BOW));
		inv.setItem(7, new ItemStack(Material.ARROW, 16));
	}
	
}
