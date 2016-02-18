package net.minegage.minigame.game.games.skywars;


import net.minegage.common.board.Board;
import net.minegage.common.board.objective.ObjectiveSide;
import net.minegage.common.build.ItemBuild;
import net.minegage.common.util.UtilArmour;
import net.minegage.common.util.UtilArmour.ArmourType;
import net.minegage.common.util.UtilMat;
import net.minegage.core.combat.DeathMessenger.DeathMessageMode;
import net.minegage.minigame.MinigameManager;
import net.minegage.minigame.board.helper.SurvivalHelper;
import net.minegage.minigame.game.GameFFA;
import net.minegage.minigame.game.GameType;
import net.minegage.minigame.game.common.loot.LootManager;
import net.minegage.minigame.game.common.loot.MultiLoot;
import net.minegage.minigame.game.games.skywars.kits.KitSurvivor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


public class GameSkywars
		extends GameFFA {

	private LootManager lootManager;

	public GameSkywars(MinigameManager manager) {
		super(manager, GameType.SKYWARS, new String[] { "Don't fall! Last person alive wins!", "Loot chests to get items!" }, new KitSurvivor());

		this.itemDropDeath = true;

		this.blockBreak = true;
		this.blockPlace = true;

		this.itemPickup = true;
		this.itemDrop = true;

		this.armourMove = true;

		this.blockPlaceDeny.add(UtilMat.getData(Material.CHEST));
		this.deathMessageMode = DeathMessageMode.ALL;

		this.explodeRegen = false;
		this.explodeDebris = true;

		this.itemDropDeath = true;


		getBoardManager().addBoardHelper(new SurvivalHelper(this));

		lootManager = new LootManager(this);

		ItemBuild cobble = ItemBuild.create(Material.COBBLESTONE);
		ItemBuild wood   = ItemBuild.create(Material.WOOD);
		ItemBuild log    = ItemBuild.create(Material.LOG);
		ItemBuild tnt    = ItemBuild.create(Material.TNT);

		ItemBuild goldApple = ItemBuild.create(Material.GOLDEN_APPLE);

		ItemBuild arrow = ItemBuild.create(Material.ARROW);

		ItemBuild egg  = ItemBuild.create(Material.EGG);
		ItemBuild snow = ItemBuild.create(Material.SNOW_BALL);
		ItemBuild pearl = ItemBuild.create(Material.ENDER_PEARL);

		// Normal loot
		lootManager.loot(0, Material.IRON_SWORD, Material.DIAMOND_SWORD, Material.IRON_AXE, Material.FISHING_ROD, Material.BOW, Material.FLINT_AND_STEEL);
		lootManager.loot(0, Material.WATER_BUCKET);

		lootManager.loot(0, Material.COBBLESTONE, 24, 32);
		lootManager.loot(0, Material.WOOD, 24, 32);
		lootManager.loot(0, Material.TNT);

		lootManager.loot(0, Material.SNOW_BALL, 2, 4);
		lootManager.loot(0, Material.EGG, 2, 4);

		lootManager.loot(0, new MultiLoot(lootManager.create(Material.BOW), lootManager.create(Material.ARROW, 2, 4)));

		for (ItemStack armour : UtilArmour.getArmourSet(ArmourType.LEATHER)) {
			lootManager.loot(0, armour);
		}
		for (ItemStack armour : UtilArmour.getArmourSet(ArmourType.GOLD)) {
			lootManager.loot(0, armour);
		}
		for (ItemStack armour : UtilArmour.getArmourSet(ArmourType.IRON)) {
			lootManager.loot(0, armour);
		}

		// Good loot
		lootManager.loot(1, Material.DIAMOND_SWORD, Material.DIAMOND_AXE, Material.FISHING_ROD);
		lootManager.loot(1, Material.BOW, Material.FLINT_AND_STEEL);
		lootManager.loot(1, Material.LAVA_BUCKET, Material.WATER_BUCKET);

		for (ItemStack armour : UtilArmour.getArmourSet(ArmourType.IRON)) {
			lootManager.loot(1, armour);
		}
		for (ItemStack armour : UtilArmour.getArmourSet(ArmourType.DIAMOND)) {
			lootManager.loot(1, armour);
		}

		lootManager.loot(0, new MultiLoot(lootManager.create(Material.BOW), lootManager.create(Material.ARROW, 8, 16)));

		lootManager.loot(1, Material.GOLDEN_APPLE, 1, 2);

		lootManager.loot(1, pearl.amount(3).item(), pearl.amount(2).item());

		lootManager.loot(0, Material.COBBLESTONE, 24, 32);
		lootManager.loot(0, Material.WOOD, 24, 32);

		lootManager.loot(0, Material.TNT, 2, 3);
	}

	@Override
	protected void onDisable() {
		super.onDisable();

		lootManager.disable();
	}

	@EventHandler
	public void fillChest(InventoryOpenEvent event) {
		Inventory inv = event.getInventory();

		if (!(inv.getHolder() instanceof BlockState)) {
			return;
		}

		Material type = ((BlockState) inv.getHolder()).getType();

		int tier;
		int minItems;
		int maxItems;
		if (type == Material.CHEST) {
			tier = 0;
			minItems = 3;
			maxItems = 6;
		} else if (type == Material.TRAPPED_CHEST) {
			tier = 1;
			minItems = 5;
			maxItems = 7;
		} else {
			return;
		}

		lootManager.fillInventory(inv, tier, minItems, maxItems);
	}

	@Override
	public void giveBoard(Player player, Board board) {
		super.giveBoard(player, board);

		ObjectiveSide side = board.getSideObjective();
		side.addRow("");

	}

	@Override
	public boolean endCheck() {
		return super.endCheckState(PlayerState.IN, 1);
	}

}
