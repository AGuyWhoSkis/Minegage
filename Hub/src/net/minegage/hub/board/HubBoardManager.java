package net.minegage.hub.board;


import com.google.common.collect.Lists;
import net.minegage.common.board.Board;
import net.minegage.common.board.objective.ObjectiveSide;
import net.minegage.common.java.SafeMap;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.util.UtilString;
import net.minegage.core.board.BoardManager;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import net.minegage.core.rank.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;


public class HubBoardManager
		extends BoardManager {
		
	private final int WIDTH = 18;
	private final String PADDING;
	private  List<String> news = Lists.newArrayList();
	
	private SafeMap<Player, Integer> newsIndexes = new SafeMap<>();
	private SafeMap<Player, Integer> scrollIndexes = new SafeMap<>();
	
	private void addNews(String message) {
		/* Add extra spaces at the start to give it a bit of delay */
		news.add("     " + PADDING + message + PADDING);
	}
	
	public HubBoardManager(JavaPlugin plugin) {
		super(plugin);

		StringBuilder spaces = new StringBuilder();
		for (int i = 0; i < WIDTH; i++) {
			spaces.append(" ");
		}
		PADDING = spaces.toString();

		addNews("Welcome to Minegage!");
		addNews("Factions is now open!");
		addNews("Heavy development is underway. Stay tuned!");
		addNews("Have a suggestion? Post on the forums!");

		setRankMode(true);
	}
	
	@EventHandler
	public void giveBoard(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		Board board = new Board();
		ObjectiveSide side = board.setSideObjective();
		
		Rank rank = RankManager.instance.getRank(player);
		
		side.setHeader(C.cBold + "Minegage");
		side.setRow(15, "");
		side.setRow(14, C.cGreen + C.cBold + "Rank");
		side.setRow(13, UtilString.format(rank.name()));
		side.setRow(12, "");
		side.setRow(11, C.cYellow + C.cBold + "News");
		side.setRow(10, "");
		side.setRow(9, "");
		side.setRow(8, C.cAqua + C.cBold + "IP");
		side.setRow(7, "minegage.net");
		side.setRow(6, "");
		side.setRow(5, "------------------");
		
		setBoard(player, board);
	}
	
	public void tickNews(Player player) {
		Board board = getBoard(player);
		
		int newsIndex = newsIndexes.getOrDefault(player, 0);
		int scrollIndex = scrollIndexes.getOrDefault(player, 0);
		
		String message = news.get(newsIndex);
		
		// Go to next message if message section isn't big enough
		if (scrollIndex + WIDTH > message.length()) {
			scrollIndex = 0;
			
			if (++newsIndex >= news.size()) {
				newsIndex = 0;
			}
			
			message = news.get(newsIndex);
		}
		
		String display = message.substring(scrollIndex, scrollIndex + WIDTH);
		
		ObjectiveSide side = board.getSideObjective();
		side.updateRow(10, display);
		
		scrollIndex++;
		
		newsIndexes.put(player, newsIndex);
		scrollIndexes.put(player, scrollIndex);
	}

	@EventHandler
	public void tickNews(TickEvent event) {
		if (event.isNot(Tick.TICK_5)) {
			return;
		}
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			tickNews(player);
		}
	}
	
}
