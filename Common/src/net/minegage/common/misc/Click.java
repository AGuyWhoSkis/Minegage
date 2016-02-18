package net.minegage.common.misc;


import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;


public class Click {
	
	private ClickButton button;
	private ClickTarget target;
	private boolean shifting;
	
	public Click(ClickButton button, ClickTarget target, boolean shifting) {
		this.button = button;
		this.target = target;
		this.shifting = shifting;
	}
	
	public ClickButton getButton() {
		return button;
	}
	
	public ClickTarget getTarget() {
		return target;
	}
	
	public boolean getShifting() {
		return shifting;
	}
	
	public static enum ClickButton {
		LEFT,
		MIDDLE,
		RIGHT,
		OTHER;
	}
	
	public static enum ClickTarget {
		BLOCK,
		AIR,
		ITEM
	}
	
	public static Click from(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		ClickButton button;
		ClickTarget target;
		boolean shifting = player.isSneaking();
		
		Action action = event.getAction();
		
		if (action == Action.LEFT_CLICK_AIR) {
			button = ClickButton.LEFT;
			target = ClickTarget.AIR;
		} else if (action == Action.LEFT_CLICK_BLOCK) {
			button = ClickButton.LEFT;
			target = ClickTarget.BLOCK;
		} else if (action == Action.RIGHT_CLICK_AIR) {
			button = ClickButton.RIGHT;
			target = ClickTarget.BLOCK;
		} else if (action == Action.RIGHT_CLICK_BLOCK) {
			button = ClickButton.RIGHT;
			target = ClickTarget.BLOCK;
		} else {
			return null;
		}
		
		return new Click(button, target, shifting);
	}
	
	public static Click from(InventoryClickEvent event) {
		ClickType type = event.getClick();
		
		ClickButton button;
		ClickTarget target = ClickTarget.ITEM;
		boolean shifting = false;
		
		if (type == ClickType.LEFT) {
			button = ClickButton.LEFT;
		} else if (type == ClickType.RIGHT) {
			button = ClickButton.RIGHT;
		} else if (type == ClickType.MIDDLE) {
			button = ClickButton.MIDDLE;
		} else if (type == ClickType.SHIFT_LEFT) {
			button = ClickButton.LEFT;
			shifting = true;
		} else if (type == ClickType.SHIFT_RIGHT) {
			button = ClickButton.RIGHT;
			shifting = true;
		} else if (type == ClickType.WINDOW_BORDER_LEFT) {
			button = ClickButton.LEFT;
		} else if (type == ClickType.WINDOW_BORDER_RIGHT) {
			button = ClickButton.RIGHT;
		} else {
			button = ClickButton.OTHER;
		}
		
		return new Click(button, target, shifting);
	}
	
}
