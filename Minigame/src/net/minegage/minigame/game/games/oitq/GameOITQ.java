package net.minegage.minigame.game.games.oitq;


import net.minegage.common.C;
import net.minegage.common.board.Board;
import net.minegage.common.board.objective.ObjectiveSide;
import net.minegage.common.util.UtilSound;
import net.minegage.core.combat.event.CombatDeathEvent;
import net.minegage.core.combat.event.CombatEvent;
import net.minegage.minigame.MinigameManager;
import net.minegage.minigame.game.GameFFA;
import net.minegage.minigame.game.GameType;
import net.minegage.minigame.game.games.oitq.kits.KitDefault;
import net.minegage.minigame.stats.Stat;
import net.minegage.minigame.stats.UpdatePlayerStatEvent;
import net.minegage.minigame.winnable.PlayerComparator;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;


public class GameOITQ
		extends GameFFA {
		
	private final int WIN_KILLS = 10;
	
	public GameOITQ(MinigameManager manager) {
		super(manager, GameType.ONE_IN_THE_QUIVER, new String[] { }, new KitDefault());
		
		this.deathOut = false;
		this.explainFreeze = false;
		
		getTeam().respawnSeconds = 1.0;
	}
	
	@EventHandler
	public void arrowInstaKill(CombatEvent event) {
		if (event.getDirectDamager() instanceof Arrow) {
			event.addModIncrement(500.0);
		}
	}
	
	@EventHandler
	public void arrowReward(CombatDeathEvent event) {
		if (!event.isPlayerCreditted()) {
			return;
		}
		
		Player player = event.getKillerPlayer();
		player.getInventory()
				.addItem(new ItemStack(Material.ARROW));
		player.updateInventory();
		
		UtilSound.playLocal(player, Sound.CHICKEN_EGG_POP, 1F, 1.2F);
	}
	
	private int scoreStart;
	
	@Override
	public void giveBoard(Player player, Board board) {
		ObjectiveSide side = board.getSideObjective();
		side.addRow("");
		side.addRow(C.cRed + C.cBold + "Kills");
		side.addRow("First to " + WIN_KILLS);
		side.addRow("");
		scoreStart = side.nextRowNum();
		updateBoard(player, board);
	}
	
	@EventHandler
	public void updateScores(UpdatePlayerStatEvent event) {
		if (event.getStat()
				.equals(Stat.KILLS)) {
				
			for (Entry<Player, Board> playerBoard : boardManager.getBoards()
					.entrySet()) {
				updateBoard(playerBoard.getKey(), playerBoard.getValue());
			}
			
			for (Player player : getPlayersIn()) {
				int kills = stats.get(player, Stat.KILLS);
				if (kills >= WIN_KILLS) {
					setState(GameState.ENDING);
				}
			}
		}
	}
	
	private void updateBoard(Player player, Board board) {
		List<Player> players = getPlayersIn();
		players.sort(new PlayerComparator(stats, Stat.KILLS));
		
		ObjectiveSide side = board.getSideObjective();
		for (int i = scoreStart; i > 0; i--) {
			side.removeRow(i);
		}
		
		Iterator<Player> playersIt = players.iterator();
		while (side.hasRoom() && playersIt.hasNext()) {
			Player next = playersIt.next();
			Integer score = stats.get(next, Stat.KILLS);
			
			if (score > 0) {
				String prefix = "";
				if (player.equals(next)) {
					prefix = C.cPink;
				}
				
				String content = prefix + score + " " + next.getName();
				side.addRow(content);
			}
		}
		
		// Add spacer if there's room
		if (side.hasRoom()) {
			side.addRow("");
		}
	}
	
}
