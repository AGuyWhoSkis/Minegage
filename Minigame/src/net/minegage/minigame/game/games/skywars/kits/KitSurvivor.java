package net.minegage.minigame.game.games.skywars.kits;

import net.minegage.minigame.kit.Kit;
import net.minegage.minigame.kit.attrib.AttribThrowableTNT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class KitSurvivor
		extends Kit {

	public KitSurvivor() {
		super("Survivor", new String[] {"Starts with stone tools!"}, new AttribThrowableTNT());

		mobHand = new ItemStack(Material.WOOD_AXE);
	}

	@Override
	protected void giveItems(PlayerInventory inv) {
		inv.addItem(new ItemStack(Material.STONE_SWORD));
		inv.addItem(new ItemStack(Material.STONE_AXE));
		inv.addItem(new ItemStack(Material.STONE_PICKAXE));
		inv.addItem(new ItemStack(Material.STONE_SPADE));
	}
}
