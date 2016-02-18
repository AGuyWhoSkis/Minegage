package net.minegage.minigame.game.games.spleef.kits;


import net.minegage.minigame.kit.Kit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class KitStandard
		extends Kit {
		
	public KitStandard() {
		super("Standard Kit", new String[0]);
	}
	
	
	@Override
	protected void giveItems(PlayerInventory inv) {
		inv.addItem(new ItemStack(Material.SNOW_BALL, 24));
	}
	
}
