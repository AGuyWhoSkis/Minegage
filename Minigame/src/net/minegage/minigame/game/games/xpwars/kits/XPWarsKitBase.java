package net.minegage.minigame.game.games.xpwars.kits;


import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import net.minegage.minigame.game.games.xpwars.GameXPWars;
import net.minegage.minigame.kit.Kit;
import net.minegage.minigame.kit.attrib.Attrib;


public abstract class XPWarsKitBase
		extends Kit {
		
	public XPWarsKitBase(String name, String[] description, Attrib... attributes) {
		super(name, description, attributes);
	}
	
	@Override
	protected void giveItems(PlayerInventory inv) {
		inv.setItem(8, ( (GameXPWars) getGame() ).enchantItem);
		
		( (Player) inv.getHolder() ).setLevel(1337);
	}
	
}
