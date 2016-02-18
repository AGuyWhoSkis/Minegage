package net.minegage.common.menu;


import net.minegage.common.java.SafeMap;
import net.minegage.common.menu.button.Button;
import net.minegage.common.menu.button.ButtonBack;
import net.minegage.common.misc.Click;
import net.minegage.common.token.SoundToken;
import net.minegage.common.util.UtilItem;
import net.minegage.common.util.UtilSound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map.Entry;


public abstract class Menu
		implements Listener {
		
	protected SafeMap<Integer, Button> buttons = new SafeMap<>();
	protected SafeMap<Integer, ItemStack> defaultItems = new SafeMap<>();
	
	protected InventoryType type = InventoryType.CHEST;
	protected MenuManager menuManager;
	protected Inventory recentInventory;
	protected String title;
	protected String rawName;
	protected int rows = 0;
	protected String prevRawName;
	protected boolean lockItems = true;
	
	Menu(MenuManager manager, String title, String rawName, String prevRawName) {
		this.menuManager = manager;
		this.title = title;
		this.rawName = rawName;
		this.prevRawName = prevRawName;
		
		if (prevRawName != null) {
			addBackButton();
		}
		
		manager.menus.add(this);
		manager.registerEvents(this);
	}
	
	public Menu(MenuManager manager, String title, String rawName, String prevRawName, int rows) {
		this(manager, title, rawName, prevRawName);
		this.rows = rows;
	}
	
	public Menu(MenuManager manager, String title, String rawName, String prevRawName, InventoryType type) {
		this(manager, title, rawName, prevRawName);
		this.type = type;
	}
	
	/**
	 * Registers components to items
	 */
	public abstract void addComponents();
	
	/**
	 * Adds items to the inventory. Called when the inventory is opened
	 */
	public abstract void addItems(Player player, Inventory inventory);
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Inventory clInv = event.getClickedInventory();
		
		if (clInv == null) {
			return;
		}
		
		if (isOpened(player, clInv)) {
			Button clicked = buttons.get(event.getSlot());
			
			if (clicked != null) {
				Click   click   = Click.from(event);
				boolean success = clicked.onClick(player, click);
				
				SoundToken sound;
				if (success) {
					sound = clicked.successful;
				} else {
					sound = clicked.unsuccessful;
				}
				
				if (sound != null) {
					UtilSound.playLocal(player, sound);
				}
				
				event.setCancelled(true);
			} else if (lockItems) {
				event.setCancelled(true);
			}
		}
	}
	
	/**
	 * @param player
	 *        The player who opened the inventory
	 */
	protected boolean isOpened(Player player, Inventory inventory) {
		return inventory.getTitle()
				.equals(this.title);
	}
	
	public void addBackButton() {
		ButtonBack button  = new ButtonBack(this);
		String     display = ( prevRawName.equals("close") ) ? ChatColor.RED + "Close" : ChatColor.BLUE + "Previous page";
		ItemStack  item    = UtilItem.create(Material.BARRIER, 0, display);
		addButton(0, button, item);
	}
	
	public void open(Player player) {
		if (rows > 0) {
			recentInventory = Bukkit.createInventory(null, ( rows * 9 ), title);
		} else {
			recentInventory = Bukkit.createInventory(null, type, title);
		}
		
		for (Entry<Integer, ItemStack> entry : defaultItems.entrySet()) {
			recentInventory.setItem(entry.getKey(), entry.getValue());
		}
		
		addItems(player, recentInventory);
		player.openInventory(recentInventory);
	}
	
	public void addButton(int slot, Button button) {
		buttons.put(slot, button);
	}
	
	public void addButton(int slot, Button button, ItemStack item) {
		addButton(slot, button);
		addDefaultItem(slot, item);
	}
	
	public void addDefaultItem(int slot, ItemStack item) {
		defaultItems.put(slot, item);
	}
	
	public int getSlot(int column, int row) {
		return ( 9 * row ) + column;
	}
	
	public SafeMap<Integer, Button> getButtons() {
		return buttons;
	}
	
	public SafeMap<Integer, ItemStack> getDefaultItems() {
		return defaultItems;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getRawName() {
		return rawName;
	}
	
	public int getRows() {
		return rows;
	}
	
	public String getPrevRawName() {
		return prevRawName;
	}
	
	public MenuManager getMenuManager() {
		return menuManager;
	}
	
	public void dispose() {
		recentInventory = null;
		menuManager.unregisterEvents(this);
		menuManager.menus.remove(this);
	}
	
}
