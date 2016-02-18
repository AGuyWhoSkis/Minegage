package net.minegage.core.board;

import net.minegage.common.C;
import net.minegage.common.board.AssignBoardEvent;
import net.minegage.common.board.CommonBoardManager;
import net.minegage.core.rank.Rank;
import net.minegage.core.rank.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BoardManager
		extends CommonBoardManager {

	private boolean rankMode = false;

	public BoardManager(JavaPlugin plugin) {
		super(plugin);
	}

	@Override
	protected void onEnable() {
		if (rankMode) {
			setRankMode(true);
		}
	}

	public void setRankMode(boolean enabled) {
		if (enabled) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				Rank rank = RankManager.instance.getRank(player);

				setPrefix(player, rank.getTeamPrefix());
				player.setDisplayName(rank.getChatPrefix() + player.getName() + C.cReset);
			}
		} else {
			for (Player player : Bukkit.getOnlinePlayers()) {
				setPrefix(player, null);
			}
		}

		this.rankMode = enabled;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void assignBoard(AssignBoardEvent event) {
		if (rankMode) {
			for (Player other : Bukkit.getOnlinePlayers()) {
				Rank rank = RankManager.instance.getRank(other);
				setPrefix(other, rank.getTeamPrefix());
			}
		}
	}

	@EventHandler
	public void updateRank(PlayerJoinEvent event) {
		if (rankMode) {
			Player player = event.getPlayer();
			Rank   rank   = RankManager.instance.getRank(player);

			setPrefix(player, rank.getTeamPrefix());
			player.setDisplayName(rank.getChatPrefix() + player.getName() + C.cReset);
		}
	}
}
