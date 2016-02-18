package net.minegage.minigame.game.games.kitpvp.kits;


import net.minegage.common.C;
import net.minegage.common.util.UtilItem;
import net.minegage.minigame.game.games.kitpvp.KitPvpBase;
import net.minegage.minigame.kit.attrib.AttribProjectileDeflect;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class KitTank
		extends KitPvpBase {

	public KitTank() {
		super("Tank", new String[] {"Normal melee, very strong defense", C.sOut + "Good for learning to use soup!"},
		      new AttribProjectileDeflect(25.0, EntityType.ARROW));

		ItemStack sword = UtilItem.create(Material.IRON_AXE);
		mobHand = sword;

		mobHelm = new ItemStack(Material.DIAMOND_HELMET);
		mobChest = new ItemStack(Material.DIAMOND_CHESTPLATE);
		mobLegs = new ItemStack(Material.IRON_LEGGINGS);
		mobBoots = new ItemStack(Material.IRON_BOOTS);

		addSwordUpgrades(sword.getType(), 15, 20);
	}

	@Override
	protected void giveItems(PlayerInventory inv) {
		super.giveItems(inv);
		giveMobItems(inv);
	}

}
