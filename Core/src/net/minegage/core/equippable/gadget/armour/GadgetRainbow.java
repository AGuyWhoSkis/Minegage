package net.minegage.core.equippable.gadget.armour;


import net.minegage.common.util.UtilArmour.ArmourSlot;
import net.minegage.core.equippable.EquippableManager;
import net.minegage.core.rank.Rank;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class GadgetRainbow
		extends GadgetArmour {
		
	public GadgetRainbow(EquippableManager manager, String name, ItemStack item, ArmourSlot slot, int displaySlot, Rank rank) {
		super(manager, name, item, slot, displaySlot, rank);
	}
	
	public static List<Color> colourSequence = new ArrayList<>();
	
	static {
		double shades = 50D;
		double step = 1.0 / shades;
		
		for (double hue = 0.0; hue <= 1.0; hue += step) {
			java.awt.Color color = java.awt.Color.getHSBColor((float) hue, 1F, 1F);
			Color bukkitColor = Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
			colourSequence.add(bukkitColor);
		}
	}
	
}
