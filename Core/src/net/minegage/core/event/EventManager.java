package net.minegage.core.event;


import net.minegage.common.misc.Click;
import net.minegage.common.misc.Click.ClickButton;
import net.minegage.common.misc.Click.ClickTarget;
import net.minegage.common.module.PluginModule;
import net.minegage.common.util.UtilEvent;
import net.minegage.core.rank.Rank;
import net.minegage.core.rank.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;


public class EventManager
		extends PluginModule {

	public EventManager(JavaPlugin plugin) {
		super("Event Manager", plugin);
	}

	@EventHandler (priority = EventPriority.HIGH)
	public void filterLogin(PlayerLoginEvent event) {
		RankManager rankManager = RankManager.instance;
		Server      server      = Bukkit.getServer();
		Player      player      = event.getPlayer();

		if (server.hasWhitelist() && !rankManager.hasPermission(player, Rank.MODERATOR) && !player.isWhitelisted()) {
			event.disallow(Result.KICK_WHITELIST, ChatColor.YELLOW + "You must be on the whitelist to join!");
		}
	}

	/* Entity right/left clicks */
	private void callEntityClick(Entity clicked, Player clicker, ClickButton click) {
		EventClickEntity event = new EventClickEntity(clicked, clicker,
		                                              new Click(click, ClickTarget.AIR, clicker.isSneaking()));
		UtilEvent.call(event);
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onLeftClickEntity(EntityDamageByEntityEvent event) {
		Entity clicker = event.getDamager();

		if (!(clicker instanceof Player)) {
			return;
		}

		Entity clicked  = event.getEntity();
		Player pClicker = (Player) clicker;

		callEntityClick(clicked, pClicker, ClickButton.LEFT);
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onRightClickEntity(PlayerInteractEntityEvent event) {
		Player clicker = event.getPlayer();
		Entity clicked = event.getRightClicked();

		callEntityClick(clicked, clicker, ClickButton.RIGHT);
	}

	/**
	 * In some cases, PlayerInteractEvent will be cancelled by default. This uncancels the event at the lowest priority
	 * level to give the event predictable behaviour at higher priorities.
	 */
	@EventHandler (priority = EventPriority.LOWEST)
	public void correctPlayerInteract(PlayerInteractEvent event) {
		event.setUseItemInHand(Event.Result.ALLOW);
	}

	/**
	 * If the player has clicked and item and the event was cancelled, update their inventory, because the item may have
	 * been changed but not updated.
	 */
	@EventHandler (priority = EventPriority.MONITOR)
	public void updateInventory(PlayerInteractEvent event) {
		if (event.useItemInHand() == Event.Result.DENY && event.getItem() != null && event.getItem()
				                                                      .getType() != Material.AIR) {
			event.getPlayer()
					.updateInventory();
		}
	}

}
