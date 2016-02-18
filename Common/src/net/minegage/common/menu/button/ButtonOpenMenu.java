package net.minegage.common.menu.button;


import net.minegage.common.menu.MenuManager;
import net.minegage.common.misc.Click;
import org.bukkit.entity.Player;


public class ButtonOpenMenu
		extends Button {
		
	private MenuManager menuManager;
	private String menuName;
	
	
	public ButtonOpenMenu(MenuManager menuManager, String menuName) {
		this.menuManager = menuManager;
		this.menuName = menuName;
	}
	
	@Override
	public boolean onClick(Player player, Click click) {
		menuManager.open(player, menuName);
		return true;
	}
	
}
