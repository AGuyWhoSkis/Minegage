package net.minegage.core.command;


import net.minegage.common.C;
import net.minegage.common.module.PluginModule;
import net.minegage.common.timer.Timer;
import net.minegage.core.CorePlugin;
import net.minegage.core.command.message.MessageManager;
import net.minegage.core.command.misc.CommandColour;
import net.minegage.core.command.misc.CommandItem;
import net.minegage.core.command.misc.CommandPing;
import net.minegage.core.command.misc.CommandPoke;
import net.minegage.core.command.misc.CommandSnap;
import net.minegage.core.command.misc.CommandVanish;
import net.minegage.core.command.speed.CommandSpeed;
import net.minegage.core.command.world.CommandWorld;
import net.minegage.core.mob.command.CommandMob;
import net.minegage.core.rank.Rank;
import net.minegage.core.rank.RankManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.HashSet;
import java.util.Set;


public final class CoreCommandManager
		extends PluginModule {
		
	private Set<String> hidden = new HashSet<>();

	protected MessageManager messageManager;
	
	public CoreCommandManager(CorePlugin plugin) {
		super("Core Commands", plugin);

		messageManager = new MessageManager(plugin);

		addCommand(new CommandMob());
		addCommand(new CommandWorld());
		addCommand(new CommandSpeed());
		addCommand(new CommandSnap());
		addCommand(new CommandItem());
		addCommand(new CommandColour());
		addCommand(new CommandVanish(plugin.getVisibilityManager()));
		addCommand(new CommandPoke(plugin.getMoveManager()));
		addCommand(new CommandPing());
	}
	

	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		Player player = event.getPlayer();
		
		if (!RankManager.instance.hasPermission(player, Rank.ADMIN)) {
			if (!Timer.instance.use(player, null, "Send Command", 500L, false)) {
				C.pMain(player, "System", "You can't send commands that fast");
				event.setCancelled(true);
				return;
			}
		}
		// Command not found, allow Bukkit to finish command processing
	}
	
	
}
