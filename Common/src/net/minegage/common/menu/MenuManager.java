package net.minegage.common.menu;


import net.minegage.common.module.PluginModule;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;


public class MenuManager
		extends PluginModule {
	
	public List<Menu> menus = new ArrayList<>();
	
	public MenuManager(JavaPlugin plugin) {
		super("Menu Manager", plugin);
	}
	
	@Override
	public void onDisable() {
		menus.clear();
	}
	
	public void open(Player player, String rawName) {
		Menu menu = getMenu(rawName);
		if (menu != null) {
			menu.open(player);
		}
	}
	
	public Menu getMenu(String rawName) {
		for (Menu menu : menus) {
			if (menu.getRawName().equals(rawName)) {
				return menu;
			}
		}
		return null;
	}
	
}
