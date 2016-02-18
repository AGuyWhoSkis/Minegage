package net.minegage.minigame.game.games.riot.kit;


import net.minegage.common.build.ItemBuild;
import net.minegage.common.C;
import net.minegage.minigame.game.games.riot.kit.attrib.AttribDefuseTNT;
import net.minegage.minigame.kit.Kit;
import net.minegage.minigame.kit.attrib.AttribEffect;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;


public class KitCop
		extends Kit {
		
	public KitCop() {
		super("Cop", new String[] { });
		
		addAttribute(new AttribDefuseTNT());
		addAttribute(new AttribEffect("Speed Bonus", new String[] {"Permanent Speed 1" }, PotionEffectType.SPEED
				.createEffect(Integer.MAX_VALUE, 0)));
				
		mobHelm = new ItemStack(Material.DIAMOND_HELMET);
		mobChest = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
		mobLegs = new ItemStack(Material.CHAINMAIL_LEGGINGS);
		
		mobBoots = ItemBuild.create(Material.LEATHER_BOOTS)
				.colour(Color.BLUE)
				.item();
				
		mobHand = new ItemStack(Material.WOOD_SWORD);
	}
	
	@Override
	protected void giveItems(PlayerInventory inv) {
		giveMobItems(inv);
		
		ItemStack bucket = new ItemStack(Material.BUCKET);
		ItemStack defuse = ItemBuild.create(Material.SHEARS)
				.name(C.cBold + "TNT Defuser " + C.cReset + " (right click)")
				.item();
				
		ItemStack poison = new ItemStack(Material.POTION);
		Potion potion = new Potion(PotionType.POISON);
		potion.setSplash(true);
		potion.apply(poison);
		
		inv.addItem(defuse);
		inv.addItem(poison);
		inv.addItem(poison);
		inv.addItem(bucket);
	}
	
}
