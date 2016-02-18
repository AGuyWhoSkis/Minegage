package net.minegage.creative;


import net.minegage.common.board.Board;
import net.minegage.common.module.PluginModule;
import net.minegage.core.board.BoardManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class Creative
		extends PluginModule {
		
	private BoardManager boardManager;
	
	public Creative(JavaPlugin plugin) {
		super("Creative", plugin);
		
		boardManager = new BoardManager(plugin);
		boardManager.setRankMode(true);
	}
	
	@EventHandler
	public void assignBoard(PlayerJoinEvent event) {
		Board board = new Board();
		boardManager.setBoard(event.getPlayer(), board);
	}
	
}
