package net.minegage.minigame.game.games.kitpvp;


import net.minegage.common.board.Board;
import net.minegage.common.board.objective.ObjectiveSide;
import net.minegage.common.board.objective.ObjectiveTag;
import net.minegage.common.datafile.DataFile;
import net.minegage.common.util.UtilMath;
import net.minegage.common.util.UtilServer;
import net.minegage.common.util.UtilTime;
import net.minegage.core.combat.CombatDamage;
import net.minegage.core.combat.DamageHistory;
import net.minegage.core.combat.DeathMessenger.DeathMessageMode;
import net.minegage.core.combat.KillAssistEvent;
import net.minegage.common.C;
import net.minegage.core.combat.event.CombatDeathEvent;
import net.minegage.core.spawn.SpawnCommandEvent;
import net.minegage.minigame.MinigameManager;
import net.minegage.minigame.game.GameFFA;
import net.minegage.minigame.game.GameType;
import net.minegage.minigame.game.games.kitpvp.kits.KitArcher;
import net.minegage.minigame.game.games.kitpvp.kits.KitKnight;
import net.minegage.minigame.game.games.kitpvp.kits.KitRanger;
import net.minegage.minigame.game.games.kitpvp.kits.KitRogue;
import net.minegage.minigame.game.games.kitpvp.kits.KitTank;
import net.minegage.minigame.game.games.kitpvp.kits.KitWarrior;
import net.minegage.minigame.game.games.kitpvp.shop.KitShopItem;
import net.minegage.minigame.game.games.kitpvp.shop.ShopMenu;
import net.minegage.minigame.item.SoupManager;
import net.minegage.minigame.kit.Kit;
import net.minegage.minigame.kit.SelectKitEvent;
import net.minegage.minigame.stats.StatTracker;
import net.minegage.minigame.stats.UpdatePlayerStatEvent;
import net.minegage.minigame.team.GameTeam;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.inventory.PlayerInventory;

import java.io.IOException;


