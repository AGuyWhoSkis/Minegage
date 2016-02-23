package net.minegage.common.loot;

import org.bukkit.inventory.ItemStack;

/**
	For items which should be grouped together; for example a bow and arrow
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
