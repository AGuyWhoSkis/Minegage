package net.minegage.core.condition;


import net.minegage.common.module.PluginModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;


public class VisibilityManager
		extends PluginModule {
		
	private Set<Player> vanishedPlayers = new HashSet<>();
	
	public VisibilityManager(JavaPlugin plugin) {
		super("Visibility Manager", plugin);
	}
	
	public void setVanished(Player player, boolean vanished) {
		if (vanished) {
			for (Player other : plugin.getServer()
					.getOnlinePlayers()) {
				other.hidePlayer(player);
			}
			vanishedPlayers.add(player);
		} else {
			for (Player other : plugin.getServer()
					.getOnlinePlayers()) {
				other.showPlayer(player);
			}
			vanishedPlayers.remove(player);
		}
	}
	
	public boolean isVanished(Player player) {
		return vanishedPlayers.contains(player);
	}
	
	@EventHandler
	public void vanishPlayers(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		for (Player other : vanishedPlayers) {
			player.hidePlayer(other);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void clearReference(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		vanishedPlayers.remove(player);
	}
	
}
