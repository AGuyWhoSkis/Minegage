package net.minegage.core.equippable.trail;


import net.minegage.common.menu.MenuManager;
import net.minegage.common.menu.button.Button;
import net.minegage.common.misc.Click;
import net.minegage.core.equippable.EquippableManager;
import org.bukkit.entity.Player;


public class ButtonUnequipTrail
		extends Button {
		
	private EquippableManager equippableManager;
	private MenuManager menuManager;
	private Trail trail;
	
	public ButtonUnequipTrail(EquippableManager equippableManager, MenuManager menuManager, Trail trail) {
		this.equippableManager = equippableManager;
		this.menuManager = menuManager;
		this.trail = trail;
	}
	
	@Override
	public boolean onClick(Player player, Click click) {
		equippableManager.unequip(player, trail, true);
		MenuTrail menu = new MenuTrail(menuManager, equippableManager, player);
		menu.open(player);
		return true;
	}
}