public class GameKitPVP
		extends GameFFA {
		
	private ShopMenu shop;
	private SoupManager soupManager;
	
	private final double SAFEZONE_RADIUS = 15.5;
	
	public GameKitPVP(MinigameManager manager) {
		super(manager, GameType.KIT_PVP, new String[0], new KitKnight(), new KitArcher(), new KitTank(), new KitWarrior(), new KitRanger(),
				new KitRogue());
				
		this.shop = new ShopMenu(manager.getMinigame().getMenuManager(), this);
		this.soupManager = new SoupManager(this);
		
		for (Kit kit : getGlobalKits()) {
			if (kit instanceof KitPvpBase) {
				( (KitPvpBase) kit ).setShop(this.shop);
			}
		}
		
		this.lobby = false;
		this.explain = false;
		
		this.deathSpecItem = false;
		
		this.joinMessageLive = false;
		this.quitMessageLive = false;
		
		this.joinOut = false;
		this.deathOut = false;
		
		this.armourMove = false;
		this.itemDamage = false;

		this.minPlayers = 0;
		this.minPlayersAbsolute = 0;
		
		this.maxPlayers = 100;
		this.maxPlayersAbsolute = 125;

		this.explodeDebris = true;
		this.explodeRegen = true;

		this.deathMessageMode = DeathMessageMode.SIMPLE;
	}
	
	@Override
	protected void onDisable() {
		super.onDisable();

		shop.dispose();
		soupManager.disable();
	}
	
	@Override
	public void load(String mapName) throws IOException {
		super.load(mapName);
		addSafezone(getSpecSpawn(), SAFEZONE_RADIUS);
	}
	
	@Override
	public void createTeams() {
		createTeam("Green", C.cGreen);
		createTeam("Yellow", C.cYellow);
		createTeam("Orange", C.cGold);
		createTeam("Red", C.cRed);
	}
	
	@Override
	public void assignTeam(Player player) {
		getTeam("Green").addPlayer(player);
	}
	
	@Override
	public void giveBoard(Player player, Board board) {
		ObjectiveSide side = board.getSideObjective();
		
		String kit = defaultKit.getName();
		
		side.setRow(15, "");
		side.setRow(14, C.cRed + C.cBold + "Killstreak");
		side.setRow(13, "0");
		side.setRow(12, "");
		side.setRow(11, C.cGreenD + C.cBold + "Balance");
		side.setRow(10, "0");
		side.setRow(9, "");
		side.setRow(8, C.cGreen + C.cBold + "Kit");
		side.setRow(7, kit);
		side.setRow(6, "");
		
		ObjectiveTag tag = board.setTagObjective();
		tag.setSuffix("kills");
		
		// Initialize killsreak value under names of others
		StatTracker stats = getStatTracker();
		for (Player other : UtilServer.players()) {
			tag.setValue(other, stats.get(other, "killstreak"));
		}
		
		
	}
	
	@EventHandler
	public void onChooseKit(SelectKitEvent event) {
		Player player = event.getPlayer();
		Kit kit = event.getKit();
		
		Board board = boardManager.getBoard(player);
		board.getSideObjective()
				.updateRow(7, kit.getName());
	}
	
	private int[] soupRewards = { 4, 3, 3, 3, 3, 2, 2, 2, 2, 1, 1, 1, 1, 0 };
	
	private int getSoupReward(int killstreak) {
		if (killstreak >= 0 && killstreak < soupRewards.length) {
			return soupRewards[killstreak];
		}
		
		// Return the last number in the array
		return soupRewards[soupRewards.length - 1];
	}
	
	@EventHandler
	public void onIncrement(UpdatePlayerStatEvent event) {
		if (event.getStat()
				.equals("killstreak")) {
			Player player = event.getPlayer();
			int killstreak = event.getNewValue();
			
			// Update sidebar value
			Board board = boardManager.getBoard(player);
			board.getSideObjective()
					.updateRow(13, killstreak + "");
					
					
			// Update tag value
			for (Board b : boardManager.getPlayerBoards()) {
				b.getTagObjective()
						.setValue(player, killstreak);
			}
			
			// Update name colour
			String teamName = null;
			if (killstreak < 3) {
				teamName = "Green";
			} else if (killstreak < 7) {
				teamName = "Yellow";
			} else if (killstreak < 12) {
				teamName = "Orange";
			} else {
				teamName = "Red";
			}
			
			GameTeam currentTeam = getTeam(player);
			GameTeam newTeam = getTeam(teamName);
			
			if (!currentTeam.equals(newTeam)) {
				newTeam.addPlayer(player);
				
				if (!teamName.equals("Green")) {
					C.bMain("Game", player.getDisplayName() + C.cWhite + C.cBold + " has a killstreak of " + C.cRed + C.cBold + killstreak + C.cWhite
					                + C.cBold + " and their bounty has doubled!");
				}
				
			}
			
			player.updateInventory();
		} else if (event.getStat()
				.equals("kitpvp balance")) {
			Player player = event.getPlayer();
			int balance = event.getNewValue();
			
			Board board = boardManager.getBoard(player);
			board.getSideObjective()
					.updateRow(10, balance + "");
					
			// Update shop items to reflect new balance
			shop.giveItems(player);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onDeath(CombatDeathEvent event) {
		if (!event.isPlayerKilled()) {
			return;
		}
		
		Player killed = event.getKilledPlayer();
		
		if (event.isPlayerKiller()) {
			int killedKillstreak = getStatTracker().get(killed, "killstreak");
			
			int multiplier;
			if (killedKillstreak < 3) {
				multiplier = 1;
			} else if (killedKillstreak < 7) {
				multiplier = 2;
			} else if (killedKillstreak < 12) {
				multiplier = 4;
			} else {
				multiplier = 8;
			}
			
			Player pKiller = event.getKillerPlayer();
			getStatTracker().increment(pKiller, "kitpvp balance", 5 * multiplier);
			int killstreak = getStatTracker().increment(pKiller, "killstreak", 1);
			
			PlayerInventory inv = pKiller.getInventory();
			int reward = getSoupReward(killstreak);
			for (int i = 0; i < reward; i++) {
				inv.addItem(KitShopItem.SOUP_ITEM);
			}
		}
		
		getStatTracker().reset(killed, "killstreak");
	}
	
	@EventHandler
	public void onAssist(KillAssistEvent event) {
		if (!event.getPlayer()
				.isOnline()) {
			return;
		}
		
		getStatTracker().increment(event.getPlayer()
				.getPlayer(), "kitpvp balance", 2);
	}
	
	private double combatTagTime = 15.0;
	private double spawnMoveTime = 3.0;
	
	// Disabled for now; can be abused
	// @EventHandler
	public void combatTag(SpawnCommandEvent event) {
		Player player = event.getPlayer();
		DamageHistory history = gameManager.getMinigameManager()
				.getCombatManager()
				.getHistory()
				.get(player);
				
		if (history != null) {
			CombatDamage damage = history.getMostRecentLivingDamager();
			if (damage != null) {
				long timeSinceDamage = UtilTime.timePassedSince(damage.timestamp);
				double secondsSinceDamage = UtilTime.toSeconds(timeSinceDamage);
				
				if (secondsSinceDamage < combatTagTime) {
					// Player is combat tagged
					
					// Format the second value for displaying
					secondsSinceDamage = combatTagTime - secondsSinceDamage;
					secondsSinceDamage = UtilMath.round(secondsSinceDamage, 1);
					
					event.setCancelled(true);
					C.pMain(player, "Spawn", "You are " + C.fElem("combat tagged") + " for another " + C
							.fElem2(secondsSinceDamage + "s"));
					return;
				}
			}
		}
		
		long lastMoved = gameManager.getMinigame()
				.getMoveManager()
				.getMoveToken(player).lastPhysicalMoved;
				
		long timeSinceMove = UtilTime.timePassedSince(lastMoved);
		double secondsSinceMove = UtilTime.toSeconds(timeSinceMove);
		
		if (secondsSinceMove < spawnMoveTime) {
			// Player has moved within spawnMoveTime
			
			// Format the second value for displaying
			secondsSinceMove = spawnMoveTime - secondsSinceMove;
			secondsSinceMove = UtilMath.round(secondsSinceMove, 1);
			
			event.setCancelled(true);
			C.pMain(player, "Spawn", "Please don't move for another " + C.fElem(secondsSinceMove + "s") + " and try again");
			return;
		}
	}
	
	@EventHandler
	public void activateMine(BlockRedstoneEvent event) {
		if (event.getNewCurrent() != 0) {
			return;
		}
		
		Block block = event.getBlock();
		
		switch (block.getType()) {
		case WOOD_PLATE:
		case STONE_PLATE:
		case IRON_PLATE:
		case GOLD_PLATE:
			block.setType(Material.AIR);
			block.getWorld()
					.createExplosion(block.getLocation(), 5F);
		default:
			break;
		}
	}
	
	@Override
	public void loadWorldData(DataFile worldData) {
		// Do nothing
	}
	
	@Override
	public boolean endCheck() {
		return false;
	}
	
	public ShopMenu getShop() {
		return shop;
	}
	
}
