package net.minegage.core.equippable;


import net.minegage.common.module.PluginModule;
import net.minegage.core.CorePlugin;


public class EquipManager
		extends PluginModule {
		
	private EquippableManager equippableManager;
	private MenuEquip menuEquip;
	
	public EquipManager(CorePlugin plugin) {
		super("Equip Manager", plugin);
		
		this.equippableManager = new EquippableManager(plugin.getMoveManager());
		this.menuEquip = new MenuEquip(plugin.getMenuManager(), equippableManager);
	}

	public EquippableManager getEquippableManager() {
		return equippableManager;
	}
	
	public MenuEquip getMenuEquip() {
		return menuEquip;
	}
	
}
