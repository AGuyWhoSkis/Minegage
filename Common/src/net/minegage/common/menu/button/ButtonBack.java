package net.minegage.common.menu.button;


import net.minegage.common.menu.Menu;
import net.minegage.common.menu.MenuManager;
import net.minegage.common.misc.Click;
import org.bukkit.entity.Player;


public class ButtonBack
		extends Button {
		
	private String prevRawName;
	private MenuManager manager;
	
	public ButtonBack(Menu menu) {
		this.prevRawName = menu.getPrevRawName();
		this.manager = menu.getMenuManager();
	}
	
	@Override
	public boolean onClick(Player player, Click click) {
		if (prevRawName == "close") {
			player.closeInventory();
		} else {
			manager.open(player, prevRawName);
		}
		
		return true;
	}
	
}
