package net.minegage.core.equippable.trail;

import net.minegage.core.equippable.Equippable;
import net.minegage.core.equippable.EquippableManager;
import net.minegage.core.rank.Rank;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Trail 
		extends Equippable {
	
	public Trail(EquippableManager manager, String name, ItemStack displayItem, int displaySlot, Rank rank) {
		super(manager, "Trail", name, displayItem, displaySlot, rank);
	}
	
	public abstract void play(Player player, Location location);
	
	public abstract Location getLocation(Player player);
	
}
