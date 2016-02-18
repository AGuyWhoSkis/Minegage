package net.minegage.core.equippable.gadget.armour;


import net.minegage.common.menu.button.Button;
import net.minegage.common.misc.Click;
import net.minegage.common.C;
import net.minegage.core.equippable.Equippable;
import net.minegage.core.equippable.EquippableManager;
import org.bukkit.entity.Player;

import java.util.List;


public class ButtonUnequipArmour
		extends Button {
		
	private EquippableManager equippableManager;
	private int slot;
	
	public ButtonUnequipArmour(EquippableManager equippableManager, int slot) {
		this.equippableManager = equippableManager;
		this.slot = slot;
		
		this.unsuccessful = null;
	}
	
	@Override
	public boolean onClick(Player player, Click click) {
		List<Equippable> gadgets = equippableManager.getEquipped(player);
		
		Equippable gadget = null;
		
		for (Equippable g : gadgets) {
			if (g.getEquipSlot() == this.slot) {
				gadget = g;
			}
		}
		
		if (gadget == null) {
			C.pMain(player, "Gadget", "You don't have a gadget equipped in that slot!");
			return false;
		}
		
		equippableManager.unequip(player, gadget, true);
		return true;
	}
	
}
