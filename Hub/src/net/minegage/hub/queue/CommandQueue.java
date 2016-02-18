package net.minegage.hub.queue;


import net.md_5.bungee.api.ChatColor;
import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilJava;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandQueue
		extends RankedCommand {
	
	private String PREFIX = "&8[&dQueue&8] ";
	private String CHAT = "&7";
	
	private Queue queue;
	
	public CommandQueue(Queue mgq) {
		super(Rank.DEFAULT, "queue", "q");
		this.queue = mgq;
		PREFIX = ChatColor.translateAlternateColorCodes('&', PREFIX);
		CHAT = ChatColor.translateAlternateColorCodes('&', CHAT);
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		
		if (args.size() == 0) {
			
			message(player, "/queue help");
			rawMessage(player, "Note: /q works too!");
			rawMessage(player, "/queue <game>: " + ChatColor.WHITE + "Adds you to the specified queue");
			rawMessage(player, "/queue list: " + ChatColor.WHITE + "Lists all queueable games");
			rawMessage(player, "/queue remove: " + ChatColor.WHITE + "Removes you from the queue");
			
		} else {
			String sub = UtilJava.joinList(args, " ").toLowerCase();
			QueueManager queueManager = queue.getQueueManager();
			
			if (sub.equals("remove")) {
				
				synchronized (queue.getUpdateQueue().getGameQueueLock()) {
					
					GameQueue gameQueue = queueManager.getGameQueue(player);
					
					if (gameQueue == null) {
						message(player, ChatColor.GRAY + "You are not in a queue!");
						return;
					}
					
					gameQueue.remove(player);
					
					message(player, "You have been removed from the queue.");
				}
				
			} else if (sub.equals("reload")) {
				synchronized (queue.getUpdateQueue().getGameQueueLock()) {
					if (player.hasPermission("queue.admin.reload")) {
						queue.reload();
						message(player, "Config reloaded.");
					} else {
						message(player, "You don't have permission to do that");
					}
				}
			} else {
				
				// Attempted queue add
				synchronized (queue.getUpdateQueue().getGameQueueLock()) {
					GameQueue gameQueue = queue.getQueueManager().getGameQueue(sub);
					if (gameQueue == null) {
						message(player, "That game doesn't exist!");
						return;
					}
					
					if (gameQueue.getQueued().contains(player.getUniqueId())) {
						message(player, "You are already in that queue.");
						return;
					}
					
					String name = gameQueue.getNames().get(0);
					
					gameQueue.add(player);
					player.sendMessage("");
					message(player, "You are now in the " + C.sOut + name + CHAT + " queue.");
					player.sendMessage(C.cWhite + "" + C.cBold
					                   + "You will join a lobby when enough players are available to play.");
				}
			}
		}
	}
	
	public void message(Player player, String message) {
		C.pMain(player, "Queue", message);
	}
	
	public void rawMessage(Player player, String message) {
		player.sendMessage(CHAT + message);
	}
	
}
