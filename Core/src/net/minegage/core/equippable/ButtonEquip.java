package net.minegage.core.equippable;


import net.minegage.common.menu.button.Button;
import net.minegage.common.misc.Click;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import net.minegage.core.rank.RankManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class ButtonEquip
		extends Button {
		
	protected EquippableManager equippableManager;
	protected Equippable equippable;
	protected boolean closeOnEquip;
	protected boolean notify = true;
	
	public ButtonEquip(EquippableManager equippableManager, Equippable equippable, boolean closeOnEquip) {
		this.equippableManager = equippableManager;
		this.equippable = equippable;
		this.closeOnEquip = closeOnEquip;
	}
	
	public ButtonEquip(EquippableManager equippableManager, Equippable equippable) {
		this(equippableManager, equippable, true);
	}

	@Override
	public boolean onClick(Player player, Click click) {
		String permOverride = equippable.getPermOverride();
		Rank rank = equippable.getRank();
		
		if (permOverride != null && !player.hasPermission(permOverride)) {
			C.pMain(player, equippable.getType(), "You haven't earned that " + equippable.getType() + " yet");
			player.closeInventory();
			return false;
		}
		
		if (rank != null && !RankManager.instance.hasPermission(player, equippable.getRank())) {
			String type = equippable.getType();
			C.pMain(player, type, "That " + type.toLowerCase() + " requires rank " + rank.getDisplayName() + C.sBody
			                      + ". Please consider donating at " + C.sOut + "donate.minegage.com" + C.sBody + " to unlock!");
			player.closeInventory();
			return false;
		}
		
		boolean canEquip = canEquip(player, click);
		
		if (!canEquip) {
			return false;
		}
		
		equip(player);
		
		if (closeOnEquip) {
			player.closeInventory();
		}
		
		return true;
	}
	
	/**
	 * Optional override
	 * 
	 * @param player
	 *        The player attempting to equip the Equippable
	 * @param click
	 *        The list of button clicks which can be used to describe the click type used
	 */
	public boolean canEquip(Player player, Click click) {
		return true;
	}
	
	public void equip(Player player) {
		List<Equippable> allEquipped = new ArrayList<>(equippableManager.getEquipped(player));
		boolean isEquipped = allEquipped.contains(equippable);
		
		if (isEquipped) {
			equippableManager.unequip(player, equippable, notify);
		} else {
			
			// Remove gadgets which have the same equip slot as the one about to
			// be equipped
			if (equippable.isPhysical()) {
				for (Equippable equipped : allEquipped) {
					// Checking if $equipped is physical is not necessary, this
					// check will work anyway
					if (equipped.getEquipSlot() == equippable.getEquipSlot()) {
						equippableManager.unequip(player, equipped, false);
					}
				}
			}
			
			equippableManager.equip(player, equippable, notify);
		}
	}
	
	
}
