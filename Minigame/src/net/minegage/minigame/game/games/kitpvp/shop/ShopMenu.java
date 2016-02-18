package net.minegage.minigame.game.games.kitpvp.shop;


import net.minegage.common.menu.Menu;
import net.minegage.common.menu.MenuManager;
import net.minegage.common.util.UtilItem;
import net.minegage.common.C;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.game.games.kitpvp.KitPvpBase;
import net.minegage.minigame.game.games.kitpvp.shop.purchase.Purchase;
import net.minegage.minigame.kit.Kit;
import net.minegage.minigame.stats.StatTracker;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;


public class ShopMenu
		extends Menu {
		
	public static final String RAW_NAME = "shop menu";
	
	private Game game;
	
	public ShopMenu(MenuManager manager, Game game) {
		super(manager, "container.inventory", RAW_NAME, null, 3);
		
		this.game = game;
		this.lockItems = false;
		
		addComponents();
	}
	
	public int getBalance(Player player) {
		return game.getStatTracker()
				.get(player, "kitpvp balance");
	}
	
	public void setBalance(Player player, int balance) {
		game.getStatTracker()
				.set(player, "kitpvp balance", balance);
	}
	
	@Override
	public void addComponents() {
		for (KitShopItem item : KitShopItem.values()) {
			addButton(item.getSlot(), new ShopButton(this, item));
		}
	}
	
	public KitPvpBase getKit(Player player) {
		Kit equippedKit = game.getKit(player);
		if (equippedKit == null || !( equippedKit instanceof KitPvpBase )) {
			throw new IllegalStateException("Player does not have KitPvpBase equipped");
		}
		
		return (KitPvpBase) equippedKit;
	}
	
	public void giveItems(Player player) {
		PlayerInventory inv = player.getInventory();
		
		for (KitShopItem shopItem : KitShopItem.values()) {
			String itemName = C.cBold + shopItem.getType() +  " " + C.cGreen + C.cBold + shopItem.getName();
			Material itemMaterial = shopItem.getDisplayType();
			int itemAmount = 1;
			
			List<String> itemLore = new ArrayList<>();
			
			Purchase purchase = null;
			if (shopItem.isUpgradeable()) {
				itemLore.add("");
				
				int level = game.getStatTracker()
						.get(player, getStat(shopItem));
				itemAmount = Math.max(1, level);
				
				purchase = getPurchase(player, shopItem);
				
				// Null purchase means no next upgrade (maximum level)
				if (purchase == null) {
					itemLore.add(C.cGreen + "Maximum level reached");
				} else {
					itemLore.add(C.cGray + "Next level: " + C.sOut + (level + 1 ));
				}
			}
			
			if (purchase != null) {
				int balance = getBalance(player);
				
				boolean affordable = balance >= purchase.getCost();
				String costPrefix = ( affordable ) ? C.cGreen : C.cRed;
				
				itemLore.add("");
				itemLore.add(C.cGray + "Costs " + costPrefix + purchase.getCost());
				itemLore.add(C.cGray + "Balance: " + C.cYellow + balance);
				
				if (purchase.hasDescription()) {
					itemLore.add("");
					itemLore.addAll(purchase.getDescription());
				}
			}
			
			itemLore.add("");
			
			ItemStack item = UtilItem.create(itemMaterial, itemName, itemLore);
			item.setAmount(itemAmount);
			UtilItem.addFlags(item, ItemFlag.HIDE_ATTRIBUTES);
			
			inv.setItem(shopItem.getSlot(), item);
		}
	}
	
	public String getStat(KitShopItem item) {
		return "level" + item.getName();
	}
	
	public Purchase getPurchase(Player player, KitShopItem item) {
		KitPvpBase kit = getKit(player);
		int nextLevel = game.getStatTracker()
				.get(player, getStat(item)) + 1;
				
		return kit.getUpgrade(item, nextLevel);
	}
	
	public boolean attemptBuy(Player player, KitShopItem item) {
		Purchase purchase = getPurchase(player, item);
		if (purchase == null) {
			return false;
		}
		
		int cost = purchase.getCost();
		int balance = getBalance(player);
		if (balance < cost) {
			return false;
		}
		
		game.getStatTracker()
				.increment(player, getStat(item), 1);
				
		setBalance(player, balance - cost);
		
		purchase.apply(player);
		
		giveItems(player);
		player.updateInventory();
		
		return true;
	}
	
	public void resetLevel(Player player) {
		StatTracker stats = game.getStatTracker();
		for (KitShopItem item : KitShopItem.values()) {
			stats.set(player, getStat(item), 0);
		}
	}
	
	/* Unused super methods */
	@Override
	public void addItems(Player player, Inventory inventory) {
		// Do nothing; this will never be called
	}
	
	@Override
	public void open(Player player) {
		throw new IllegalArgumentException("Cannot open ShopMenu");
	}
	
	
	
	
	
}
