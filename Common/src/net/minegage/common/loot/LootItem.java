package net.minegage.common.loot;

import net.minegage.common.util.Rand;
import org.bukkit.inventory.ItemStack;

public class LootItem
		extends Loot {

	public ItemStack item;
	public int minAmount = 1;
	public int maxAmount = 1;

	public LootItem(ItemStack item) {
		this.item = item;
	}

	public LootItem(ItemStack item, int min, int max) {
		this(item);

		this.minAmount = min;
		this.maxAmount = max;
	}


	@Override
	public ItemStack[] getItems() {
		int amount = Rand.rInt(minAmount, maxAmount + 1);
		item.setAmount(amount);

		return new ItemStack[] { item };
	}

}
