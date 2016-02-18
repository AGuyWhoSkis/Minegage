package net.minegage.common.board;


import net.minegage.common.java.SafeMap;
import net.minegage.common.module.PluginModule;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public class CommonBoardManager
		extends PluginModule {
		
	private SafeMap<Player, Board> boards = new SafeMap<>();
	
	private boolean rankMode = false;
	
	public CommonBoardManager(JavaPlugin plugin) {
		super("Board Manager", plugin);
	}

	@Override
	protected void onDisable() {
		boards.clear();
	}

	
	@EventHandler (priority = EventPriority.MONITOR)
	public void clearReferences(PlayerQuitEvent event) {
		removeBoard(event.getPlayer());
	}

	
	public void setPrefix(Player player, String prefix) {
		for (Board board : getPlayerBoards()) {
			board.setPrefix(player, prefix);
		}
	}
	
	public void setBoard(Player player, Board board) {
		boards.put(player, board);
		player.setScoreboard(board.getBoard());

		
		AssignBoardEvent event = new AssignBoardEvent(player, board);
		UtilEvent.call(event);
	}
	
	public Board removeBoard(Player player) {
		player.setScoreboard(UtilUI.getNewScoreboard());
		return boards.remove(player);
	}
	
	public Board getBoard(Player player) {
		return boards.get(player);
	}
	
	public Set<Board> getPlayerBoards() {
		return new HashSet<>(boards.values());
	}
	
	public SafeMap<Player, Board> getBoards() {
		return boards;
	}
	
}
