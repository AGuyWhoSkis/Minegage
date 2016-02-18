package net.minegage.minigame.game.games.kitpvp.shop;


import net.minegage.common.menu.button.Button;
import net.minegage.common.misc.Click;
import org.bukkit.entity.Player;


public class ShopButton
		extends Button {
		
	private ShopMenu menu;
	private KitShopItem item;
	
	public ShopButton(ShopMenu menu, KitShopItem item) {
		this.menu = menu;
		this.item = item;
	}
	
	@Override
	public boolean onClick(Player player, Click click) {
		return menu.attemptBuy(player, item);
	}
	
}
