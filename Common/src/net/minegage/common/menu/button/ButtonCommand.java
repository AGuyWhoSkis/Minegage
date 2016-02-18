package net.minegage.common.menu.button;


import net.minegage.common.misc.Click;
import org.bukkit.entity.Player;


public class ButtonCommand
		extends Button {
		
	private String command;
	
	public ButtonCommand(String command) {
		this.command = command;
	}
	
	@Override
	public boolean onClick(Player player, Click click) {
		player.chat("/" + command);
		return true;
	}
	
}
