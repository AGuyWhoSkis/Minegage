package net.minegage.minigame.game.games.oitq.kits;


import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class KitDefault
		extends OITQKitBase {
		
	public KitDefault() {
		super("Classic", new String[] { });
		this.mobHand = new ItemStack(Material.WOOD_SWORD);
	}
	
	@Override
	protected void giveItems(PlayerInventory inv) {
		inv.addItem(mobHand);
		inv.addItem(bow);
		inv.addItem(arrow);
	}
	
}
