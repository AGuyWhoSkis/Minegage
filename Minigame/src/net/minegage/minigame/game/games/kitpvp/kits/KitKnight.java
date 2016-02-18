package net.minegage.minigame.game.games.kitpvp.kits;


import net.minegage.common.util.UtilArmour;
import net.minegage.common.util.UtilArmour.ArmourType;
import net.minegage.common.util.UtilItem;
import net.minegage.core.mob.MobType;
import net.minegage.minigame.game.games.kitpvp.KitPvpBase;
import org.bukkit.Material;
import org.bukkit.inventory.PlayerInventory;


public class KitKnight
		extends KitPvpBase {
		
	public KitKnight() {
		super("Knight", new String[] { "Strong melee, normal defense" });
		this.mobType = MobType.ZOMBIE;
		
		setMobItems(UtilArmour.getArmourSet(ArmourType.IRON, UtilItem.create(Material.DIAMOND_SWORD)));
		addArmourUpgrades(25);
		addSwordUpgrades(Material.DIAMOND_SWORD, 20, 30);
	}
	
	@Override
	protected void giveItems(PlayerInventory inv) {
		super.giveItems(inv);
		giveMobItems(inv);
	}
	
}
