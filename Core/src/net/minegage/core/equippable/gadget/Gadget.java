package net.minegage.core.equippable.gadget;


import net.minegage.core.equippable.Equippable;
import net.minegage.core.equippable.EquippableManager;
import net.minegage.core.rank.Rank;
import org.bukkit.inventory.ItemStack;


public abstract class Gadget
		extends Equippable {
		
	public static final String GADGET_METADATA_KEY = "fromgadget";
	
	public Gadget(EquippableManager manager, String name, ItemStack item, int displaySlot, Rank rank, int equipSlot) {
		super(manager, "Gadget", name, item, displaySlot, rank, equipSlot);
	}
	
}
