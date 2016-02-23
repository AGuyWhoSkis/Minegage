package net.minegage.minigame.game.games.riot;


import net.minegage.common.C;
import net.minegage.common.board.Board;
import net.minegage.common.board.objective.ObjectiveSide;
import net.minegage.common.datafile.DataFile;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.timer.Timer;
import net.minegage.common.util.Rand;
import net.minegage.common.util.UtilBlock;
import net.minegage.common.util.UtilEffect;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilMat;
import net.minegage.common.util.UtilServer;
import net.minegage.common.util.UtilUI;
import net.minegage.core.combat.event.CombatDeathEvent;
import net.minegage.minigame.MinigameManager;
import net.minegage.minigame.game.GameTDM;
import net.minegage.minigame.game.GameType;
import net.minegage.common.loot.Looter;
import net.minegage.common.loot.MultiLoot;
import net.minegage.minigame.game.games.riot.kit.KitCop;
import net.minegage.minigame.game.games.riot.kit.KitFirefighter;
import net.minegage.minigame.game.games.riot.kit.KitRioter;
import net.minegage.minigame.team.GameTeam;
import net.minegage.minigame.team.PlayerJoinTeamEvent;
import net.minegage.minigame.winnable.Winnable;
import net.minegage.minigame.winnable.WinnableTeam;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class GameRiot
		extends GameTDM {
		
	private Set<MaterialData> unbreakable = new HashSet<>();
	
	private final String POINTS_KEY = "Riot Points";

	private Looter looter;

	// The chaos required for the rioters to win
	private int chaosPerRioter;
	private int chaos = 100;
	
	private final int DEFAULT_CHAOS_PER_RIOTER = 465;
	
	public GameRiot(MinigameManager manager) {
		super(manager, GameType.RIOT, new String[] {
				C.cRed + "Rioters: " + C.cWhite + "Break things! Set buildings on fire! Blow up your",
				"neighbor's house! ...But watch out for cops!", "", C.cBlue + "Cops: " + C.cWhite
				                                                    + "Stop the riot at all costs! Wait until backup arrives!" });

		this.explodeDebris = true;
		this.explodeRegen = false;

		this.maxPlayers = 16;

		this.deathOut = false;

		this.blockBreak = true;
		this.blockPlace = true;

		this.armourMove = false;

		this.damageFallVsPlayer = true;
		this.itemPickup = false;
		this.itemDrop = false;
		
		this.timed = true;
		this.timeLimit = 60 * 3;

		this.looter = new Looter(this);

		looter.loot(0, Material.SNOW_BALL, 4, 12);
		looter.loot(0, Material.EGG, 4, 12);

		looter.loot(0, Material.WOOD_AXE, Material.WOOD_PICKAXE, Material.GOLD_AXE, Material.GOLD_PICKAXE, Material.STONE_PICKAXE);

		looter.loot(0, Material.LADDER, 4, 6);
		looter.loot(0, new MultiLoot(looter.create(Material.BOW), looter.create(Material.ARROW, 2, 4)));

		looter.loot(1, Material.TNT, 1, 3);
		looter.loot(1, Material.FIREBALL, Material.STONE_SWORD, Material.STONE_AXE);
		looter.loot(1, new MultiLoot(looter.create(Material.BOW), looter.create(Material.ARROW, 4, 8)));
	}

	@Override
	protected void onDisable() {
		super.onDisable();

		looter.disable();
	}

	@Override
	public void loadWorldData(DataFile worldData) {
		super.loadWorldData(worldData);

		this.chaosPerRioter = worldData.read("chaos")
				.defaults(DEFAULT_CHAOS_PER_RIOTER)
				.asInt();

		this.timeLimit = worldData.read("backup")
				.defaults(210)
				.asInt();

		unbreakable.add(UtilMat.getData(Material.BEDROCK));

		for (MaterialData matData : worldData.read("unbreakable").asMaterialDatas()) {
			unbreakable.add(matData);
		}
	}


	@Override
	public void createTeams() {
		createTeam("Rioters", C.cRed, new KitRioter()).respawnSeconds = 6.0;
		createTeam("Cops", C.cBlue, new KitCop(), new KitFirefighter());
	}

	/**
	 * @return If cops are winning, this will be positive. If rioters are winning, this will be
	 *         negative. The size of the value is the amount of difference between the % completed
	 *         objective of the two teams
	 */
	public double getBalanceDifference() {
		double percentChaos = getPercentageChaos();
		double percentBackup = getPercentageBackup();
		
		return percentBackup - percentChaos;
	}
	
	public boolean isRiotBoosted() {
		// If the rioters are losing by this percentage
		return getBalanceDifference() > 7.5;
	}

	public Set<ItemStack> getRandLoot() {
		double goodLootChance = 20;
		if (isRiotBoosted()) {
			goodLootChance = 40;
		}

		int tier = 0;
		if (Rand.chance(goodLootChance)) {
			tier = 1;
		}

		return looter.getLoot(tier, 2, 5);
	}

	@Override
	public boolean endCheck() {
		if (getBackupTime() <= 0) {
			C.bRaw("");
			C.bRaw(C.cBlue + C.cBold + "Backup has arrived! The cops win!");
			C.bRaw("");

			updateProgress();
			riotersWin = false;
			return true;
		}
		
		if (getRioterPoints() > chaos) {
			C.bRaw("");
			C.bRaw(C.cRed + C.cBold + "The cops couldn't stop the riot! The rioters win!");
			C.bRaw("");

			updateProgress();
			riotersWin = true;
			return true;
		}
		
		return false;
	}
	
	@EventHandler
	public void setChaos(PlayerJoinTeamEvent event) {
		if (event.getTeam()
				.equals(getRioters())) {
			this.chaos = chaosPerRioter * event.getTeam()
					.getPlayers()
					.size();
		}
	}
	
	@Override
	protected List<Winnable<?>> getWinnerPlaces() {
		List<Winnable<?>> winner     = new ArrayList<>();
		GameTeam          winnerTeam = null;
		if (riotersWin) {
			winnerTeam = getRioters();
		} else {
			winnerTeam = getCops();
		}
		
		winner.add(new WinnableTeam(winnerTeam));
		return winner;
	}

	
	@Override
	public void assignTeam(Player player) {
		int cops = getCops().getPlayers()
				.size();
		int rioters = getRioters().getPlayers()
				.size();
				
		rioters = (int) Math.ceil( (double) rioters / 3.0);
		
		if (cops > rioters) {
			getRioters().addPlayer(player);
		} else {
			getCops().addPlayer(player);
		}
	}
	
	private int chaosLabel = 0;
	private int chaosRow = 0;
	
	private int backupLabel = 0;
	private int backupRow = 0;
	
	@Override
	public void giveBoard(Player player, Board board) {
		ObjectiveSide side = board.getSideObjective();
		side.addRow("");
		chaosLabel = side.addRow(C.cGold + C.cBold + "Chaos");
		chaosRow = side.addRow(C.cGold + C.cBold + "0%");
		side.addRow("");
		backupLabel = side.addRow(C.cAqua + C.cBold + "Backup arrives");
		backupRow = side.addRow(C.cAqua + C.cBold + UtilUI.getTimer((int) timeLimit));
		side.addRow("");
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (!isPlaying()) {
			return;
		}
		
		event.getPlayer()
				.setExp((float) getPercentageChaos() / 100F);
	}
	
	@EventHandler
	public void tickProgress(TickEvent event) {
		if (event.isNot(Tick.SEC_1) || !isPlaying() || explaining) {
			return;
		}
		
		if (chaosFlash) {
			chaosToggle = !chaosToggle;
		}
		
		if (backupFlash) {
			backupToggle = !backupToggle;
		}
		
		updateProgress();
	}
	
	private boolean chaosFlash = false;
	private boolean chaosToggle = false;
	
	private boolean backupFlash = false;
	private boolean backupToggle = false;
	
	private DecimalFormat format = new DecimalFormat("#");
	
	private void updateProgress() {
		String chaosColour = C.cGold + C.cBold;
		if (chaosToggle) {
			chaosColour = C.cRed + C.cBold;
		}
		String backupColour = C.cAqua + C.cBold;
		if (backupToggle) {
			backupColour = C.cBlue + C.cBold;
		}
		
		int chaosPoints = getRioterPoints();
		double percentage = (double) chaosPoints / (double) chaos * 100;
		if (percentage > 80.0) {
			chaosFlash = true;
		}

		if (percentage < 0.0) {
			percentage = 0.0;
		} else if (percentage > 100.0) {
			percentage = 100.0;
		}
		
		String chaosLabelMessage = chaosColour + "Chaos";
		String chaosValueMessage = chaosColour + format.format(percentage) + "%";
		
		double backupTime = getRemainingSeconds();
		if (backupTime < 45.0) {
			backupFlash = true;
		}
		String backupTimeDisplay = UtilUI.getTimer((int) backupTime);
		
		String backupLabelMessage = backupColour + "Backup arrives";
		String backupValueMessage = backupColour + backupTimeDisplay;
		
		for (Board board : boardManager.getPlayerBoards()) {
			ObjectiveSide side = board.getSideObjective();
			side.updateRow(chaosLabel, chaosLabelMessage);
			side.updateRow(chaosRow, chaosValueMessage);
			side.updateRow(backupLabel, backupLabelMessage);
			side.updateRow(backupRow, backupValueMessage);
		}
	}
	
	public GameTeam getCops() {
		return getTeam("Cops");
	}
	
	public GameTeam getRioters() {
		return getTeam("Rioters");
	}
	
	/* Point management */

	private boolean riotersWin = false;

	private int breakBuffer = 0;
	private int fireBuffer = 0;
	private int explodeBuffer = 0;

	public void addRioterPoints(int points) {
		if (!isPlaying()) {
			return;
		}
		
		// Multiply the points to get a higher precision while still using integers
		points *= 100;
		
		
		// Don't multiply punishment points
		if (points > 0) {
			double pointMultiplier = 1 + ( ( getBalanceDifference() ) / 100 );
			int multipliedPoints = (int) ( points * pointMultiplier );
			
			for (Player player : UtilServer.players()) {
				if (player.isOp()) {
					UtilUI.sendActionBar(player, C.cBold + points + " -> " + multipliedPoints);
				}
			}
			
			points = multipliedPoints;
		}
		
		stats.increment(getRioters(), POINTS_KEY, points);
		
		float exp = (float) getPercentageChaos() / 100F;
		for (Player player : UtilServer.players()) {
			player.setExp(exp);
		}
	}
	
	public int getRioterPoints() {
		return stats.get(getRioters(), POINTS_KEY) / 100;
	}
	
	public double getBackupTime() {
		double playTime = getStateSeconds() - explainTime;
		return timeLimit - playTime;
	}
	
	public double getPercentageChaos() {
		return (double) getRioterPoints() / (double) chaos * 100.0;
	}
	
	public double getPercentageBackup() {
		double totalTime = timeLimit - explainTime;
		double playTime = getStateSeconds() - explainTime;
		
		return playTime / totalTime * 100.0;
	}
	
	/* Events */
	
	@EventHandler
	public void cancelDefaultBreak(BlockBreakEvent event) {
		if (getTeam(event.getPlayer()).equals(getRioters())) {
			event.setCancelled(true);
		} else if (!isBreakable(event.getBlock())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void breakBlock(BlockDamageEvent event) {
		breakBlock(event.getPlayer(), event.getBlock());
	}
	
	@EventHandler
	public void breakBlock(PlayerInteractEvent event) {
		if (!UtilEvent.isBlockClick(event) || !UtilEvent.isLeftClick(event)) {
			return;
		}
		
		breakBlock(event.getPlayer(), event.getClickedBlock());
	}
	
	private void breakBlock(Player player, Block block) {
		if (!isPlaying() || explaining || !canInteract(player)) {
			return;
		}
		
		if (getTeam(player).equals(getRioters())) {
			if (isBreakable(block)) {
				if (Timer.instance.use(player, null, "Break Block", 49L, false)) {
					addRioterPoints(1);
					breakBuffer++;
					UtilEffect.breakBlock(block);
				}
			}
		}
	}
	
	private boolean isBreakable(Block block) {
		return !unbreakable.contains(UtilMat.getData(block));
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		if (!isPlaying() || explaining || !canInteract(player)) {
			return;
		}
		
		if (getTeam(player).equals(getRioters())) {
			addRioterPoints(1);
		}
	}
	
	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		if (!isPlaying() || explaining) {
			return;
		}
		
		Iterator<Block> blockIt = event.blockList()
				.iterator();
		while (blockIt.hasNext()) {
			if (!isBreakable(blockIt.next())) {
				blockIt.remove();
			}
		}
		
		int size = event.blockList()
				.size();
				
		explodeBuffer += size;
		addRioterPoints(size);
	}
	
	@EventHandler
	public void onBurn(BlockBurnEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		if (!isBreakable(event.getBlock())) {
			for (Block fire : UtilBlock.getAdjacentBlocks(event.getBlock())) {
				if (fire.getType() == Material.FIRE) {
					fire.setType(Material.AIR);
				}
			}
			event.setCancelled(true);
		} else {
			fireBuffer += 1;
			addRioterPoints(1);
		}
		
	}
	
	@EventHandler
	public void onIgnite(BlockIgniteEvent event) {
		if (!isBreakable(event.getIgnitingBlock())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void preventSpread(BlockSpreadEvent event) {
		if (event.getSource().getType() == Material.FIRE) {
			event.setCancelled(true);
		}
	}
	
	private int copKillPoints = 30;
	// When 3 or more players kill a cop
	private int mobKillPoints = 50;

	private int rioterKillPoints = -10;
	
	@EventHandler
	public void onRioterDeath(CombatDeathEvent event) {
		if (!event.isPlayerKilled()) {
			return;
		}
		
		Player player = event.getKilledPlayer();
		if (!canInteract(player)) {
			return;
		}
		
		GameTeam team = getTeam(player);
		if (team.equals(getCops())) {
			if (event.getAssists()
					.size() >= 2) {
				addRioterPoints(mobKillPoints);
				announceChaos(mobKillPoints, "mob kill");
			} else {
				addRioterPoints(copKillPoints);
				announceChaos(copKillPoints, "cop death");
			}
		} else {
			addRioterPoints(rioterKillPoints);
			announceChaos(rioterKillPoints, "rioter death");
		}
	}
	
	@EventHandler
	public void announceDestructionPoints(TickEvent event) {
		if (event.is(Tick.SEC_1)) {
			if (explodeBuffer > 0) {
				announceChaos(explodeBuffer, "explosion");
				explodeBuffer = 0;
			}
		} else if (event.is(Tick.SEC_10)) {
			if (fireBuffer > 0) {
				announceChaos(fireBuffer, "fire");
				fireBuffer = 0;
			}
		} else if (event.is(Tick.SEC_5)) {
			if (breakBuffer > 0) {
				announceChaos(breakBuffer, "destruction");
				breakBuffer = 0;
			}
		}
	}
	
	private void announceChaos(int points, String reason) {
		String prefix = (points < 0) ? C.cBlue: C.cRed + "+";
		C.bRaw(prefix + points + C.cGray + " (" + reason + ")");
	}
	
	@Override
	protected List<GameTeam> getWinners() {
		return null;
	}
	
}
