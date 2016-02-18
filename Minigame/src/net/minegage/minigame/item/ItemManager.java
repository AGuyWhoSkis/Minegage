package net.minegage.minigame.item;


import net.minegage.common.java.SafeMap;
import net.minegage.common.misc.Click;
import net.minegage.common.misc.Click.ClickButton;
import net.minegage.common.module.PluginModule;
import net.minegage.common.server.ServerManager;
import net.minegage.common.timer.Timer;
import net.minegage.common.util.UtilItem;
import net.minegage.common.util.UtilPlayer;
import net.minegage.common.C;
import net.minegage.minigame.MinigameManager;
import net.minegage.minigame.game.Game;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemManager
		extends PluginModule {
		
	public static final ItemStack specItem = UtilItem.create(Material.EYE_OF_ENDER, C.cBold + "Spectate " + C.cGray + "(click to scroll)");
	public static final ItemStack lobbyItem = UtilItem.create(Material.ENDER_PEARL, C.cBold + "Back to Hub " + C.cReset
	                                                                                + "(click)");
			
	private SafeMap<Player, Integer> indexes = new SafeMap<>();
	private MinigameManager manager;
	
	public ItemManager(MinigameManager manager) {
		super("Item Manager", manager);
		this.manager = manager;
	}
	
	@EventHandler
	public void clearReference(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		indexes.remove(player);
	}
	
	@EventHandler
	public void onLobbyClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if (UtilPlayer.isHolding(player, lobbyItem)) {
			ServerManager.instance.connect(player, ServerManager.LOBBY);
		} else if (UtilPlayer.isHolding(player, specItem)) {
			if (Timer.instance.use(player, "Spectate", "Spectate Teleport", 250L, false)) {
				Player target = getNextSpectator(player, Click.from(event));
				
				if (target == null) {
					C.pMain(player, "Spectate", "There is nobody to spectate right now");
				} else {
					player.teleport(target);
					C.pMain(player, "Spectate", "Now spectating " + C.sOut + target.getName());
				}
			}
			
		} else {
			return;
		}
		
		event.setCancelled(true);
	}
	
	private Player getNextSpectator(Player requester, Click click) {
		int increment = click.getButton() == ClickButton.LEFT ? -1 : 1;
		
		Game game = manager.getGameManager()
				.getGame();
				
		List<Player> players = game.getPlayersNotSpectating();
		players.remove(requester);
		
		if (players.size() == 0) {
			return null;
		}
		
		int index = indexes.getOrDefault(requester, 0);
		index += increment;
		
		if (index < 0) {
			index = players.size() - 1;
		} else if (index >= players.size()) {
			index = 0;
		}
		
		indexes.put(requester, index);
		return players.get(index);
	}
	
}
