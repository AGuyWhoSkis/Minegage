package net.minegage.minigame.game.games.skywars.kits;

import net.minegage.minigame.kit.Kit;
import net.minegage.minigame.kit.attrib.AttribThrowableTNT;
import org.bukkit.inventory.PlayerInventory;

public class KitFletcher
		extends Kit {

	public KitFletcher() {
		super("Fletcher", new String[] {}, new AttribThrowableTNT());
	}

	@Override
	protected void giveItems(PlayerInventory inv) {

	}
}
