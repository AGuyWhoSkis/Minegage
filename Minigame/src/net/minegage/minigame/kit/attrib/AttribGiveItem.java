package net.minegage.minigame.kit.attrib;

import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class AttribGiveItem
		extends Attrib {

	private ItemStack item;
	private Tick interval;
	private int max;

	public AttribGiveItem(ItemStack item, Tick interval, int max, String name, String... desc) {
		super(name, desc);

		this.interval = interval;
	}

	@Override
	public void apply(Player player) {
		// Do nothing
	}

	@EventHandler
	public void giveItem(TickEvent event) {
		if (event.isNot(interval)) {
			return;
		}

		for (Player player : getGame().getPlayersIn()) {
			if (appliesTo(player)) {

			}
		}


	}




}
