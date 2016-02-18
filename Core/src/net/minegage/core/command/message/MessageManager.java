package net.minegage.core.command.message;


import net.minegage.common.java.SafeMap;
import net.minegage.common.misc.Note;
import net.minegage.common.module.PluginModule;
import net.minegage.common.util.UtilSound;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import net.minegage.core.rank.RankManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class MessageManager
		extends PluginModule {

	private SafeMap<UUID, UUID> replyTo = new SafeMap<>();
	private Set<Player> spying = new HashSet<>();

	public MessageManager(JavaPlugin plugin) {
		super("Message Manager", plugin);

		addCommand(new CommandMessage(this));
		addCommand(new CommandReply(this));
		addCommand(new CommandSpy(this));
	}

	@EventHandler
	public void setSpy(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (!RankManager.instance.getRank(player)
				.includes(Rank.MODERATOR)) {
			spying.remove(player);
		} else {
			spying.add(player);
		}
	}

	@EventHandler
	public void clearReference(PlayerQuitEvent event) {
		UUID uid = event.getPlayer()
				.getUniqueId();
		replyTo.remove(uid);

		spying.remove(event.getPlayer());
	}

	public void message(Player from, Player to, String message) {
		message = C.cYellow + message;

		String fromName = from.getName();
		String toName   = to.getName();

		C.pMain(from, "You > " + toName, message);
		C.pMain(to, fromName + " > You", message);

		UtilSound.playLocal(to, Sound.NOTE_PIANO, 1F, Note.O3_D);

		String spyMessage = fromName + " > " + toName + ": " + C.cReset + message;

		Rank fromRank = Rank.get(from);
		Rank toRank = Rank.get(to);
		for (Player player : spying) {
			Rank spyRank = Rank.get(player);

			// Can't spy on higher ranks
			if (spyRank.includes(fromRank) && spyRank.includes(toRank)) {
				if (player != null && !player.equals(from) && !player.equals(to)) {
					C.pMain(player, "Spy", spyMessage);
				}
			}
		}

		replyTo.put(from.getUniqueId(), to.getUniqueId());
	}

	public void reply(Player player, String message) {
		UUID uid = replyTo.get(player.getUniqueId());

		if (uid == null) {
			C.pMain(player, "Msg", "You don't have anyone to reply to");
			return;
		}

		Player other = getServer().getPlayer(uid);

		//TODO: inter-server messaging

		if (other == null) {
			C.pMain(player, "Msg", "That player is not online");
			return;
		}

		message(player, other, message);
	}

	public void toggleSpy(Player player, boolean notify) {
		String state = null;

		if (spying.contains(player)) {
			spying.remove(player);
			state = "disabled";
		} else {
			spying.add(player);
			state = "enabled";
		}

		if (notify) {
			C.pMain(player, "Spy", "Spy mode " + C.sOut + state);
		}
	}

}
