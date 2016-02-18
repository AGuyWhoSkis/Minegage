package net.minegage.common.util;


import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class UtilInv {
	
	private static void clearContents(Inventory inventory) {
		inventory.setContents(new ItemStack[inventory.getSize()]);
	}
	
	public static void clear(PlayerInventory inventory) {
		clearContents(inventory);
		inventory.setArmorContents(new ItemStack[4]);
	}
	
	public static void clear(DoubleChestInventory inventory) {
		clearContents(inventory.getLeftSide());
		clearContents(inventory.getRightSide());
	}
	
	public static void clear(Inventory inventory) {
		if (inventory instanceof PlayerInventory) {
			clear((PlayerInventory) inventory);
		} else if (inventory instanceof DoubleChestInventory) {
			clear((DoubleChestInventory) inventory);
		} else {
			clearContents(inventory);
		}
	}
	
}
