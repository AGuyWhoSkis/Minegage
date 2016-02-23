package net.minegage.common.loot;

import net.minegage.common.java.SafeMap;
import net.minegage.common.log.L;
import net.minegage.common.module.PluginModule;
import net.minegage.common.util.Rand;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilString;
import net.minegage.common.util.UtilTime;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Looter
		extends PluginModule {

	private SafeMap<Integer, Set<Loot>> tiers = new SafeMap<>();

	public Looter(PluginModule host) {
		super("Looter", host);
	}

	public void fillInventory(Inventory inventory, Set<ItemStack> items) {
		setLootTime(inventory.getHolder(), System.currentTimeMillis());

		List<Integer> freeSlots = new ArrayList<>();

		int slot = 0;
		for (ItemStack item : inventory.getContents()) {
			if (item == null || item.getType() == Material.AIR) {
				freeSlots.add(slot);
			}

			slot++;
		}

		Collections.shuffle(freeSlots);

		Iterator<ItemStack> itemsIt = items.iterator();
		for (int i = 0; i < Math.min(items.size(), freeSlots.size()); i++) {
			ItemStack item = itemsIt.next();
			inventory.setItem(freeSlots.get(i), item);
		}
	}

	/* Loot and itemstack getters */

	public Set<ItemStack> convertLoot(Set<Loot> loot, int maxItems) {
		// Convert Set<Loot> into Set<ItemStack> while ensuring item limit isn't exceeded

		Set<Loot> lootCopy = new HashSet<>(loot);
		Set<ItemStack> converted = new HashSet<>();

		Iterator<Loot> lootCopyIt = lootCopy.iterator();

		while (lootCopyIt.hasNext() && converted.size() < maxItems) {
			Loot randLoot = lootCopyIt.next();

			ItemStack[] randItems = randLoot.getItems();

			// Don't pass item limit
			if (converted.size() + randItems.length > maxItems) {
				lootCopyIt.remove(); // This loot can't fit. Remove it
				continue;
			}

			// Prevent duplicates
			lootCopyIt.remove();

			Collections.addAll(converted, randItems);
		}

		return converted;
	}

	public Set<ItemStack> getLoot(int tier, int numItems, boolean duplicates) {
		Set<Loot> randLoot = new HashSet<>();

		Set<Loot> lootCopy = new HashSet<>(tiers.get(tier));
		for (int i = 0; i < numItems && lootCopy.size() > 0; i++) {
			// Item count of each Loot doesn't matter; it will be converted and trimmed to max size after
			Loot rand = UtilJava.getRandIndex(lootCopy);

			randLoot.add(rand);

			if (!duplicates) {
				lootCopy.remove(rand);
			}
		}

		return convertLoot(randLoot, numItems);
	}

	public Set<ItemStack> getLoot(int tier, int numItems) {
		return getLoot(tier, numItems, false);
	}

	public Set<ItemStack> getLoot(int tier, int minItems, int maxItems, boolean duplicates) {
		return getLoot(tier, Rand.rInt(minItems, maxItems + 1), duplicates);
	}

	public Set<ItemStack> getLoot(int tier, int minItems, int maxItems) {
		return getLoot(tier, minItems, maxItems, false);
	}

	/* Loot creation */
	public void loot(int tier, Loot... items) {
		Set<Loot> addedLoot = tiers.get(tier);
		if (addedLoot == null) {
			addedLoot = new HashSet<>();
		}

		Collections.addAll(addedLoot, items);

		tiers.put(tier, addedLoot);
	}

	public void loot(int tier, ItemStack... itemStacks) {
		Loot[] items = new Loot[itemStacks.length];

		for (int i = 0; i < items.length; i++) {
			items[i] = create(itemStacks[i]);
		}

		loot(tier, items);
	}

	public void loot(int tier, ItemStack item, int min, int max) {
		loot(tier, create(item, min, max));
	}

	public void loot(int tier, Material... materials) {
		Loot[] items = new Loot[materials.length];

		for (int i = 0; i < materials.length; i++) {
			items[i] = create(materials[i]);
		}

		loot(tier, items);
	}

	public void loot(int tier, Material material, int min, int max) {
		loot(tier, create(material, min, max));
	}

	public Loot create(ItemStack item, int min, int max) {
		return new LootItem(item, min, max);
	}

	public Loot create(ItemStack item) {
		return create(item, 1, 1);
	}

	public Loot create(Material item, int min, int max) {
		return create(new ItemStack(item), min, max);
	}

	public Loot create(Material item) {
		return create(item, 1, 1);
	}

	/* Loot time helpers */

	public long getLootTime(InventoryHolder holder) {
		if (!(holder instanceof Metadatable)) {
			L.warn("Unable to get loot time of unsupported inventory holder, holding inventory of type " + UtilString.format(holder.getInventory().getType().name()));
			return -1;
		}

		Metadatable metadatable = (Metadatable) holder;

		if (!metadatable.hasMetadata("lootTime")) {
			return 0L;
		}

		List<MetadataValue> data = metadatable.getMetadata("lootTime");
		if (data.isEmpty()) {
			return 0L;
		}

		return data.get(0).asLong();
	}

	public void setLootTime(InventoryHolder holder, long time) {
		if (!(holder instanceof Metadatable)) {
			L.warn("Unable to set loot time of unsupported inventory holder, holding inventory of type " + UtilString.format(holder.getInventory().getType().name()));
			return;
		}

		Metadatable metadatable = (Metadatable) holder;
		metadatable.removeMetadata("lootTime", getPlugin());
		metadatable.setMetadata("lootTime", new FixedMetadataValue(getPlugin(), time));
	}

	public boolean isLooted(InventoryHolder holder) {
		return getLootTime(holder) > 0L;
	}

	public boolean isRefilled(InventoryHolder holder, long refillTime) {
		if (!isLooted(holder)) {
			return true;
		} else {
			return getLootTime(holder) < refillTime;
		}
	}

	public boolean isRefilledTimer(InventoryHolder holder, long refillDelay) {
		if (!isLooted(holder)) {
			return false;
		} else {
			return UtilTime.hasPassedSince(getLootTime(holder), refillDelay);
		}
	}

	public Set<Loot> getTier(int tier) {
		return tiers.get(tier);
	}

}
