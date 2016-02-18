package net.minegage.core.db;


import net.minegage.common.module.PluginModule;
import net.minegage.core.db.Currency.CurrencyType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.UUID;


/**
 * Created by kkworden on 7/24/15.
 */
public class DBManager
		extends PluginModule {
		
	public static File PLUGIN_DIR;
	
	public net.minegage.core.db.Currency currency;
	
	public DBManager(JavaPlugin plugin) {
		super("DBManager", plugin);
		DBManager.PLUGIN_DIR = this.getPlugin()
				.getDataFolder();
				
		// Initialize DataSets. //
		currency = new net.minegage.core.db.Currency();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(AsyncPlayerPreLoginEvent event) {
		currency.creditCurrency(event.getUniqueId(), CurrencyType.AESTHETIC, 100);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {}
	
	protected static File getPlayerDir(UUID id) {
		return new File(PLUGIN_DIR, "db" + File.separator + id.toString());
	}
}
