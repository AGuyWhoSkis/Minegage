package net.minegage.minigame.game.common.loot;

import org.bukkit.inventory.ItemStack;

/**
Loot consisting of multiple items; for example a bow and arrows
 */
public class MultiLoot
		extends Loot {

	public Loot[] items;

	public MultiLoot(Loot... items) {
		this.items = items;
	}

	@Override
	public ItemStack[] getItems() {
		ItemStack[] loot = new ItemStack[items.length];

		for (int i = 0; i < items.length; i++) {
			loot[i] = items[i].getItems()[0];
		}

		return loot;
	}
}
