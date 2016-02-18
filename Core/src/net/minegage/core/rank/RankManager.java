package net.minegage.core.rank;


import net.milkbowl.vault.permission.Permission;
import net.minegage.common.java.SafeMap;
import net.minegage.common.log.L;
import net.minegage.common.module.PluginModule;
import net.minegage.common.util.UtilJava;
import net.minegage.common.C;
import net.minegage.core.vault.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


public class RankManager
		extends PluginModule {

	public static RankManager instance;

	public SafeMap<Player, Rank> ranks = new SafeMap<>();

	public RankManager(JavaPlugin plugin) {
		super("Rank Manager", plugin);

		RankManager.instance = this;

		for (Player player : Bukkit.getOnlinePlayers()) {
			loadRank(player);
		}
	}

	public Rank getRank(Player player) {
		return ranks.get(player);
	}

	public Set<Player> getPlayers(Rank... ranks) {
		return this.ranks.entrySet()
				.stream()
				.filter((entry) -> {
					return UtilJava.contains(ranks, entry.getValue());
				})
				.map(Entry::getKey)
				.collect(Collectors.toSet());
	}

	public boolean hasPermission(Rank rank, Rank minRank) {
		return rank.includes(minRank);
	}

	public boolean hasPermission(Player player, Rank minRank) {
		return hasPermission(getRank(player), minRank);
	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void clearRank(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		ranks.remove(player);
	}

	// Load rank before PlayerJoinEvent is called
	@EventHandler (priority = EventPriority.LOWEST)
	public void loadRank(PlayerLoginEvent event) {
		loadRank(event.getPlayer());
	}

	public void loadRank(Player player) {
		String primaryGroup = "null";

		Permission perm = VaultHook.instance.getPermission();

		if (perm != null) {
			primaryGroup = perm.getPrimaryGroup(player);
		}

		Rank rank = null;

		// Find rank with the name of the player group
		for (Rank other : Rank.values()) {
			if (other.getPermName()
					.equalsIgnoreCase(primaryGroup)) {
				rank = other;
				break;
			}
		}

		// Failsafe in case zperms doesn't load
		if (perm == null && player.isOp()) {
			rank = Rank.ADMIN;
			UUID uid = player.getUniqueId();

			runSyncDelayed(20L, new Runnable() {
				@Override
				public void run() {
					Player p = Bukkit.getPlayer(uid);
					if (p != null) {
						C.pWarn(p, "Warning",
						        "The permission service failed to load. Players will be set to the default group unless opped.");
					}
				}
			});
		}

		// Default to member if rank isn't found
		if (rank == null) {
			rank = Rank.DEFAULT;
			L.warn("Unknown rank " + primaryGroup + "; defaulting " + player.getName() + " to default group");
		}

		L.info(player.getName() + " loaded with rank " + rank.getPermName());

		ranks.put(player, rank);
	}

	public String getDisplayNamee(Player player) {
		Rank rank = getRank(player);

		String displayName = rank.getChatPrefix() + player.getName() + C.cReset;
		return displayName;
	}

}
