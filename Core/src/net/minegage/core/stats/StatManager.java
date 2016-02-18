package net.minegage.core.stats;


import net.minegage.common.java.SafeMap;
import net.minegage.common.module.PluginModule;
import net.minegage.core.stats.column.Column;
import net.minegage.core.stats.column.ColumnInt;
import net.minegage.core.stats.column.ColumnVarChar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;


public class StatManager
		extends PluginModule {

	private int SAVE_TASK = -1;

	public static StatManager instance;

	public static final String SCHEMA = "mc_stats";
	public static final String CONNECTION_STRING = "jdbc:mysql://lebroncraft.com:3306/" + SCHEMA
	                                               + "?autoReconnect=true&failOverReadOnly=false&maxReconnects=10";
	public static final String USERNAME = "mc_db";
	public static final String PASSWORD = "CMSLYH2tVzFpdsP8";

	public SafeMap<String, Table> tables = new SafeMap<>();
	public SafeMap<String, SafeMap<UUID, Row>> cache = new SafeMap<>();

	protected long saveIntervalTicks = 300L;
	protected SafeMap<String, SafeMap<UUID, Row>> queue = new SafeMap<>();
	protected Object queueLock = new Object();

	public StatManager(JavaPlugin plugin) {
		super("Stat Manager", plugin);
		StatManager.instance = this;
	}

	@Override
	public void onDisable() {
		tables.clear();
		cache.clear();

		synchronized (queueLock) {
			queue.clear();
		}
	}

	public void scheduleSaveTask() {
		runAsyncTimer(saveIntervalTicks, saveIntervalTicks, new BukkitRunnable() {
			@Override
			public void run() {
				saveStats();
			}
		});
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		add(event.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		UUID uid = player.getUniqueId();

		for (Entry<String, SafeMap<UUID, Row>> entry : cache.entrySet()) {
			entry.getValue()
					.remove(uid);
		}
	}

	public void add(Player player) {
		ColumnVarChar uidColumn = new ColumnVarChar("uid", 36);
		uidColumn.value = player.getUniqueId()
				.toString();

		List<Column<?>> whereColumns = new ArrayList<>();
		whereColumns.add(uidColumn);

		for (Entry<String, Table> entry : tables.entrySet()) {
			Row newRow = entry.getValue()
					.fetch(whereColumns);

			if (newRow == null || newRow.columns == null || newRow.columns.size() == 0) {
				newRow = new Row();
				newRow.columns = entry.getValue()
						.getColumnsCopy();

				((ColumnVarChar) newRow.getColumn("uid")).value = player.getUniqueId()
						.toString();
			}

			this.cache.get(entry.getKey())
					.put(player.getUniqueId(), newRow);
		}
	}

	public void addTable(String name, List<String> statNames) {
		List<Column<?>> columnList  = new ArrayList<>();
		List<Column<?>> primaryList = new ArrayList<>();

		ColumnVarChar uid = new ColumnVarChar("uid", 36);

		columnList.add(uid);
		for (String columnName : statNames) {
			columnList.add(new ColumnInt(columnName));
		}

		primaryList.add(uid);

		Table table = new Table(name, columnList, primaryList, uid);
		tables.put(name, table);
		cache.put(name, new SafeMap<UUID, Row>());
	}

	public void removeTable(String name) {
		saveStats();

		synchronized (queueLock) {
			queue.remove(name);
			cache.remove(name);
			tables.remove(name);
		}
	}

	// Old
	// public void incrementStat(String tableName, String statName, UUID uid, int increment) throws
	// SQLException {
	// if (!cache.containsKey(tableName)) {
	// throw new SQLException("Table " + tableName + " not found in database \"" + SCHEMA + "\"");
	// }
	//
	// SafeMap<UUID, Row> data = cache.get(tableName);
	//
	// if (!data.containsKey(uid)) {
	// Row newRow = new Row();
	// newRow.columns = tables.get(tableName)
	// .getColumnsCopy();
	//
	// ColumnVarChar uidColumn = (ColumnVarChar) newRow.getColumn("uid");
	// uidColumn.value = uid.toString();
	//
	// data.put(uid, newRow);
	// }
	//
	// Row row = data.get(uid);
	// ColumnInt column = (ColumnInt) row.getColumn(statName);
	//
	// if (column == null) {
	// throw new SQLException("Column \"" + statName + "\" not found in table \"" + tableName + "\"
	// of schema \"" + SCHEMA
	// + "\"");
	// }
	//
	// column.value += increment;
	//
	// synchronized (queueLock) {
	// if (!queue.containsKey(tableName)) {
	// queue.put(tableName, new SafeMap<UUID, Row>());
	// }
	//
	// SafeMap<UUID, Row> tableQueue = queue.get(tableName);
	// tableQueue.put(uid, row);
	// }
	// }

	public int getStat(String tableName, String statName, UUID uid)
			throws SQLException {
		Row       row       = getRow(tableName, uid);
		ColumnInt columnInt = getColumnInt(row, statName);

		return columnInt.value;
	}

	public void setStat(String tableName, String statName, UUID uid, int value)
			throws SQLException {
		Row       row       = getRow(tableName, uid);
		ColumnInt columnInt = getColumnInt(row, statName);
		columnInt.value = value;

		queue(tableName, row, uid);
	}

	public void incrementStat(String tableName, String statName, UUID uid, int increment)
			throws SQLException {
		Row       row       = getRow(tableName, uid);
		ColumnInt columnInt = getColumnInt(row, statName);

		columnInt.value += increment;

		queue(tableName, row, uid);
	}

	public ColumnInt getColumnInt(Row row, String statName) {
		Column<?> column = row.getColumn(statName);

		if (!(column instanceof ColumnInt)) {
			throw new IllegalArgumentException("Invalid Column type; must be ColumnInt");
		}

		return (ColumnInt) column;
	}

	public Row getRow(String tableName, UUID uid)
			throws SQLException {
		SafeMap<UUID, Row> data = cache.get(tableName);

		if (data == null) {
			throw new SQLException("Table " + tableName + " not found in database \"" + SCHEMA + "\"");
		}

		if (!data.containsKey(uid)) {
			Row newRow = new Row();
			newRow.columns = tables.get(tableName)
					.getColumnsCopy();

			ColumnVarChar uidColumn = (ColumnVarChar) newRow.getColumn("uid");
			uidColumn.value = uid.toString();

			data.put(uid, newRow);
		}

		Row row = data.get(uid);
		return row;
	}

	public void queue(String tableName, Row row, UUID uid) {
		synchronized (queueLock) {
			if (!queue.containsKey(tableName)) {
				queue.put(tableName, new SafeMap<UUID, Row>());
			}

			SafeMap<UUID, Row> tableQueue = queue.get(tableName);
			tableQueue.put(uid, row);
		}
	}


	public void saveStats() {
		synchronized (queueLock) {

			for (String tableName : queue.keySet()) {
				Table              table = tables.get(tableName);
				SafeMap<UUID, Row> rows  = queue.get(tableName);

				for (Entry<UUID, Row> entry : rows.entrySet()) {
					Row row = entry.getValue();
					table.insert(row);
				}
			}

			queue.clear();
		}
	}

}
