package net.minegage.core.equippable.gadget.armour;


import net.minegage.common.menu.MenuManager;
import net.minegage.common.menu.MenuTemp;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.util.UtilArmour.ArmourSlot;
import net.minegage.common.util.UtilItem;
import net.minegage.common.C;
import net.minegage.core.equippable.ButtonEquip;
import net.minegage.core.equippable.Equippable;
import net.minegage.core.equippable.EquippableManager;
import net.minegage.core.equippable.MenuEquip;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class MenuWardrobe
		extends MenuTemp {
		
	private EquippableManager equippableManager;
	
	public MenuWardrobe(MenuManager manager, EquippableManager equippableManager, Player player) {
		super(manager, "Wardrobe", MenuEquip.RAW_NAME, 6, player);
		this.equippableManager = equippableManager;
		addComponents();
	}
	
	@Override
	public void addComponents() {
		for (Equippable gadget : equippableManager.getEquippables()) {
			if (gadget instanceof GadgetArmour) {
				ButtonEquip button = new ButtonEquip(equippableManager, gadget, false);
				addButton(gadget.getDisplaySlot(), button, gadget.getDisplayItem());
			}
		}
		
		int row = 1;
		
		for (ArmourSlot armourSlot : ArmourSlot.values()) {
			List<String> lore = new ArrayList<>();
			lore.add("");
			lore.add(C.iOut + "Click " + C.cReset + "to unequip!");
			ItemStack item = UtilItem.create(Material.REDSTONE_BLOCK, "Armour - " + armourSlot.getName(), lore);

			ButtonUnequipArmour button = new ButtonUnequipArmour(equippableManager, armourSlot.getSlot());
			addButton(UtilItem.getSlot(1, row++), button, item);
		}
	}
	
	@Override
	public void addItems(Player player, Inventory inventory) {
		// Do nothing
	}
	
	@EventHandler
	public void updateWardrobeInventory(TickEvent event) {
		if (event.isNot(Tick.TICK_1)) {
			return;
		}
		
		for (ItemStack item : player.getOpenInventory()
				.getTopInventory()
				.getContents()) {
				
			if (item == null) {
				continue;
			}
			if (item.getType()
					.equals(Material.AIR)) {
				continue;
			}
			if (!item.hasItemMeta()) {
				continue;
			}
			if (!item.getItemMeta()
					.hasDisplayName()) {
				continue;
			}
			
			if (!item.getItemMeta()
					.getDisplayName()
					.contains("Rainbow")) {
				continue;
			}
			
			equippableManager.getGadgetManager()
					.colourArmour(item, player);
		}
		
		player.updateInventory();
	}
	
}
