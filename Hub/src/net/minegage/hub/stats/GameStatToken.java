package net.minegage.hub.stats;


import org.bukkit.inventory.ItemStack;


public class GameStatToken {
	
	public String tableName;
	public String displayName;
	
	public ItemStack item;
	public int slot;
	
	public GameStatToken(String tableName, String displayName, ItemStack item, int slot) {
		this.tableName = tableName;
		this.displayName = displayName;
		this.item = item;
		this.slot = slot;
	}
	
}
