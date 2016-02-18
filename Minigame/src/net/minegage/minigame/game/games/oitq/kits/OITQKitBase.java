package net.minegage.minigame.game.games.oitq.kits;


import net.minegage.minigame.kit.attrib.Attrib;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.minegage.minigame.kit.Kit;

import net.minegage.core.mob.MobType;


public abstract class OITQKitBase
		extends Kit {
		
	protected ItemStack bow = new ItemStack(Material.BOW);
	protected ItemStack arrow = new ItemStack(Material.ARROW);
	
	public OITQKitBase(String name, String[] desc, Attrib... attributes) {
		super(name, desc, attributes);
		
		this.mobType = MobType.SKELETON;
	}
	
}
