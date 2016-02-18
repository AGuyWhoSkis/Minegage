package net.minegage.minigame.lobby;


import net.minegage.common.java.SafeMap;
import net.minegage.common.log.L;
import net.minegage.common.module.PluginModule;
import net.minegage.common.server.ServerManager;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilItem;
import net.minegage.common.util.UtilWorld;
import net.minegage.common.util.UtilZip;
import net.minegage.common.C;
import net.minegage.core.equippable.MenuEquip;
import net.minegage.core.rank.Rank;
import net.minegage.core.rank.RankManager;
import net.minegage.minigame.GameManager;
import net.minegage.minigame.game.Game;
import net.minegage.minigame.game.Game.GameState;
import net.minegage.minigame.game.event.GameStateChangeEvent;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;


public class LobbyManager
		extends PluginModule {

	public static final int START_SECONDS = 10;
	public static final int SKIP_SECONDS = 5;

	private GameManager gameManager;
	private LobbyBoardManager boardManager;

	private ItemStack backToHub;
	private ItemStack cosmetics;

	private SafeMap<Integer, ItemStack> items = new SafeMap<>();

	public LobbyManager(GameManager gameManager) {
		super("Lobby Manager", gameManager);

		this.gameManager = gameManager;
		this.boardManager = new LobbyBoardManager(this);

		items.put(7, backToHub = UtilItem.create(Material.WATCH, C.fItem("Back to Hub", "click")));
		items.put(8, cosmetics = UtilItem.create(Material.CHEST, C.fItem("Cosmetics", "click")));

		setupLobby();
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void handleStateChange(GameStateChangeEvent event) {
		Game game = gameManager.getGame();

		if (game.inLobby()) {
			boardManager.enable();
			gameManager.getMinigameManager().getEquipManager().enable();

		} else {
			boardManager.disable();
			gameManager.getMinigameManager().getEquipManager().disable();
		}

		if (event.getNewState() == GameState.WAITING) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				giveItems(player);
			}
		}
	}


	@EventHandler
	public void giveItems(PlayerJoinEvent event) {
		Game game = gameManager.getGame();
		if (game != null && game.inLobby()) {
			giveItems(event.getPlayer());
		}
	}

	private void giveItems(Player player) {
		for (Entry<Integer, ItemStack> entry : items.entrySet()) {
			player.getInventory()
					.setItem(entry.getKey(), entry.getValue());
		}
	}

	@EventHandler
	public void useItems(PlayerInteractEvent event) {
		if (!UtilEvent.isClick(event)) {
			return;
		}

		ItemStack hand = event.getItem();
		if (UtilItem.is(hand, backToHub)) {
			ServerManager.instance.connect(event.getPlayer(), "Hub");
		} else if (UtilItem.is(hand, cosmetics)) {
			getGameManager().getMinigame()
					.getMenuManager()
					.open(event.getPlayer(), MenuEquip.RAW_NAME);
		}
	}

	@EventHandler
	public void orderItems(PlayerDropItemEvent event) {
		if (!RankManager.instance.hasPermission(event.getPlayer(), Rank.ADMIN)) {
			UtilEvent.orderItems(event, items);
		}
	}

	@EventHandler
	public void itemMove(InventoryClickEvent event) {
		if (!RankManager.instance.hasPermission((Player) event.getWhoClicked(), Rank.ADMIN)) {
			UtilEvent.lockItem(event, items.values());
		}
	}


	public void setupLobby() {
		logInfo("Setting up lobby...");
		File serverDir = Bukkit.getWorldContainer();

		logInfo("Deleting all worlds...");

		// All worlds are unloaded at this point (STARTUP load time)
		List<String> worlds = UtilWorld.getUnloadedWorlds();

		if (worlds.size() > 5) {
			L.warn("There are more than 5 worlds on this server. This is a failsafe to prevent worlds from accidentally "
			       +
			       "being deleted, should this plugin be uploaded to the wrong server. Please delete the worlds manually to continue.");
			return;
		}

		for (String worldName : worlds) {
			File worldDir = new File(serverDir, worldName);

			try {
				FileUtils.forceDelete(worldDir);
				L.info("Deleted world " + worldName);
			} catch (IOException ex) {
				L.error(ex, "Unable to delete world");
			}
		}

		logInfo("Extracting lobby zip...");
		File worldsDir = ServerManager.getWorldsDir();

		File lobbyZip = new File(worldsDir, "gamelobby.zip");
		if (!lobbyZip.exists()) {
			L.severe("Lobby file \"" + lobbyZip.getAbsolutePath() + "\" not found");
			return;
		}

		File lobbyDir = new File(serverDir, "world");
		try {
			UtilZip.extract(lobbyZip, lobbyDir);
		} catch (IOException ex) {
			L.error(ex, "Unable to extract lobby");
		}
	}

	@EventHandler
	public void applySettings(WorldLoadEvent event) {
		if (!UtilWorld.isMainWorld(event.getWorld())) {
			return;
		}

		UtilWorld.applySettings(event.getWorld());
	}

	public GameManager getGameManager() {
		return gameManager;
	}

	public LobbyBoardManager getBoardManager() {
		return boardManager;
	}
}
