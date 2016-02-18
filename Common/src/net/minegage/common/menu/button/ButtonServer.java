package net.minegage.common.menu.button;


import net.minegage.common.misc.Click;
import net.minegage.common.server.ServerManager;
import org.bukkit.entity.Player;


public class ButtonServer
		extends Button {
		
	private String serverName;
	private ServerManager serverManager;
	
	public ButtonServer(ServerManager serverManager, String serverName) {
		this.serverManager = serverManager;
		this.serverName = serverName;
	}
	
	@Override
	public boolean onClick(Player player, Click click) {
		serverManager.connect(player, serverName);
		return true;
	}
	
}
