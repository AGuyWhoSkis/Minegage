package net.minegage.core.chat;


import net.minegage.common.java.SafeMap;
import net.minegage.common.module.PluginModule;
import net.minegage.common.timer.Timer;
import net.minegage.common.util.UtilMath;
import net.minegage.common.util.UtilNet;
import net.minegage.common.util.UtilTime;
import net.minegage.core.chat.command.CommandChat;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import net.minegage.core.rank.RankManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


public class ChatManager
		extends PluginModule {
		
	private final String URL = "http://www.purgomalum.com/service/plain?text=";
	private SafeMap<Player, Long> lastMessage = new SafeMap<>();
	
	private boolean silenced = false;
	private long silencedUntil = -1L;
	
	private boolean slow = false;
	
	private boolean filter = true;
	
	public ChatManager(JavaPlugin plugin) {
		super("Chat Manager", plugin);
		
		addCommand(new CommandChat(this));
	}
	
	
	@EventHandler
	public void purge(PlayerQuitEvent event) {
		lastMessage.remove(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void formatChat(AsyncPlayerChatEvent event) {
		event.setFormat(" %s " + C.cReset + "%s");
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void filterChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		Player player = event.getPlayer();
		if (!Timer.instance.use(player, "Chat", "Send Message", 350L, true)) {
			event.setCancelled(true);
			return;
		}
		
		Rank rank = RankManager.instance.getRank(player);
		
		if (silenced && !rank.includes(Rank.MODERATOR)) {
			if (silencedUntil == -1L) {
				C.pMain(player, "Chat", "Chat is silenced");
			} else {
				long timeLeft = UtilTime.timeLeft(silencedUntil);
				double secondsLeft = UtilTime.toSeconds(timeLeft);
				secondsLeft = UtilMath.round(secondsLeft, 1);
				C.pMain(player, "Chat", "Chat is silenced for " + C.sOut + secondsLeft + "s");
			}
			
			event.setCancelled(true);
			return;
		}
		
		if (slow && !rank.includes(Rank.MODERATOR)) {
			long lastTime = lastMessage.getOrDefault(player, 0L);
			
			long slowDelay;
			if (rank == Rank.DEFAULT) {
				slowDelay = 10000L;
			} else if (rank == Rank.ACE) {
				slowDelay = 5000L;
			} else if (rank == Rank.PRO) {
				slowDelay = 4000L;
			} else {
				slowDelay = 3000L;
			}
			
			long timeLeft = UtilTime.timeLeft(lastTime + slowDelay);
			
			if (timeLeft > 0) {
				double secondsLeft = UtilTime.toSeconds(timeLeft);
				secondsLeft = UtilMath.round(secondsLeft, 1);
				C.pMain(player, "Chat", "Chat is in slow mode. Please wait " + C.sOut + secondsLeft + "s");
				
				event.setCancelled(true);
				return;
			}
		}
		
		if (filter && !rank.includes(Rank.OWNER)) {
			String message = event.getMessage();

			try {
				String urlMessage = URLEncoder.encode(message, StandardCharsets.UTF_8.name());
				String filterUrl  = URL + urlMessage;

				String response = UtilNet.read(filterUrl, 300);
				if (response != null) {
					event.setMessage(response);
				}

			} catch (UnsupportedEncodingException ex) {
				// This should never happen
				ex.printStackTrace();
			}
		}
	}

	@EventHandler(priority =  EventPriority.HIGHEST)
	public void recordChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (event.getMessage() == null) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority =  EventPriority.MONITOR)
	public void monitorChat (AsyncPlayerChatEvent event) {
		if (event.isCancelled()) {
			return;
		}

		lastMessage.put(event.getPlayer(), System.currentTimeMillis());
	}
	
	public void setSilenced(boolean silenced) {
		this.silenced = silenced;
	}
	
	public void silence(long silencedMillis) {
		this.silencedUntil = System.currentTimeMillis() + silencedMillis;
	}
	
	public void setSlow(boolean slow) {
		this.slow = slow;
	}
	
	public boolean isSilenced() {
		return silenced;
	}
	
	public long getSilencedUntil() {
		return silencedUntil;
	}
	
	public boolean isSlow() {
		return slow;
	}
	
	public boolean isFilterEnabled() {
		return filter;
	}
	
	public void setFilterEnabled(boolean enabled) {
		this.filter = enabled;
	}
	
	
	
	
	
}
