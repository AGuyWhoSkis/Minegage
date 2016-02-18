package net.minegage.minigame.game.games.kitpvp.shop;


import net.minegage.common.util.UtilItem;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum KitShopItem {
	
	ATTACK("Attack", "Upgrade", true, Material.IRON_SWORD, KitShopItem.SLOT_ATTACK),
	DEFENSE("Defense", "Upgrade", true, Material.IRON_HELMET, KitShopItem.SLOT_DEFENSE),;
	
	public static final int SLOT_DEFENSE = 12;
	public static final int SLOT_ATTACK = 14;
	
	public static final ItemStack SOUP_ITEM = UtilItem.create(Material.MUSHROOM_SOUP);
	public static final ItemStack[] SOUPS = new ItemStack[] { SOUP_ITEM, SOUP_ITEM, SOUP_ITEM, SOUP_ITEM };
	
	private String type;
	private String name;
	private boolean upgradeable;
	private Material displayItem;
	private int slot;
	
	KitShopItem(String name, String type, boolean upgradeable, Material displayItem, int slot) {
		this.name = name;
		this.type = type;
		this.upgradeable = upgradeable;
		this.displayItem = displayItem;
		this.slot = slot;
	}
	
	public String getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isUpgradeable() {
		return upgradeable;
	}
	
	public Material getDisplayType() {
		return displayItem;
	}
	
	public int getSlot() {
		return slot;
	}
	
	
}
