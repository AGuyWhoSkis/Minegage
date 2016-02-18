package net.minegage.core.equippable.gadget.armour;


import net.minegage.common.util.UtilArmour.ArmourSlot;
import net.minegage.core.equippable.EquippableManager;
import net.minegage.core.equippable.gadget.Gadget;
import net.minegage.core.rank.Rank;
import org.bukkit.inventory.ItemStack;


public class GadgetArmour
		extends Gadget {
		
	public GadgetArmour(EquippableManager manager, String name, ItemStack item, ArmourSlot slot, int displaySlot, Rank rank) {
		super(manager, name, item, displaySlot, rank, slot.getSlot());
	}
	
}
