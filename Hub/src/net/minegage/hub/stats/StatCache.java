package net.minegage.hub.stats;


import net.minegage.common.java.SafeMap;
import net.minegage.common.module.PluginModule;
import net.minegage.common.util.UtilItem;
import net.minegage.core.stats.StatManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;


public class StatCache
		extends PluginModule {
		
	public StatManager statManager;
	
	public final SafeMap<String, String> statAliases = new SafeMap<>();
	public final List<GameStatToken> statTokens = new ArrayList<>();
	
	public StatCache(JavaPlugin plugin) {
		super("Stat Cache", plugin);
		
		statManager = new StatManager(plugin);
		
		statAliases.put("wins", "Wins");
		statAliases.put("losses", "Losses");
		statAliases.put("earned", "Total Score");
		statAliases.put("kills", "Kills");
		statAliases.put("deaths", "Deaths");
		
		addStat("XPWars", "XP Wars", Material.EXP_BOTTLE, 18);
		addStat("Skywars", "Skywars", Material.FEATHER, 20);
		addStat("Predator", "Predator", Material.COMPASS, 22);
		addStat("OITC_TDM", "One in the Chamber", Material.ARROW, 24);
		addStat("Survival_Games", "Survival Games", Material.COOKED_BEEF, 26);
		
		loadTables();
	}
	
	private void addStat(String tableName, String displayname, Material material, int slot) {
		ItemStack item = UtilItem.create(material, ChatColor.BLUE + displayname);
		GameStatToken statItem = new GameStatToken(tableName, displayname, item, slot);
		statTokens.add(statItem);
	}
	
	public void loadTables() {
		Connection connection = null;
		PreparedStatement getColumns = null;
		try {
			connection = DriverManager.getConnection(StatManager.CONNECTION_STRING, StatManager.USERNAME, StatManager.PASSWORD);
			StringBuilder tableList = new StringBuilder();
			Iterator<GameStatToken> i = statTokens.iterator();
			while (i.hasNext()) {
				GameStatToken next = i.next();
				tableList.append("'" + next.tableName + "'");
				
				if (i.hasNext()) {
					tableList.append(",");
				}
			}
			
			String query = "SELECT table_name,column_name FROM information_schema.columns " + "WHERE table_schema = '"
					+ StatManager.SCHEMA + "' " + "AND column_name != 'uid' " + "AND table_name IN (" + tableList.toString()
					+ ") " + ";";
					
			getColumns = connection.prepareStatement(query);
			
			// Buffer for storing query ResultSet
			SafeMap<String, List<String>> tableStats = new SafeMap<>();
			ResultSet columnResult = getColumns.executeQuery();
			
			while (columnResult.next()) {
				String table = columnResult.getString("table_name");
				String column = columnResult.getString("column_name");
				
				if (!tableStats.containsKey(table)) {
					tableStats.put(table, new ArrayList<>());
				}
				
				tableStats.get(table)
						.add(column);
			}
			
			// Cache the tables into StatManager
			for (Entry<String, List<String>> entry : tableStats.entrySet()) {
				String table = entry.getKey();
				List<String> stats = entry.getValue();
				
				statManager.addTable(table, stats);
			}
			
		} catch (SQLException ex) {
			System.out.println("Unable to cache tables; an SQLException occurred");
			ex.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
			
			if (getColumns != null) {
				try {
					getColumns.close();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	public GameStatToken getStatToken(String tableName) {
		for (GameStatToken statToken : statTokens) {
			if (statToken.tableName.equals(tableName)) {
				return statToken;
			}
		}
		return null;
	}
}
