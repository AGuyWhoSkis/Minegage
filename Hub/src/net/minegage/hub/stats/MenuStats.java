package net.minegage.hub.stats;


import net.minegage.common.java.SafeMap;
import net.minegage.common.menu.MenuManager;
import net.minegage.common.menu.MenuTemp;
import net.minegage.core.stats.Row;
import net.minegage.core.stats.column.Column;
import net.minegage.core.stats.column.ColumnInt;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;


public class MenuStats
		extends MenuTemp {
		
	protected StatCache statCache;
	
	public MenuStats(MenuManager menuManager, StatCache statCache, Player player) {
		super(menuManager, "Stats", "close", 4, player);
		this.statCache = statCache;
		addComponents();
	}
	
	@Override
	public void addComponents() {
		// Do nothing
	}
	
	@Override
	public void addItems(Player player, Inventory inv) {
		// <Game, Player stat>
		SafeMap<String, Row> stats = new SafeMap<>();
		for (Entry<String, SafeMap<UUID, Row>> entry : statCache.statManager.cache.entrySet()) {
			stats.put(entry.getKey(), entry.getValue()
					.get(player.getUniqueId()));
		}
		
		for (GameStatToken gameStatToken : statCache.statTokens) {
			String tableName = gameStatToken.tableName;
			
			Row playerStats = stats.get(tableName);
			List<String> lore = getStatLore(playerStats);
			
			GameStatToken statToken = statCache.getStatToken(tableName);
			
			ItemStack item = statToken.item;
			ItemStack itemCopy = new ItemStack(item);
			
			int slot = statToken.slot;
			
			ItemMeta meta = itemCopy.getItemMeta();
			meta.setLore(lore);
			itemCopy.setItemMeta(meta);
			
			inv.setItem(slot, itemCopy);
		}
	}
	
	public List<String> getStatLore(Row row) {
		List<String> lore = new ArrayList<>();
		
		for (Column<?> column : row.columns) {
			if (column instanceof ColumnInt) {
				ColumnInt stat = (ColumnInt) column;
				String displayName = statCache.statAliases.getOrDefault(stat.name, stat.name);
				lore.add(ChatColor.GREEN + displayName + ": " + ChatColor.WHITE + stat.value);
			}
		}
		return lore;
	}
}
