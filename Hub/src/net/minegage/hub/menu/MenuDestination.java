package net.minegage.hub.menu;


import net.minegage.common.build.ItemBuild;
import net.minegage.common.menu.Menu;
import net.minegage.common.menu.MenuManager;
import net.minegage.common.menu.button.ButtonCommand;
import net.minegage.common.menu.button.ButtonServer;
import net.minegage.common.server.ServerManager;
import net.minegage.common.util.UtilItem;
import net.minegage.common.C;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class MenuDestination
		extends Menu {
		
	public static final String RAW_NAME = "destination";
	
	private ServerManager serverManager;
	
	public MenuDestination(MenuManager manager, ServerManager serverManager) {
		super(manager, "Select your destination!", RAW_NAME, "close", 5);
		
		this.serverManager = serverManager;
		
		addComponents();
	}
	
	@Override
	public void addComponents() {
		// Removed for downscale update, will need in future
		// addQueue(getSlot(0, 2), "XP Wars", Material.EXP_BOTTLE);
		// addQueue(getSlot(2, 2), "Skywars", Material.FEATHER);
		// addQueue(getSlot(4, 2), "Predator", Material.COMPASS);
		// addQueue(getSlot(6, 2), "OITC TDM", Material.ARROW);
		// addQueue(getSlot(8, 2), "Survival Games", Material.COOKED_BEEF);
		
		addServer(getSlot(3, 1), "Kit PVP", "kitpvp", Material.IRON_SWORD);
		addServer(getSlot(5, 1), "Paintball", "paintball", Material.SNOW_BALL);
		
		addServer(getSlot(1, 3), "Creative", "creative", Material.WORKBENCH);
		addServer(getSlot(3, 3), "Skyblock", "skyblock", Material.GRASS);
		addServer(getSlot(5, 3), "OP Prison", "prison", Material.DIAMOND_PICKAXE);
		
		addServer(getSlot(7, 3), "Factions", "factions", ItemBuild.create(Material.MONSTER_EGG)
				.durability((short) 50)
				.item());
				
		// ItemStack removeItem = UtilItem.create(Material.REDSTONE_BLOCK, ChatColor.BLUE + "Leave
		// queue");
		
		// ButtonCommand removeButton = new ButtonCommand("queue remove");
		// addButton(getSlot(4, 0), removeButton, removeItem);
	}
	
	@Override
	public void addItems(Player player, Inventory inventory) {
		// Do nothing
	}
	
	@SuppressWarnings("unused")
	private void addQueue(int slot, String name, Material material) {
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GREEN + "Click " + ChatColor.WHITE + "to join " + ChatColor.YELLOW + name + ChatColor.WHITE + "!");
		lore.add("");
		
		ItemStack     item   = UtilItem.create(material, ChatColor.BLUE + name, lore);
		ButtonCommand button = new ButtonCommand("queue " + name);
		
		addButton(slot, button, item);
	}
	
	private void addServer(int slot, String displayName, String serverName, ItemStack item) {
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GREEN + "Click " + ChatColor.WHITE + "to play " + ChatColor.YELLOW + displayName + ChatColor.WHITE
				+ "!");
		lore.add("");
		
		UtilItem.setLore(item, lore);
		UtilItem.setName(item, C.cBold + displayName);
		
		UtilItem.addFlags(item, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
		
		ButtonServer button = new ButtonServer(serverManager, serverName);
		
		addButton(slot, button, item);
	}
	
	private void addServer(int slot, String displayName, String serverName, Material material) {
		addServer(slot, displayName, serverName, UtilItem.create(material));
	}
	
}
