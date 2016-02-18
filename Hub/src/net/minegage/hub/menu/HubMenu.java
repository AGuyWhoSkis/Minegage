package net.minegage.hub.menu;


import net.minegage.common.java.SafeMap;
import net.minegage.common.menu.MenuManager;
import net.minegage.common.module.PluginModule;
import net.minegage.common.server.ServerManager;
import net.minegage.common.timer.Timer;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilInv;
import net.minegage.common.util.UtilItem;
import net.minegage.common.C;
import net.minegage.core.equippable.EquipManager;
import net.minegage.core.equippable.MenuEquip;
import net.minegage.core.rank.Rank;
import net.minegage.core.rank.RankManager;
import net.minegage.hub.HubManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;


public class HubMenu
		extends PluginModule {
		
	// TODO: Stats viewing disabled until backend completed
	
	private HubManager hubManager;
	private MenuManager menuManager;
	private MenuDestination destinationMenu;
	private EquipManager equipManager;
	// private StatCache statCache;
	
	private ItemStack destinationItem;
	private ItemStack equipItem;
	// private ItemStack statsItem;
	private ItemStack vanishPlayersItem;
	private ItemStack unvanishPlayersItem;
	
	private final int destinationItemSlot = 0;
	private final int equipItemSlot = 4;
	private final int visibilitySlot = 8;
	
	// Slots of items
	private SafeMap<Integer, ItemStack> items = new SafeMap<>();
	
	public HubMenu(JavaPlugin plugin, ServerManager serverManager, MenuManager menuManager, EquipManager equipManager, HubManager hubManager) {
		super("HubPlugin Menu", plugin);
		
		this.hubManager = hubManager;
		this.menuManager = menuManager;
		this.equipManager = equipManager;
		// this.statCache = statCache;
		
		this.destinationItem = UtilItem.create(Material.EYE_OF_ENDER, C
				.fMain("Destination", C.cReset + "click to start playing!"));
		this.equipItem = UtilItem.create(Material.CHEST, C.fMain("Perks", C.cReset + "click to open!"));
		// this.statsItem = UtilItem.create(Material.PAPER, C.fMain("Stats", C.cReset + "click to
		// view!"));
		
		this.vanishPlayersItem = UtilItem.create(Material.INK_SACK, 10, C
				.fMain("Players " + C.cGreen + "visible", C.cReset + "click to toggle!"));
		this.unvanishPlayersItem = UtilItem.create(Material.INK_SACK, 8, C
				.fMain("Players " + C.cGray + "reduced", C.cReset + "click to toggle!"));
		
		this.destinationMenu = new MenuDestination(menuManager, serverManager);
		
		items.put(destinationItemSlot, destinationItem);
		items.put(equipItemSlot, equipItem);
		items.put(visibilitySlot, vanishPlayersItem);
		items.put(visibilitySlot, unvanishPlayersItem);
	}
	
	@EventHandler
	public void addMenuItems(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PlayerInventory inventory = player.getInventory();
		UtilInv.clear(inventory);
		
		inventory.setItem(destinationItemSlot, destinationItem);
		inventory.setItem(equipItemSlot, equipItem);
		inventory.setItem(visibilitySlot, vanishPlayersItem);
	}
	
	@EventHandler
	public void openMenu(PlayerInteractEvent event) {
		if (!UtilEvent.isClick(event)) {
			return;
		}
		
		ItemStack clicked = event.getItem();
		if (clicked == null) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if (clicked.equals(destinationItem)) {
			menuManager.open(player, MenuDestination.RAW_NAME);
		} else if (clicked.equals(equipItem)) {
			menuManager.open(player, MenuEquip.RAW_NAME);
		} else if (clicked.equals(vanishPlayersItem)) {
			
			if (Timer.instance.use(player, "Visibility", "Toggle Players", 3000L, true)) {
				hubManager.vanishOthers(player);
				player.getInventory()
						.setItem(visibilitySlot, unvanishPlayersItem);
			}
			
		} else if (clicked.equals(unvanishPlayersItem)) {
			if (Timer.instance.use(player, "Visibility", "Toggle Players", 3000L, true)) {
				hubManager.unvanishOthers(player);
				player.getInventory()
						.setItem(visibilitySlot, vanishPlayersItem);
			}
		} else {
			return;
		}
		
		event.setCancelled(true);
	}
	
	@EventHandler
	public void orderItems(PlayerDropItemEvent event) {
		if (!RankManager.instance.hasPermission(event.getPlayer(), Rank.ADMIN)) {
			UtilEvent.orderItems(event, items);
		}
	}
	
	@EventHandler
	public void itemMove(InventoryClickEvent event) {
		if (!RankManager.instance.hasPermission((Player) event.getWhoClicked(), Rank.ADMIN)) {
			UtilEvent.lockItem(event, items.values());
		}
	}
	
	public MenuDestination getDestinationMenu() {
		return destinationMenu;
	}
	
	public EquipManager getEquipManager() {
		return equipManager;
	}
	
	
}
