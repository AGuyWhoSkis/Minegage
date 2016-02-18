package net.minegage.common.util;


import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;


public class UtilServer {
	
	private static <A, R> R playersCollect(Collector<? super Player, A, R> collector) {
		return Bukkit.getOnlinePlayers()
				.stream()
				.collect(collector);
	}
	
	public static Set<Player> playersSet() {
		return playersCollect(Collectors.toSet());
	}
	
	public static List<Player> playersList() {
		return playersCollect(Collectors.toList());
	}
	
	public static Collection<? extends Player> players() {
		return Bukkit.getOnlinePlayers();
	}
	
	public static int numPlayers() {
		return players().size();
	}
	
	public static void broadcast(String message) {
		getServer().broadcastMessage(message);
	}
	
	public static void stop() {
		getServer().shutdown();
	}
	
	public static int currentTick() {
		return MinecraftServer.currentTick;
	}
	
	public static Server getServer() {
		return Bukkit.getServer();
	}
}
