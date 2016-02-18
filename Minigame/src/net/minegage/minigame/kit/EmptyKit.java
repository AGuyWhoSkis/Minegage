package net.minegage.minigame.kit;


import org.bukkit.inventory.PlayerInventory;

import net.minegage.core.mob.MobType;


public class EmptyKit
		extends Kit {
		
	public EmptyKit(String name, String[] desc, MobType type) {
		super(name, desc);
		mobType = type;
	}
	
	@Override
	protected void giveItems(PlayerInventory inv) {
		// Do nothing
	}
	
}
