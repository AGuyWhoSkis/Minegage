package net.minegage.core.equippable;


import net.minegage.common.menu.Menu;
import net.minegage.common.menu.MenuManager;
import net.minegage.common.menu.button.Button;
import net.minegage.common.misc.Click;
import net.minegage.common.util.UtilItem;
import net.minegage.common.C;
import net.minegage.core.equippable.gadget.armour.MenuWardrobe;
import net.minegage.core.equippable.gadget.item.GadgetItem;
import net.minegage.core.equippable.trail.MenuTrail;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class MenuEquip
		extends Menu {
		
	public static final String RAW_NAME = "perk menu";
	private EquippableManager equippableManager;
	
	public MenuEquip(MenuManager manager, EquippableManager equippableManager) {
		super(manager, "Perk Menu", RAW_NAME, "close", 4);
		this.equippableManager = equippableManager;
		addComponents();
	}
	
	@Override
	public void addComponents() {
		List<String> trailLore = new ArrayList<>();
		trailLore.add("");
		trailLore.add(C.cGreen + "Click " + C.cWhite + "to open " + C.cYellow + "trail " + C.cWhite + "menu!");
		
		List<String> armourLore = new ArrayList<>();
		armourLore.add("");
		armourLore.add(C.cGreen + "Click " + C.cWhite + "to open " + C.cYellow + "wardrobe" + C.cWhite + "!");
		
		ItemStack trailItem = UtilItem.create(Material.BLAZE_POWDER, C.cBold + "Trails", trailLore);
		ItemStack armourItem = UtilItem.create(Material.LEATHER, C.cBold + "Wardrobe", armourLore);
		
		addButton(getSlot(2, 2), new Button() {
			@Override
			public boolean onClick(Player player, Click click) {
				Menu menu = new MenuTrail(menuManager, equippableManager, player);
				menu.open(player);
				return true;
			}
		}, trailItem);
		
		addButton(getSlot(3, 2), new Button() {
			@Override
			public boolean onClick(Player player, Click click) {
				Menu menu = new MenuWardrobe(menuManager, equippableManager, player);
				menu.open(player);
				return true;
			}
		}, armourItem);
		
		for (Equippable gadget : equippableManager.getEquippables()) {
			if (gadget instanceof GadgetItem) {
				addGadget(gadget);
			}
		}
	}
	
	@Override
	public void addItems(Player player, Inventory inventory) {
		// Do nothing
	}
	
	private void addGadget(Equippable gadget) {
		ButtonEquip button = new ButtonEquip(equippableManager, gadget);
		addButton(gadget.getDisplaySlot(), button, gadget.getDisplayItem());
	}
	
}
