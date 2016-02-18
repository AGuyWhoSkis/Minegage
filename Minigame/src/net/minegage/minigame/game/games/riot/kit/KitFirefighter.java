package net.minegage.minigame.game.games.riot.kit;


import net.minegage.common.build.ItemBuild;
import net.minegage.common.C;
import net.minegage.minigame.game.games.riot.kit.attrib.AttribDefuseTNT;
import net.minegage.minigame.game.games.riot.kit.attrib.AttribExtinguish;
import net.minegage.minigame.game.games.riot.kit.attrib.AttribSnowballExtinguish;
import net.minegage.minigame.kit.Kit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;


public class KitFirefighter
		extends Kit {
		
	public KitFirefighter() {
		super("Firefighter", new String[] { "Specializes in fighting fires!" }, new AttribDefuseTNT(), new AttribExtinguish(),
		      new AttribSnowballExtinguish());
				
		mobHelm = ItemBuild.create(Material.LEATHER_HELMET)
				.colour(Color.RED)
				.item();
				
		mobChest = ItemBuild.create(Material.LEATHER_CHESTPLATE)
				.colour(Color.BLUE)
				.item();
				
		mobLegs = ItemBuild.create(Material.LEATHER_LEGGINGS)
				.colour(Color.BLUE)
				.item();
				
		mobBoots = ItemBuild.create(Material.LEATHER_BOOTS)
				.colour(Color.BLUE)
				.item();
				
		mobHand = new ItemStack(Material.STONE_AXE);
	}
	
	@Override
	protected void giveItems(PlayerInventory inv) {
		giveMobItems(inv);
		
		ItemStack bucket = new ItemStack(Material.BUCKET);
		ItemStack defuse = ItemBuild.create(Material.SHEARS)
				.name(C.cBold + "TNT Defuser " + C.cReset + " (right click)")
				.item();
				
		ItemStack snowball = new ItemStack(Material.SNOW_BALL, 48);
		
		inv.addItem(snowball);
		inv.addItem(defuse);
		inv.addItem(bucket);
	}
	
}
