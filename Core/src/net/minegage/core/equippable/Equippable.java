package net.minegage.core.equippable;


import net.minegage.common.util.UtilItem;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


/*
 * Represents a "perk" given in the lobby; can be a gadget or a trail
 */
public abstract class Equippable
		implements Listener {
		
	protected EquippableManager manager;
	protected String type;
	protected String name;
	protected ItemStack displayItem;
	protected int displaySlot;
	protected Rank rank;
	protected String permOverride;
	protected ItemStack equipItem;
	protected int equipSlot;
	
	protected Set<UUID> equipped = new HashSet<>();
	
	private Equippable(EquippableManager manager, String type, String name, ItemStack displayItem, int displaySlot,
			int equipSlot) {
		this.manager = manager;
		this.type = type;
		this.name = name;
		this.displayItem = displayItem;
		this.displaySlot = displaySlot;
		this.equipSlot = equipSlot;
		
		manager.registerEvents(this);
	}
	
	protected Equippable(EquippableManager manager, String type, String name, ItemStack displayItem, int displaySlot, Rank rank,
			int equipSlot) {
		this(manager, type, name, displayItem, displaySlot, equipSlot);
		this.rank = rank;
	}
	
	protected Equippable(EquippableManager manager, String type, String name, ItemStack displayItem, int displaySlot, Rank rank) {
		this(manager, type, name, displayItem, displaySlot, rank, -1);
	}
	
	public void equip(Player player) {
		equipped.add(player.getUniqueId());
		
		if (equipSlot != -1) {
			player.getInventory()
					.setItem(equipSlot, equipItem);
		}
	}
	
	public void unequip(Player player) {
		equipped.remove(player.getUniqueId());
		
		if (equipSlot != -1) {
			player.getInventory()
					.setItem(equipSlot, null);
		}
	}
	
	public Set<UUID> getEquipped() {
		return equipped;
	}
	
	public JavaPlugin getPlugin() {
		return manager.getPlugin();
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public ItemStack getDisplayItem() {
		return displayItem;
	}
	
	public void setDisplayItem(ItemStack displayItem) {
		this.displayItem = displayItem;
		this.equipItem = new ItemStack(displayItem);
		UtilItem.stripLore(equipItem);
	}
	
	public int getDisplaySlot() {
		return displaySlot;
	}
	
	public Rank getRank() {
		return rank;
	}
	
	public String getPermOverride() {
		return permOverride;
	}
	
	public int getEquipSlot() {
		return equipSlot;
	}
	
	public ItemStack getEquipItem() {
		return equipItem;
	}
	
	public boolean isPhysical() {
		return equipSlot != -1;
	}
	
	public void setPermOverride(String permOverride) {
		this.permOverride = permOverride;
	}
	
	public static enum EquippableType {
		TRAIL("Trail"),
		GADGET("Gadget");
		
		private String name;
		
		private EquippableType(String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
	
}
