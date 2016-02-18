package net.minegage.core.equippable;


import net.minegage.common.java.SafeMap;
import net.minegage.common.module.PluginModule;
import net.minegage.common.move.MoveManager;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilTime;
import net.minegage.common.C;
import net.minegage.core.equippable.gadget.GadgetManager;
import net.minegage.core.equippable.trail.TrailManager;
import net.minegage.core.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;


public class EquippableManager
		extends PluginModule {

	private List<Equippable> equippables = new ArrayList<>();
	private SafeMap<UUID, List<Equippable>> offlineEquipped = new SafeMap<>();
	private SafeMap<UUID, List<Equippable>> activeEquipped = new SafeMap<>();

	private GadgetManager gadgetManager;
	private TrailManager trailManager;

	// One day
	public static final long PURGE_TICKS = 86400000L;

	public EquippableManager(MoveManager moveManager) {
		super("Equippable Manager", moveManager);

		this.gadgetManager = new GadgetManager(this);
		this.trailManager = new TrailManager(this, moveManager);


		BukkitTask bukkitTask = runSyncTimer(PURGE_TICKS, PURGE_TICKS,
		                                     new BukkitRunnable() {
			                                     @Override
			                                     public void run() {
				                                     purgeOldEquippable();
			                                     }
		                                     }

		);
	}

	public void equip(Player player, Equippable equippable, boolean notify) {
		List<Equippable> equipped = getEquipped(player);
		equipped.add(equippable);

		equippable.equip(player);
		activeEquipped.put(player.getUniqueId(), equipped);

		if (notify) {
			C.pMain(player, equippable.getType(), "You equipped " + C.sOut + equippable.getName());
		}
	}

	public void unequip(Player player, Equippable equippable, boolean notify) {
		List<Equippable> equipped = getEquipped(player);
		equipped.remove(equippable);
		activeEquipped.put(player.getUniqueId(), equipped);

		equippable.unequip(player);

		if (notify) {
			C.pMain(player, equippable.getType(), "You unequipped " + C.sOut + equippable.getName());
		}
	}

	public List<Equippable> getEquipped(Player player) {
		return activeEquipped.get(player.getUniqueId());
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void handleJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		UUID   uid    = player.getUniqueId();

		activeEquipped.put(uid, new ArrayList<>());
		List<Equippable> equipped = offlineEquipped.getOrDefault(uid, new ArrayList<>());

		for (Equippable equippable : equipped) {
			equip(player, equippable, false);
		}
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void handleQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		UUID uid = event.getPlayer()
				.getUniqueId();

		List<Equippable> equipped = getEquipped(player);

		if (equipped != null && equipped.size() > 0) {
			offlineEquipped.put(uid, equipped);
			activeEquipped.remove(uid);

			PlayerInventory inventory = player.getInventory();

			ItemStack air = new ItemStack(Material.AIR);

			for (Equippable equippable : equipped) {
				if (equippable.isPhysical()) {
					inventory.setItem(equippable.getEquipSlot(), air);
				}
				equippable.getEquipped()
						.remove(uid);
			}

		} else {
			offlineEquipped.remove(uid);
		}
	}

	public void purgeOldEquippable() {
		Iterator<Entry<UUID, List<Equippable>>> iter = offlineEquipped.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Entry<UUID, List<Equippable>> entry = iter.next();

			UUID          uid = entry.getKey();
			OfflinePlayer pl  = Bukkit.getOfflinePlayer(uid);

			long lastPlayed = pl.getLastPlayed();

			// 1 month
			if (UtilTime.hasPassedSince(lastPlayed, 2592000000L)) {
				iter.remove();
			}
		}
	}

	public Equippable getEquippables(String keyword) {
		for (Equippable equippable : getEquippables()) {
			if (equippable.getName()
					.contains(keyword)) {
				return equippable;
			}
		}
		return null;
	}

	public List<Equippable> getEquippables() {
		return equippables;
	}

	public SafeMap<UUID, List<Equippable>> getOfflineEquipped() {
		return offlineEquipped;
	}

	public SafeMap<UUID, List<Equippable>> getActiveEquipped() {
		return activeEquipped;
	}

	@EventHandler
	public void orderItem(PlayerDropItemEvent event) {
		Player                      player      = event.getPlayer();
		SafeMap<Integer, ItemStack> targetSlots = new SafeMap<>();

		for (Equippable equippable : activeEquipped.get(player.getUniqueId())) {
			if (equippable.isPhysical()) {
				int equipSlot = equippable.getEquipSlot();
				ItemStack equipped = player.getInventory()
						.getItem(equipSlot);

				// TODO: Null item corresponds to dropped item, unless it's rainbow armour. Find out
				// why and fix in a less hacky way
				if (equipped == null) {
					equipped = event.getItemDrop()
							.getItemStack();
				}

				targetSlots.put(equipSlot, equipped);
			}
		}

		UtilEvent.orderItems(event, targetSlots);
	}

	@EventHandler
	public void onItemClick(InventoryClickEvent event) {
		Player          player = (Player) event.getWhoClicked();
		List<ItemStack> items  = new ArrayList<>();

		for (Equippable equippable : activeEquipped.get(player.getUniqueId())) {
			if (equippable.isPhysical()) {
				int equipSlot = equippable.getEquipSlot();
				ItemStack equipped = player.getInventory()
						.getItem(equipSlot);
				items.add(equipped);
			}
		}

		UtilEvent.lockItem(event, items);
	}

	public void addGeneral(Equippable equippable, List<String> lore) {
		ItemStack item = equippable.getDisplayItem();
		ItemMeta  meta = item.getItemMeta();

		String name = C.cWhite + equippable.getType() + " - " + C.cBlue + equippable.getName();
		meta.setDisplayName(name);
		meta.setLore(lore);

		item.setItemMeta(meta);
		equippable.setDisplayItem(item);

		equippables.add(equippable);
	}

	public void addDonator(Equippable equippable, String... description) {
		List<String> lore = new ArrayList<>();

		lore.addAll(getDescriptionLore(description));
		lore.add("");
		lore.addAll(getDefaultLore());
		lore.add("");
		lore.addAll(getDonatorLore(equippable.getRank()));

		addGeneral(equippable, lore);
	}

	public void addFree(Equippable equippable, String... description) {
		List<String> lore = new ArrayList<>();

		lore.addAll(getDescriptionLore(description));
		lore.add("");
		lore.addAll(getDefaultLore());
		lore.add("");

		addGeneral(equippable, lore);
	}

	public List<String> getDescriptionLore(String[] description) {
		List<String> lore = new ArrayList<>();

		if (description != null && description.length > 0) {
			lore.add("");
		}

		for (String str : description) {
			lore.add(C.iMain + str);
		}
		return lore;
	}

	public List<String> getDefaultLore() {
		List<String> lore = new ArrayList<>();
		lore.add(C.cGreen + "Click " + C.iMain + "to toggle!");
		return lore;
	}

	public List<String> getDonatorLore(Rank rank) {
		List<String> lore = new ArrayList<>();
		lore.add(C.iMain + "Unlocked for " + rank.getDisplayName() + C.cReset + C.iMain + " and up");
		lore.add("");
		lore.add(C.cAqua + C.cLine + "donate.minegage.net");
		return lore;
	}

	public GadgetManager getGadgetManager() {
		return gadgetManager;
	}

	public TrailManager getTrailManager() {
		return trailManager;
	}


}
