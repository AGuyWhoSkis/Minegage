package net.minegage.core.equippable.gadget.item;


import net.minegage.core.equippable.EquippableManager;
import net.minegage.core.equippable.gadget.Gadget;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public abstract class GadgetItem
		extends Gadget {
		
	public static final int EQUIP_SLOT = 2;
	
	public GadgetItem(EquippableManager manager, String name, ItemStack item, int displaySlot, Rank rank) {
		super(manager, name, item, displaySlot, rank, EQUIP_SLOT);
	}
	
	public abstract void use(Player player);
	
}
