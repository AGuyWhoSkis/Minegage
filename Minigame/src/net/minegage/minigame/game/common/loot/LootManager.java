package net.minegage.minigame.game.common.loot;

import net.minegage.common.java.SafeMap;
import net.minegage.common.module.PluginModule;
import net.minegage.common.util.Rand;
import net.minegage.common.util.UtilJava;
import net.minegage.minigame.game.Game;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.Metadatable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LootManager
		extends PluginModule {

	private SafeMap<Integer, Set<Loot>> loot = new SafeMap<>();

	public LootManager(Game game) {
		super("Loot Manager", game);
	}

	public void fillInventory(Inventory inventory, int tier, int minItems, int maxItems) {
		InventoryHolder holder = inventory.getHolder();

		if (!(holder instanceof Metadatable)) {
			return;
		}

		Metadatable meta = (Metadatable) holder;

		if (meta.hasMetadata("looted")) {
			return;
		}

		meta.setMetadata("looted", new FixedMetadataValue(plugin, System.currentTimeMillis()));

		Set<Loot> items = loot.get(tier);
		List<Integer> freeSlots = new ArrayList<>();

		int slot = 0;
		for (ItemStack item : inventory.getContents()) {
			if (item == null || item.getType() == Material.AIR) {
				freeSlots.add(slot);
			}

			slot++;
		}

		Collections.shuffle(freeSlots);

		List<ItemStack> randItems = getLoot(tier, minItems, maxItems);

		for (int i = 0; i < Math.min(randItems.size(), freeSlots.size()); i++) {
			ItemStack item = randItems.get(i);
			inventory.setItem(freeSlots.get(i), item);
		}
	}

	public List<ItemStack> getLoot(int tier, int numItems) {
		List<ItemStack> loot = new ArrayList<>();

		int tries = 0;
		Set<Loot> items = new HashSet<>(this.loot.get(tier));
		for (int i = 0; i < numItems && items.size() > 0;) {
			Loot randLoot = UtilJava.getRandIndex(items);

			ItemStack[] randItems = randLoot.getItems();

			// If item limit safeguard is used more than 5 times in a row, stop trying
			if (tries > 5) {
				break;
			}

			// Don't pass item limit
			if (loot.size() + randItems.length > numItems) {
				tries++;
				continue;
			}

			tries = 0;

			// Prevent duplicates
			items.remove(randLoot);

			for (ItemStack item : randItems) {
				loot.add(item);
				i++;
			}
		}

		return loot;
	}

	public List<ItemStack> getLoot(int tier, int minItems, int maxItems) {
		return getLoot(tier, Rand.rInt(minItems, maxItems + 1));
	}

	public void loot(int tier, Loot... items) {
		Set<Loot> addedLoot = loot.get(tier);
		if (addedLoot == null) {
			addedLoot = new HashSet<>();
		}

		for (Loot item : items) {
			addedLoot.add(item);
		}

		loot.put(tier, addedLoot);
	}

	public void loot(int tier, ItemStack... itemStacks) {
		Loot[] items = new Loot[itemStacks.length];

		for (int i = 0; i < items.length; i++) {
			items[i] = create(itemStacks[i]);
		}
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

}
