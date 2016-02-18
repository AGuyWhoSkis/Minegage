package net.minegage.minigame.game.games.spleefrun.kits;


import net.minegage.minigame.kit.Kit;
import org.bukkit.inventory.PlayerInventory;


public class KitDefault
		extends Kit {
		
	public KitDefault() {
		super("Default", new String[] { "The default Spleef Run kit" });
	}
	
	@Override
	protected void giveItems(PlayerInventory inv) {
	
	}
	
}
