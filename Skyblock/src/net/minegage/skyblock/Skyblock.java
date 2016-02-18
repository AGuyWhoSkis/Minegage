package net.minegage.skyblock;


import net.milkbowl.vault.economy.Economy;
import net.minegage.common.board.Board;
import net.minegage.common.board.objective.ObjectiveSide;
import net.minegage.common.module.PluginModule;
import net.minegage.core.board.BoardManager;
import net.minegage.common.C;
import net.minegage.core.move.AFKManager;
import net.minegage.core.vault.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;


public class Skyblock
		extends PluginModule {

	private SkyblockPlugin skyblockPlugin;
	private BoardManager boardManager;
	private AFKManager afkManager;


	public Skyblock(SkyblockPlugin skyblockPlugin) {
		super("Skyblock Manager", skyblockPlugin);

		this.skyblockPlugin = skyblockPlugin;
		this.boardManager = new BoardManager(skyblockPlugin);

		this.boardManager.setRankMode(true);

		this.afkManager = new AFKManager(skyblockPlugin.getMoveManager(), 60 * 5);

		// Update money if it changes
		runSyncTimer(20L, 20L, new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {

					Board board = boardManager.getBoard(player);
					board.getSideObjective()
							.updateRow(13, getBalance(player));
				}
			}
		});

		skyblockPlugin.getSpawnManager().overrideSpawns = false;
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void assignBoard(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Board  board  = new Board();

		ObjectiveSide side = board.setSideObjective();
		side.setHeader(C.cAquaD + C.cBold + "Minegage " + C.cWhite + C.cBold + "Skyblock");

		side.addRow("");
		side.addRow(C.cGreen + C.cBold + "Money");
		side.addRow(getBalance(player)); // row 13
		side.addRow("");
		side.addRow(C.cAqua + "minegage.net");

		boardManager.setBoard(player, board);
	}

	private String getBalance(Player player) {
		double  bal = 0;
		Economy eco = VaultHook.instance.getEconomy();
		if (eco != null) {
			bal = eco.getBalance(player);
		}

		return "$" + bal;
	}

	public SkyblockPlugin getSkyblock() {
		return skyblockPlugin;
	}
}
