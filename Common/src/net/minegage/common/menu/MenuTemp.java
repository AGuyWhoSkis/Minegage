package net.minegage.common.menu;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;


public abstract class MenuTemp
		extends Menu {

	private static int invId = 0;

	protected Player player;
	
	public MenuTemp(MenuManager manager, String title, String prevRawName, Player player) {
		super(manager, title, "tempinv" + invId++, prevRawName);
		this.player = player;
	}
	
	public MenuTemp(MenuManager manager, String title, String prevRawName, int rows, Player player) {
		this(manager, title, prevRawName, player);
		this.rows = rows;
	}
	
	public MenuTemp(MenuManager manager, String title, String prevRawName, InventoryType type, Player player) {
		this(manager, title, prevRawName, player);
		this.type = type;
	}
	
	@Override
	protected boolean isOpened(Player player, Inventory inventory) {
		return this.recentInventory.equals(inventory);
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		if (!event.getInventory()
				.equals(recentInventory)) {
			return;
		}
		
		if (!event.getPlayer()
				.equals(player)) {
			return;
		}
		
		dispose();
	}
	
}
