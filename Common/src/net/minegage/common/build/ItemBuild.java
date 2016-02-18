package net.minegage.common.build;


import net.minegage.common.util.UtilArmour;
import net.minegage.common.util.UtilItem;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ItemBuild {
	
	public static ItemBuild create(Material type) {
		return new ItemBuild(type);
	}

	public static ItemBuild pot(PotionType type, int level) { return new ItemBuild(Material.POTION).potion(new Potion(type, level)); }
	
	private ItemStack item;
	
	public ItemBuild(Material type) {
		this(new ItemStack(type));
	}
	
	public ItemBuild(ItemStack item) {
		this.item = item;
	}
	
	public ItemBuild type(Material type) {
		item.setType(type);

		return this;
	}
	
	@SuppressWarnings("deprecation")
	public ItemBuild data(byte data) {
		MaterialData matData = item.getData();
		matData.setData(data);
		item.setData(matData);
		
		return this;
	}
	
	public ItemBuild durability(short durability) {
		item.setDurability(durability);
		return this;
	}
	
	public ItemBuild enchant(Enchantment enchant, int level) {
		item.addUnsafeEnchantment(enchant, level);
		return this;
	}
	
	public ItemBuild enchant(Enchantment enchant) {
		return enchant(enchant, 1);
	}
	
	public ItemBuild name(String name) {
		UtilItem.setName(item, name);
		return this;
	}
	
	public ItemBuild lore(String... lore) {
		List<String> loreList = new ArrayList<>(Arrays.asList(lore));
		UtilItem.addLore(item, loreList);
		return this;
	}
	
	public ItemBuild amount(int amount) {
		item.setAmount(amount);
		return this;
	}
	
	public ItemBuild unbreakable() {
		UtilItem.setUnbreakable(item, true);
		return this;
	}
	
	public ItemBuild potion(Potion potion) {
		type(Material.POTION);

		potion.apply(item);
		return this;
	}
	
	public ItemBuild potion(PotionType type) {
		return potion(new Potion(type));
	}
	
	public ItemBuild colour(Color colour) {
		UtilArmour.colourArmour(colour, item);
		return this;
	}
	
	public ItemStack item() {
		return item;
	}
	
}
