package net.minegage.minigame.game.games.kitpvp;


import net.minegage.common.java.SafeMap;
import net.minegage.common.util.UtilArmour.ArmourSlot;
import net.minegage.minigame.game.games.kitpvp.shop.KitShopItem;
import net.minegage.minigame.game.games.kitpvp.shop.ShopMenu;
import net.minegage.minigame.game.games.kitpvp.shop.purchase.EnchantPurchase;
import net.minegage.minigame.game.games.kitpvp.shop.purchase.EnchantPurchase.EnchantToken;
import net.minegage.minigame.game.games.kitpvp.shop.purchase.Purchase;
import net.minegage.minigame.kit.Kit;
import net.minegage.minigame.kit.attrib.Attrib;
import net.minegage.minigame.kit.attrib.AttribThrowableTNT;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;


public abstract class KitPvpBase
		extends Kit {
		
	protected ShopMenu menu;
	
	protected SafeMap<KitShopItem, List<Purchase>> purchases = new SafeMap<>();
	
	public KitPvpBase(String name, String[] desc, Attrib... attributes) {
		super(name, desc, attributes);
		addAttribute(new AttribThrowableTNT());
	}
	
	protected void addWeaponUpgrades(Material weapon, int... costs) {
		for (int i = 0; i < costs.length; i++) {
			EnchantToken weaponToken = new EnchantToken(Enchantment.DAMAGE_ALL, 1, weapon);
			EnchantToken bowToken    = new EnchantToken(Enchantment.ARROW_DAMAGE, 1, Material.BOW);
			int          cost        = costs[i];
			
			EnchantPurchase upgrade = new EnchantPurchase(cost, new String[] {"+1 (sword + bow)" }, weaponToken, bowToken);
			addUpgrade(KitShopItem.ATTACK, upgrade);
		}
	}
	
	protected void addSwordUpgrades(Material sword, int... costs) {
		for (int i = 0; i < costs.length; i++) {
			EnchantToken weaponToken = new EnchantToken(Enchantment.DAMAGE_ALL, 1, sword);
			int cost = costs[i];
			
			EnchantPurchase upgrade = new EnchantPurchase(cost, new String[] { "+1 Sharpness" }, weaponToken);
			addUpgrade(KitShopItem.ATTACK, upgrade);
		}
	}
	
	protected void addBowUpgrades(int... costs) {
		for (int i = 0; i < costs.length; i++) {
			EnchantToken bowToken = new EnchantToken(Enchantment.ARROW_DAMAGE, 1, Material.BOW);
			int cost = costs[i];
			
			EnchantPurchase upgrade = new EnchantPurchase(cost, new String[] { "+1 Power" }, bowToken);
			addUpgrade(KitShopItem.ATTACK, upgrade);
		}
	}
	
	protected void addArmourUpgrades(int... costs) {
		for (int i = 0; i < costs.length; i++) {
			int cost = costs[i];
			EnchantPurchase upgrade = new EnchantPurchase(cost, new String[] { "+1 Protection" });
			
			for (ArmourSlot slot : ArmourSlot.values()) {
				EnchantToken token = new EnchantToken(Enchantment.PROTECTION_ENVIRONMENTAL, 1, slot.getSlot());
				upgrade.getEnchantments()
						.add(token);
			}
			
			addUpgrade(KitShopItem.DEFENSE, upgrade);
		}
	}
	
	public void setShop(ShopMenu menu) {
		this.menu = menu;
	}
	
	@Override
	protected void giveItems(PlayerInventory inv) {
		Player player = (Player) inv.getHolder();
		menu.resetLevel(player);
		menu.giveItems(player);
		
		for (int i = 0; i < 9; i++) {
			if (inv.getItem(i) == null) {
				inv.addItem(KitShopItem.SOUP_ITEM);
			}
		}
		
	}
	
	protected void addUpgrade(KitShopItem item, Purchase purchase) {
		List<Purchase> itemPurchases = purchases.getOrDefault(item, new ArrayList<>());
		itemPurchases.add(purchase);
		purchases.put(item, itemPurchases);
	}
	
	/**
	 * @param level
	 *        The level of the upgrade to be purchased
	 */
	public Purchase getUpgrade(KitShopItem item, int level) {
		List<Purchase> upgrades = purchases.get(item);
		if (upgrades != null) {
			int index = level - 1;
			
			if (index < upgrades.size()) {
				return upgrades.get(level - 1);
			}
		}
		
		return null;
	}
	
}
