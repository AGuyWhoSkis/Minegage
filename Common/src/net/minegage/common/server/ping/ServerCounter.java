package net.minegage.common.server.ping;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minegage.common.java.SafeMap;
import net.minegage.common.log.L;
import net.minegage.common.module.BungeeModule;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map.Entry;
import java.util.UUID;

public class ServerCounter
		extends BungeeModule
		implements PluginMessageListener {

	private Object serverLock = new Object();
	private SafeMap<String, PingToken> servers = new SafeMap<>();
	private ServerPing pinger;

	public ServerCounter(JavaPlugin plugin) {
		super("Server Counter", plugin);

		pinger = new ServerPing();


		runAsyncTimer(20L, 20L, new BukkitRunnable() {
			@Override
			public void run() {
				synchronized (serverLock) {
					// Servers aren't loaded yet; wait for player to join
					if (servers.size() == 0) {
						return;
					}

					for (Entry<String, PingToken> serverEntry : servers.entrySet()) {
						try {
							pinger.ping(serverEntry.getValue());
							serverEntry.getValue().offline = false;
						} catch (IOException ex) {
							serverEntry.getValue().offline = true;
							L.warn("Unable to ping server " + serverEntry.getValue().address + ". " + ex.getClass().getSimpleName() + ": " + ex.getMessage());
						}
					}
				}
			}
		});
	}

	public PingToken getPingToken(String server) {
		synchronized (serverLock) {
			return servers.get(server);
		}
	}

	@EventHandler
	public void addServers(PlayerJoinEvent event) {
		if (servers.size() != 0) {
			return;
		}

		UUID uid = event.getPlayer().getUniqueId();

		runSyncDelayed(5L, () -> {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("GetServers");

			Player player = Bukkit.getPlayer(uid);
			if (player == null) {
				return;
			}

			player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
		});
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (!channel.equals("BungeeCord")) {
			return;
		}

		ByteArrayDataInput in         = ByteStreams.newDataInput(message);
		String             subchannel = in.readUTF();
		if (subchannel.equals("GetServers")) {
			servers.clear();
			String[] serverList = in.readUTF().split(", ");

			for (String server : serverList) {
				// Request the address of each server
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("ServerIP");
				out.writeUTF(server);

				player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
			}
		} else if (subchannel.equals("ServerIP")) {
			String server = in.readUTF();
			String ip     = in.readUTF();
			short  port   = in.readShort();

			PingToken token = new PingToken();
			token.address = new InetSocketAddress(ip, port);

			// Don't add this server
			if (!ip.equals(Bukkit.getIp()) || port != Bukkit.getPort()) {
				servers.put(server.toLowerCase(), token);
			}

		}
	}
}
