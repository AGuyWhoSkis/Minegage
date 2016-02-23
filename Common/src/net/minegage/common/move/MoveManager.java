package net.minegage.common.move;


import net.minegage.common.java.SafeMap;
import net.minegage.common.module.PluginModule;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilMath;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.Map.Entry;


public class MoveManager
		extends PluginModule {
		
	private SafeMap<Player, MoveToken> playerMovement = new SafeMap<>();

	public MoveManager(JavaPlugin plugin) {
		super("Move Manager", plugin);
	}
	
	@EventHandler
	public void moveCheck(TickEvent event) {
		if (event.isNot(Tick.TICK_1)) {
			return;
		}

		long currentMillis = System.currentTimeMillis();

		for (Entry<Player, MoveToken> entry : playerMovement.entrySet()) {
			Player    player   = entry.getKey();
			MoveToken token = entry.getValue();

			// Movement
			Location lastLocation = token.lastLocation;
			Location currentLocation = player.getLocation();
			
			if (UtilMath.offsetSq(lastLocation, currentLocation) > 0.0) {
				token.lastMoved = currentMillis;
				token.lastPhysicalMoved = currentMillis;
				token.lastLocation = currentLocation;
			}

			if (!UtilJava.equals(currentLocation.getYaw(), lastLocation.getYaw(), 0.0001F) || !UtilJava.equals(
					currentLocation.getPitch(), lastLocation.getPitch(), 0.0001F)) {
				token.lastMoved = currentMillis;
				token.lastMouseMoved = currentMillis;
				token.lastLocation = currentLocation;
			}

			// Sneak
			if (player.isSneaking()) {
				token.lastSneaking = currentMillis;
			} else if (!player.isSneaking()) {
				token.lastStanding = currentMillis;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onJoin(PlayerSpawnLocationEvent event) {
		playerMovement.put(event.getPlayer(), new MoveToken(event.getSpawnLocation()));
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		playerMovement.remove(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTeleport(PlayerTeleportEvent event) {
		if (event.isCancelled()) {
			return;
		}

		// Update the last location so a teleport isn't considered movement
		MoveToken token = playerMovement.get(event.getPlayer());

		token.lastLocation = event.getTo();
	}
	
	public MoveToken getMoveToken(Player player) {
		return playerMovement.get(player);
	}
	
}
