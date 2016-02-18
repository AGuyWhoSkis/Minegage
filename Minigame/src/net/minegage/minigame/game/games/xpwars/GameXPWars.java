package net.minegage.minigame.game.games.xpwars;


import net.minegage.common.board.Board;
import net.minegage.common.board.objective.ObjectiveSide;
import net.minegage.common.datafile.DataFile;
import net.minegage.common.log.L;
import net.minegage.common.misc.Note;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilItem;
import net.minegage.common.util.UtilSound;
import net.minegage.common.util.UtilTime;
import net.minegage.common.util.UtilUI;
import net.minegage.common.C;
import net.minegage.minigame.MinigameManager;
import net.minegage.minigame.board.helper.RankedPlayerStatHelper;
import net.minegage.minigame.event.GameDeathEvent;
import net.minegage.minigame.game.GameFFA;
import net.minegage.minigame.game.GameType;
import net.minegage.minigame.game.games.xpwars.kits.KitXPWarrior;
import net.minegage.minigame.stats.Stat;
import net.minegage.minigame.stats.UpdatePlayerStatEvent;
import net.minegage.minigame.winnable.PlayerComparator;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


public class GameXPWars
		extends GameFFA {

	public final ItemStack enchantItem = UtilItem
			.create(Material.NETHER_STAR, C.cBold + "Enchant your items! " + C.cReset + " (click)");
	public final ItemStack LAPIS_ITEM;

	private final String ROUND_LAPIS = "roundlapis";
	private final String TOTAL_LAPIS = "totallapis";

	private int round = 0;
	private int rounds = 5;

	private final double COMBAT_SECONDS = 60.0;
	private final double ENCHANT_SECONDS = 20.0;

	private final long COMBAT_MILLIS = UtilTime.toMillis(COMBAT_SECONDS);
	private final long ENCHANT_MILLIS = UtilTime.toMillis(ENCHANT_SECONDS);

	public GameXPWars(MinigameManager manager) {
		super(manager, GameType.XP_WARS, new String[] {"Free for all! Get the most kills to win!", "Kill to get lapis!",
		                                               "Use lapis to enchant items!"}, new KitXPWarrior());

		this.itemDropDeny.add(enchantItem.getData());

		this.deathOut = false;
		this.armourMove = true;
		this.explainFreeze = false;

		this.damage = false;

		Dye dye = new Dye(DyeColor.BLUE);
		LAPIS_ITEM = dye.toItemStack();

		getStatTracker().setDefaultPlayerValue(TOTAL_LAPIS, 0);
		getStatTracker().setDefaultPlayerValue(ROUND_LAPIS, 0);

		this.winnerComparator = new PlayerComparator(stats, Stat.KILLS);

		getTeam().respawnSeconds = Double.POSITIVE_INFINITY;


		getBoardManager().addBoardHelper(new RankedPlayerStatHelper(this, winnerComparator));
	}

	@Override
	public void start() {
		super.start();

		periodStart = System.currentTimeMillis();

		runSyncDelayed(UtilTime.toTicks(explainTime), () -> {
			startEnchantPeriod();
		});
	}

	@Override
	public void loadWorldData(DataFile worldData) {
		super.loadWorldData(worldData);
		// When the map is loaded, create an enchantment table at (0, 0, 0)

		World world = getMap();

		world.getBlockAt(0, 0, 0)
				.setType(Material.ENCHANTMENT_TABLE);

		// Bookshelves
		world.getBlockAt(-1, 0, 2).setType(Material.BOOKSHELF);
		world.getBlockAt(-1, 1, 2).setType(Material.BOOKSHELF);

		world.getBlockAt(-2, 0, 2).setType(Material.BOOKSHELF);
		world.getBlockAt(-2, 1, 2).setType(Material.BOOKSHELF);

		world.getBlockAt(-2, 0, 1).setType(Material.BOOKSHELF);
		world.getBlockAt(-2, 1, 1).setType(Material.BOOKSHELF);

		world.getBlockAt(-2, 0, 0).setType(Material.BOOKSHELF);
		world.getBlockAt(-2, 1, 0).setType(Material.BOOKSHELF);

		world.getBlockAt(-2, 0, -1).setType(Material.BOOKSHELF);
		world.getBlockAt(-2, 1, -1).setType(Material.BOOKSHELF);

		world.getBlockAt(-2, 0, -2).setType(Material.BOOKSHELF);
		world.getBlockAt(-2, 1, -2).setType(Material.BOOKSHELF);

		world.getBlockAt(-1, 0, -2).setType(Material.BOOKSHELF);
		world.getBlockAt(-1, 1, -2).setType(Material.BOOKSHELF);

		world.getBlockAt(0, 0, -2).setType(Material.BOOKSHELF);


		// Set space in between shelves and table to air
		world.getBlockAt(0, 0, -1).setType(Material.AIR);
		world.getBlockAt(0, 1, -1).setType(Material.AIR);

		world.getBlockAt(-1, 0, -1).setType(Material.AIR);
		world.getBlockAt(-1, 1, -1).setType(Material.AIR);

		world.getBlockAt(-1, 0, 0).setType(Material.AIR);
		world.getBlockAt(-1, 1, 0).setType(Material.AIR);

		world.getBlockAt(-1, 0, 1).setType(Material.AIR);
		world.getBlockAt(-1, 1, 1).setType(Material.AIR);

		world.getBlockAt(0, 0, 1).setType(Material.AIR);
		world.getBlockAt(0, 1, 1).setType(Material.AIR);
	}

	private long periodStart;
	private boolean combat = false;
	private boolean enchanting = true;

	@EventHandler
	public void roundTick(TickEvent event) {
		if (event.is(Tick.SEC_1)) {
			if (!inMap()) {
				return;
			}

			double secondsLeft = getPeriodSecondsLeft();
			if (explaining) {
				secondsLeft = ENCHANT_SECONDS;
			}

			String timer = Math.round(secondsLeft) + "s";

			String roundString = Math.max(round, 1) + ""; // Round starts at 0

			for (Board board : getBoardManager().getPlayerBoards()) {
				ObjectiveSide side = board.getSideObjective();
				side.updateRow(14,
				               (enchanting) ? C.cAqua + C.cBold + "Enchant Time" : C.cRed + C.cBold + "Combat Time");
				side.updateRow(13, timer);
				side.updateRow(11,
				               C.cGold + "Round " + C.cYellow + C.cBold + roundString + C.cGold + " of " + C.cYellow +
				               C.cBold +
				               rounds);
			}


		} else if (event.is(Tick.TICK_1)) {
			if (!inMap() || explaining) {
				return;
			}

			double secondsLeft = getPeriodSecondsLeft();

			String timer = Math.round(secondsLeft) + "s";

			if (enchanting) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					UtilUI.sendActionBar(player,
					                     C.cAqua + C.cBold + "Enchant period ends in " + C.cYellow + C.cBold + timer);
				}
			} else if (combat) {
				for (Player player : Bukkit.getOnlinePlayers()) {
					UtilUI.sendActionBar(player, C.cWhite + C.cBold + timer + " remain!");
				}
			} else {
				return;
			}


		}

	}

	private BukkitTask periodExpireTask;

	private void startEnchantPeriod() {


		if (periodExpireTask != null) {
			periodExpireTask.cancel();
			periodExpireTask = null;
		}

		L.d("starting enchant period");

		round++;

		for (Player player : getPlayersIn()) {
			stats.increment(player, TOTAL_LAPIS, 1);
		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			stats.set(player, ROUND_LAPIS, stats.get(player, TOTAL_LAPIS));
		}

		this.periodStart = System.currentTimeMillis();
		this.damage = false;
		this.enchanting = true;
		this.combat = false;

		for (Board board : getBoardManager().getPlayerBoards()) {
			ObjectiveSide side = board.getSideObjective();
			side.updateRow(14, C.cAqua + C.cBold + "Enchant Time");
		}

		for (Player player : getPlayersIn()) {
			UtilUI.sendActionBar(player, C.cAqua + C.cBold + ENCHANT_SECONDS + "s of enchant time has begun!");
		}

		periodExpireTask = runSyncDelayed(UtilTime.toTicks(ENCHANT_SECONDS), new BukkitRunnable() {
			@Override
			public void run() {
				startCombatPeriod();
			}
		});

	}

	private void endCombatPeriod() {
		if (periodExpireTask != null) {
			periodExpireTask.cancel();
			periodExpireTask = null;
		}

		L.d("ending combat period");
		this.damage = false;
		this.combat = false;

		if (round < rounds) {
			C.bMain("Game", "Round " + round + " complete!");


			runSyncDelayed(100L, new BukkitRunnable() {
				public void run() {
					for (Player player : getPlayersIn()) {
						respawn(player);
					}

					startEnchantPeriod();
				}
			});

		} else {
			setState(GameState.ENDING);
		}


	}

	private void startCombatPeriod() {
		if (periodExpireTask != null) {
			periodExpireTask.cancel();
			periodExpireTask = null;
		}

		L.d("starting combat period");
		this.periodStart = System.currentTimeMillis();
		this.damage = true;
		this.combat = true;
		this.enchanting = false;

		UtilSound.playGlobal(Sound.NOTE_PLING, 1F, Note.O3_C);
		for (Player player : Bukkit.getOnlinePlayers()) {
			UtilUI.sendActionBar(player, C.cRed + C.cBold + COMBAT_SECONDS + "s of combat time has begun!");
		}

		periodExpireTask = runSyncDelayed(UtilTime.toTicks(COMBAT_SECONDS), new BukkitRunnable() {
			@Override
			public void run() {
				endCombatPeriod();
			}
		});

	}

	@EventHandler (priority = EventPriority.MONITOR)
	public void onDeath(GameDeathEvent event) {
		if (!combat) {
			return;
		}

		int remaining = getPlayersNotSpectating().size() - 1;
		if (remaining < 2) {
			endCombatPeriod();
		}
	}

	private double getPeriodSeconds() {
		return UtilTime.toSeconds(UtilTime.timePassedSince(periodStart));
	}

	private double getPeriodSecondsLeft() {
		double periodLength = (combat) ? COMBAT_SECONDS : ENCHANT_SECONDS;
		return Math.max(0, periodLength - getPeriodSeconds());
	}

	private int scoreStart;

	@Override
	public void giveBoard(Player player, Board board) {
		ObjectiveSide side = board.getSideObjective();
		side.addRow("");
		side.addRow(""); // Enchant Time or Combat Time
		side.addRow(""); // seconds left
		side.addRow("");
		side.addRow(""); // round
		side.addRow("");
		side.addRow(C.cAqua + C.cBold + "Total Lapis");
		side.addRow("1");
		side.addRow("");
		side.addRow(C.cRed + C.cBold + "Kills");
	}


	@EventHandler
	public void updateScores(UpdatePlayerStatEvent event) {
		if (event.getStat()
				.equals(Stat.KILLS)) {

			//stats.increment(event.getPlayer(), TOTAL_LAPIS, 1);

		} else if (event.getStat()
				.equals(TOTAL_LAPIS)) {
			Player player = event.getPlayer();
			int    lapis  = event.getNewValue();

			Board         board = boardManager.getBoard(player);
			ObjectiveSide side  = board.getSideObjective();
			side.updateRow(8, lapis + "");
		}
	}


	@EventHandler
	public void openMenu(PlayerInteractEvent event) {
		if (!UtilEvent.isClick(event)) {
			return;
		}

		if (UtilItem.is(event.getItem(), enchantItem)) {
			int lapisCount = stats.get(event.getPlayer(), ROUND_LAPIS);

			EnchantingInventory inv = openEnchantmentInventory(event.getPlayer());

			if (inv != null && lapisCount > 0) {
				LAPIS_ITEM.setAmount(lapisCount);
				inv.setItem(1, LAPIS_ITEM);
				event.getPlayer()
						.updateInventory();
			}
		}
	}

	// Prevent items being dropped on the ground when closing enchant menu
	@EventHandler
	public void preventItemDrop(InventoryCloseEvent event) {
		Inventory inv = event.getInventory();

		if (inv.getType() == InventoryType.ENCHANTING) {
			ItemStack enchanted = inv.getItem(0);

			if (enchanted != null) {
				event.getPlayer()
						.getInventory()
						.addItem(enchanted);
			}

			inv.clear();
		}
	}

	@EventHandler
	public void debitLapis(EnchantItemEvent event) {
		int lapisCost = event.whichButton() + 1;
		getStatTracker().increment(event.getEnchanter(), ROUND_LAPIS, -lapisCost);
	}


	@EventHandler
	public void preventLapisMove(InventoryClickEvent event) {
		if (event.getClickedInventory() == null) {
			return;
		}

		if (event.getClickedInventory()
				    .getType() != InventoryType.ENCHANTING || event.getSlot() != 1) {
			return;
		}

		event.setCancelled(true);
		((Player) event.getWhoClicked()).updateInventory();
	}

	private EnchantingInventory openEnchantmentInventory(Player player) {
		// Only true for map
		Block block = player.getWorld()
				.getBlockAt(0, 0, 0);

		if (block.getType() != Material.ENCHANTMENT_TABLE) {
			return null;
		}

		return (EnchantingInventory) player.openEnchanting(block.getLocation(), true)
				.getTopInventory();
	}

}
