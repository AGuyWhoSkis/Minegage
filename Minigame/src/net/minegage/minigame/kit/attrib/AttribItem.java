package net.minegage.minigame.kit.attrib;


import net.minegage.common.misc.Click;
import net.minegage.common.util.UtilEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;


public abstract class AttribItem
		extends Attrib {
		
	private Material[] types;
	
	public AttribItem(String name, String[] desc, Material... item) {
		super(name, desc);
		this.types = item;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public final void onInteract(PlayerInteractEvent event) {
		if (!UtilEvent.isClick(event)) {
			return;
		}

		if (!appliesTo(event.getPlayer())) {
			return;
		}

		ItemStack clicked = event.getItem();
		if (!isItem(clicked)) {
			return;
		}

		event.setUseItemInHand(Result.DENY);
		
		Click click = Click.from(event);
		onClick(event.getPlayer(), clicked, click);

		event.getPlayer()
				.updateInventory();
	}
	
	public abstract void onClick(Player player, ItemStack item, Click click);
	
	public boolean isItem(ItemStack item) {
		if (item == null) {
			return false;
		}
		
		Material material = item.getType();
		
		for (Material mat : types) {
			if (material == mat) {
				return true;
			}
		}
		
		return false;
	}
	
}
