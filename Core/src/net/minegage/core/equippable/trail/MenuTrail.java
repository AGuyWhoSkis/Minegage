package net.minegage.core.equippable.trail;


import net.minegage.common.menu.MenuManager;
import net.minegage.common.menu.MenuTemp;
import net.minegage.common.util.UtilItem;
import net.minegage.common.C;
import net.minegage.core.equippable.Equippable;
import net.minegage.core.equippable.EquippableManager;
import net.minegage.core.equippable.MenuEquip;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;


public class MenuTrail
		extends MenuTemp {
		
	public static final int[] DISPLAY_SLOTS = { 4, 3, 5, 2, 6, 1, 7 };
	
	private EquippableManager equippableManager;
	
	public MenuTrail(MenuManager menuManager, EquippableManager equippableManager, Player player) {
		super(menuManager, "Trails", MenuEquip.RAW_NAME, 6, player);
		this.equippableManager = equippableManager;
		addComponents();
	}
	
	@Override
	public void addComponents() {
		// Do nothing
	}
	
	@Override
	public void addItems(Player player, Inventory inventory) {
		int displayIndex = 0;
		
		for (Equippable equippable : equippableManager.getEquippables()) {
			if (!( equippable instanceof Trail )) {
				continue;
			}
			
			Trail trail = (Trail) equippable;
			
			ItemStack item = trail.getDisplayItem();
			ButtonEquipTrail button = new ButtonEquipTrail(equippableManager, menuManager, trail);
			addButton(trail.getDisplaySlot(), button);
			inventory.setItem(trail.getDisplaySlot(), trail.getDisplayItem());
			
			if (trail.getEquipped()
					.contains(player.getUniqueId())) {
				ItemStack unequipItem = item.clone();
				UtilItem.stripLore(unequipItem);
				UtilItem.addLore(unequipItem, new ArrayList<>(Arrays.asList("", C.iOut + "Click " + C.iMain + "to unequip!")));
				
				int displaySlot = DISPLAY_SLOTS[displayIndex++];
				ButtonUnequipTrail unequipButton = new ButtonUnequipTrail(equippableManager, menuManager, trail);
				addButton(displaySlot, unequipButton);
				inventory.setItem(displaySlot, unequipItem);
			}
		}
	}
	
}
