package net.minegage.minigame.game.games.riot.kit;


import net.minegage.common.util.Rand;
import net.minegage.minigame.game.games.riot.GameRiot;
import net.minegage.minigame.kit.Kit;
import net.minegage.minigame.kit.attrib.AttribThrowableTNT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Set;


public class KitRioter
		extends Kit {

	public KitRioter() {
		super("Rioter", new String[] { "Receives random items to riot with!" }, new AttribThrowableTNT());
	}
	
	@Override
	protected void giveItems(PlayerInventory inv) {
		ItemStack head = null;
		ItemStack chest = null;
		ItemStack legs = null;
		ItemStack boots = null;
		
		double chance = 20;
		if (( (GameRiot) getGame() ).isRiotBoosted()) {
			chance = 40;
		}
		
		if (Rand.chance(chance)) {
			head = new ItemStack(Material.LEATHER_HELMET);
		}
		if (Rand.chance(chance)) {
			chest = new ItemStack(Material.LEATHER_CHESTPLATE);
		}
		if (Rand.chance(chance)) {
			legs = new ItemStack(Material.LEATHER_LEGGINGS);
		}
		if (Rand.chance(chance)) {
			boots = new ItemStack(Material.LEATHER_BOOTS);
		}
		
		inv.setHelmet(head);
		inv.setChestplate(chest);
		inv.setLeggings(legs);
		inv.setBoots(boots);

		for (ItemStack item : getRandLoot()) {
			inv.addItem(item);
		}

	}
	
	private Set<ItemStack> getRandLoot() {
		return ( (GameRiot) getGame() ).getRandLoot();
	}
	
}
