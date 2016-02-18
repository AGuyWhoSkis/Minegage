package net.minegage.minigame.game.games.kitpvp.shop.purchase;


import net.minegage.common.util.UtilItem;
import net.minegage.minigame.game.games.kitpvp.shop.KitShopItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EnchantPurchase
		extends Purchase {
		
	private List<EnchantToken> enchantments;
	
	public EnchantPurchase(int cost, String[] description, EnchantToken... enchantments) {
		super(cost, description);
		this.enchantments = new ArrayList<>(Arrays.asList(enchantments));
	}
	
	public EnchantPurchase(int cost, String[] description, Enchantment enchantment, int enchantLevel, Material itemType) {
		this(cost, description, new EnchantToken(enchantment, enchantLevel, itemType));
	}
	
	public List<EnchantToken> getEnchantments() {
		return enchantments;
	}
	
	@Override
	public void apply(Player player) {
		PlayerInventory inv = player.getInventory();
		
		for (EnchantToken token : enchantments) {
			token.apply(inv);
		}
	}
	
	public static class EnchantToken {
		
		private Enchantment enchantment;
		private int level;
		
		private int slot;
		private Material itemType;
		
		private EnchantToken(Enchantment enchantment, int level) {
			this.enchantment = enchantment;
			this.level = level;
		}
		
		public EnchantToken(Enchantment enchantment, int level, Material itemType) {
			this(enchantment, level);
			this.itemType = itemType;
		}
		
		public EnchantToken(Enchantment enchantment, int level, int slot) {
			this(enchantment, level);
			this.slot = slot;
		}
		
		public Enchantment getEnchantment() {
			return enchantment;
		}
		
		public int getLevel() {
			return level;
		}
		
		public Material getItemType() {
			return itemType;
		}
		
		public int getSlot() {
			return slot;
		}
		
		public void apply(PlayerInventory inventory) {
			int slot = -1;
			for (ItemStack item : inventory.getContents()) {
				slot++;
				if (isShopItem(slot)) {
					continue;
				}
				
				if (applies(item, slot)) {
					apply(item);
					return;
				}
			}
			for (ItemStack item : inventory.getArmorContents()) {
				slot++;
				if (applies(item, slot)) {
					apply(item);
					return;
				}
			}
		}
		
		private void apply(ItemStack item) {
			int newLevel = item.getEnchantmentLevel(enchantment) + level;
			
			item.removeEnchantment(enchantment);
			item.addUnsafeEnchantment(enchantment, newLevel);
		}
		
		private boolean applies(ItemStack item, int slot) {
			if (this.itemType == null) {
				return this.slot == slot;
			} else {
				return UtilItem.is(item, this.itemType);
			}
		}
		
		private boolean isShopItem(int slot) {
			for (KitShopItem item : KitShopItem.values()) {
				if (item.getSlot() == slot) {
					return true;
				}
			}
			return false;
		}
		
	}
	
}
