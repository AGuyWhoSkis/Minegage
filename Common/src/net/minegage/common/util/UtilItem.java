package net.minegage.common.util;


import net.minecraft.server.v1_8_R3.MinecraftKey;
import net.minegage.common.C;
import net.minegage.common.java.SafeMap;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class UtilItem {
	
	public static final int SLOT_HELMET = 39;
	public static final int SLOT_CHEST = 38;
	public static final int SLOT_LEG = 37;
	public static final int SLOT_BOOT = 36;
	
	private static SafeMap<Enchantment, String> enchantNames = new SafeMap<>();
	
	static {
		for (MinecraftKey enchantKey : net.minecraft.server.v1_8_R3.Enchantment.getEffects()) {
			String enchantName = enchantKey.a();
			
			net.minecraft.server.v1_8_R3.Enchantment nmsEnchant = net.minecraft.server.v1_8_R3.Enchantment.getByName(enchantName);
			int id = nmsEnchant.id;
			
			@SuppressWarnings("deprecation")
			Enchantment bukkitEnchant = Enchantment.getById(id);
			
			// Format name
			enchantName = enchantName.replaceAll("_", " ");
			enchantName = WordUtils.capitalizeFully(enchantName);
			
			enchantNames.put(bukkitEnchant, enchantName);
		}
	}
	
	public static String getName(Enchantment enchantment) {
		return enchantNames.getOrDefault(enchantment, "null");
	}
	
	public static void addFlags(ItemStack item, ItemFlag... flags) {
		ItemMeta meta = getMeta(item);
		meta.addItemFlags(flags);
		setMeta(item, meta);
	}
	
	/**
	 * Attempts to find the display name of the given item. Will only work for items which are in
	 * the creative inventory.
	 * 
	 * @return The display name of the item, if found. Otherwise null.
	 */
	public static String getName(ItemStack item) {
		net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		
		// Only items which are in the creative inventory will work
		if (nmsItem == null) {
			return UtilString.format(item.getType()
					.name());
		}
		
		return nmsItem.getName();
	}
	
	public static ItemStack resetDurability(ItemStack item) {
		if (item == null) {
			return null;
		}
		
		item.setDurability((short) 0);
		return item;
	}
	
	public static ItemStack setDurabilityPercentage(ItemStack item, double percentage) {
		if (item == null) {
			return null;
		}
		
		short max = item.getType()
				.getMaxDurability();
				
		double difference = percentage * max;
		short current = (short) ( max - difference );
		item.setDurability(current);
		
		return item;
	}
	
	public static double getDurabilityPercentage(ItemStack item) {
		short max = item.getType()
				.getMaxDurability();
		short current = item.getDurability();
		
		double difference = max - current;
		double percentage = difference / max;
		
		return percentage;
	}
	
	public static boolean is(ItemStack a, ItemStack b) {
		if (UtilJava.hasNull(a, b)) {
			return false;
		}
		
		return a.equals(b);
	}
	
	public static boolean isType(ItemStack a, ItemStack b) {
		if (UtilJava.hasNull(a, b)) {
			return false;
		}
		
		return a.getType() == b.getType() && a.getData().getData() == b.getData().getData();
	}
	
	public static boolean is(ItemStack item, Material material) {
		if (UtilJava.hasNull(item, material)) {
			return false;
		}
		
		return item.getType() == material;
	}
	
	public static boolean is(ItemStack item, Material material, byte data) {
		if (UtilJava.hasNull(item, material)) {
			return false;
		}
		
		@SuppressWarnings("deprecation")
		byte itemData = (byte) item.getTypeId();
		Material itemMat = item.getType();
		
		return material == itemMat && data == itemData;
	}
	
	public static void stripLore(ItemStack item) {
		if (item == null) {
			return;
		}
		
		if (!item.hasItemMeta()) {
			return;
		}
		
		ItemMeta meta = item.getItemMeta();
		if (!meta.hasDisplayName()) {
			return;
		}
		
		meta.setLore(new ArrayList<>());
		item.setItemMeta(meta);
	}
	
	public static void addLore(ItemStack item, List<String> lore) {
		ItemMeta meta = getMeta(item);
		List<String> currentLore = meta.getLore();
		
		if (currentLore == null) {
			currentLore = new ArrayList<>();
		}
		
		currentLore.addAll(lore);
		
		meta.setLore(currentLore);
		setMeta(item, meta);
	}
	
	public static void setUnbreakable(ItemStack item, boolean unbreakable) {
		ItemMeta meta = getMeta(item);
		if (meta != null) {
			meta.spigot()
					.setUnbreakable(unbreakable);
			setMeta(item, meta);
		}
	}
	
	/* Will return null if the */
	public static ItemMeta getMeta(ItemStack item) {
		if (item.hasItemMeta()) {
			return item.getItemMeta();
		} else {
			return CraftItemStack.getItemMeta(CraftItemStack.asNMSCopy(item));
		}
	}
	
	public static void setMeta(ItemStack item, ItemMeta meta) {
		item.setItemMeta(meta);
	}
	
	public static void setName(ItemStack item, String name) {
		ItemMeta meta = getMeta(item);
		meta.setDisplayName(C.cReset + name);
		setMeta(item, meta);
	}
	
	public static void setLore(ItemStack item, List<String> lore) {
		for (String str : lore) {
			str = C.cReset + str;
		}
		
		ItemMeta meta = getMeta(item);
		meta.setLore(lore);
		setMeta(item, meta);
	}
	
	@SuppressWarnings("deprecation")
	public static ItemStack create(Material type, int data, int amount, int damage, String displayName, List<String> lore) {
		ItemStack item = new ItemStack(type, amount, (short) damage, (byte) data);
		
		List<String> loreList = new ArrayList<>();
		for (String str : lore) {
			loreList.add(ChatColor.RESET + str);
		}
		
		ItemMeta meta = item.getItemMeta();
		
		if (displayName != null) {
			meta.setDisplayName(ChatColor.RESET + displayName);
		}
		
		if (lore != null) {
			meta.setLore(new ArrayList<String>(loreList));
		}
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public static ItemStack create(Material type, int data, String displayName, List<String> lore) {
		return create(type, data, 1, 0, displayName, lore);
	}
	
	public static ItemStack create(Material type, int data, String displayName) {
		return create(type, data, 1, 0, displayName, new ArrayList<>());
	}
	
	public static ItemStack create(Material type, int data) {
		return create(type, data, 1, 0, null, new ArrayList<>());
	}
	
	public static ItemStack create(Material type, String displayName, List<String> lore) {
		return create(type, 0, 1, 0, displayName, lore);
	}
	
	public static ItemStack create(Material type, String displayName) {
		return create(type, 0, 1, 0, displayName, new ArrayList<>());
	}
	
	public static ItemStack create(Material type) {
		return create(type, 0, 1, 0, null, new ArrayList<>());
	}
	
	public static int getSlot(int x, int y) {
		return ( 9 * y ) + x;
	}
	
}
