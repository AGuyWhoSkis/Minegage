package net.minegage.minigame.game.games.kitpvp.kits;


import net.minegage.common.util.UtilItem;
import net.minegage.minigame.game.games.kitpvp.KitPvpBase;
import net.minegage.minigame.kit.attrib.AttribArmourPiercing;
import net.minegage.minigame.kit.attrib.AttribEffect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;


public class KitRogue
		extends KitPvpBase {
		
	public KitRogue() {
		super("Rogue", new String[] { "Normal (armour piercing) melee, weak defense", "Very agile and quick" }, new AttribArmourPiercing(
				Material.DIAMOND_SWORD));
				
		AttribEffect effects = new AttribEffect("speed", PotionEffectType.SPEED.createEffect(Integer.MAX_VALUE, 0));
		effects.addPotion(PotionEffectType.JUMP.createEffect(Integer.MAX_VALUE, 0));
		
		addAttribute(effects);
		
		this.mobBoots = UtilItem.create(Material.LEATHER_BOOTS);
		this.mobLegs = UtilItem.create(Material.CHAINMAIL_LEGGINGS);
		this.mobChest = UtilItem.create(Material.CHAINMAIL_CHESTPLATE);
		this.mobHelm = UtilItem.create(Material.IRON_HELMET);
		
		ItemStack sword = UtilItem.create(Material.IRON_SWORD);
		
		this.mobHand = sword;
		
		addSwordUpgrades(sword.getType(), 20, 30);
		addArmourUpgrades(10, 15, 25);
	}
	
	@Override
	protected void giveItems(PlayerInventory inv) {
		super.giveItems(inv);
		giveMobItems(inv);
	}
	
	
	
	
}
