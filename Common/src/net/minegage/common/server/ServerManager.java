package net.minegage.common.server;


import net.minegage.common.log.L;
import net.minegage.common.module.BungeeModule;
import net.minegage.common.util.UtilEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class ServerManager
		extends BungeeModule {

	public static final String ASSETS_PATH =
			System.getProperty("user.home") + File.separator + "assets" + File.separator;
	public static final String WORLDS_PATH = ASSETS_PATH + "worlds";
	public static final String MAPS_PATH = ASSETS_PATH + "maps";

	public static File getAssetsDir() {
		return new File(ASSETS_PATH);
	}

	public static File getMapsDir() {
		return new File(MAPS_PATH);
	}

	public static File getWorldsDir() {
		return new File(WORLDS_PATH);
	}

	public static File getMapsDir(String gameType) {
		return new File(ServerManager.MAPS_PATH, gameType);
	}

	public static File getMapZip(String gameType, String map) {
		File dir = getMapsDir(gameType);
		return new File(dir, map + ".zip");
	}

	public static File getWorldZip(String world) {
		File dir = getWorldsDir();
		return new File(dir, world + ".zip");
	}

	public static final String LOBBY = "Hub";
	public static ServerManager instance;

	private Set<Player> connecting = new HashSet<>();

	public ServerManager(JavaPlugin plugin) {
		super("Server Manager", plugin);
		ServerManager.instance = this;
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}

		EventPluginMessageReceive event = new EventPluginMessageReceive(message);
		UtilEvent.call(event);
	}

	/**
	 * @param server The name of the BungeeCord server
	 */
	public void connect(Player player, String server) {
		if (connecting.contains(player)) {
			return;
		}

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		DataOutputStream      dOut = new DataOutputStream(bOut);

		try {
			dOut.writeUTF("Connect");
			dOut.writeUTF(server);

			player.sendPluginMessage(plugin, "BungeeCord", bOut.toByteArray());
		} catch (IOException ex) {
			L.error(ex, "Unable to connect " + player.getName() + " to " + server);
		} finally {
			try {
				dOut.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void clearReference(PlayerQuitEvent event) {
		connecting.remove(event.getPlayer());
	}
}
