package net.minegage.common.util;


import com.comphenix.packetwrapper.WrapperPlayServerPlayerListHeaderFooter;
import com.comphenix.packetwrapper.WrapperPlayServerTitle;
import com.comphenix.protocol.wrappers.EnumWrappers.TitleAction;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minegage.common.C;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;


public class UtilUI {
	
	public static WrapperPlayServerTitle createTitle(TitleAction action) {
		WrapperPlayServerTitle packet = new WrapperPlayServerTitle();
		packet.setAction(action);
		return packet;
	}
	
	public static void sendTimes(Player player, int inTicks, int stayTicks, int outTicks) {
		WrapperPlayServerTitle packet = createTitle(TitleAction.TIMES);
		
		packet.setFadeIn(inTicks);
		packet.setStay(stayTicks);
		packet.setFadeOut(outTicks);
		
		packet.sendPacket(player);
	}
	
	public static void sendTitle(Player player, String text) {
		WrapperPlayServerTitle packet = createTitle(TitleAction.TITLE);
		packet.setTitle(WrappedChatComponent.fromText(text));
		packet.sendPacket(player);
	}
	
	public static void sendSubtitle(Player player, String text) {
		WrapperPlayServerTitle packet = createTitle(TitleAction.SUBTITLE);
		packet.setTitle(WrappedChatComponent.fromText(text));
		packet.sendPacket(player);
	}
	
	public static void sendTitle(Player player, String text, int inTicks, int stayTicks, int outTicks) {
		sendTimes(player, inTicks, stayTicks, outTicks);
		sendTitle(player, text);
	}
	
	public static void sendSubtitle(Player player, String text, int inTicks, int stayTicks, int outTicks) {
		sendTimes(player, inTicks, stayTicks, outTicks);
		sendSubtitle(player, text);
	}
	
	public static void sendTitles(Player player, String title, String subtitle) {
		sendTitle(player, title);
		sendSubtitle(player, subtitle);
	}
	
	public static void sendTitles(Player player, String title, String subtitle, int inTicks, int stayTicks, int outTicks) {
		sendTimes(player, inTicks, stayTicks, outTicks);
		sendTitle(player, title);
		sendSubtitle(player, subtitle);
	}
	
	
	public static void clearTitle(Player player) {
		WrapperPlayServerTitle packet = new WrapperPlayServerTitle();
		packet.setAction(TitleAction.CLEAR);
		packet.sendPacket(player);
	}
	
	public static void sendTabText(Player player, String header, String footer) {
		WrapperPlayServerPlayerListHeaderFooter packet = new WrapperPlayServerPlayerListHeaderFooter();
		
		WrappedChatComponent headerComponent = WrappedChatComponent.fromText(header);
		WrappedChatComponent footerComponent = WrappedChatComponent.fromText(footer);
		
		packet.setHeader(headerComponent);
		packet.setFooter(footerComponent);
		
		packet.sendPacket(player);
	}
	
	public static void sendActionBar(Player player, String message) {
		IChatBaseComponent textWrap = ChatSerializer.a("{\"text\":\"" + message + "\"}");
		PacketPlayOutChat barPacket = new PacketPlayOutChat(textWrap, (byte) 2);
		UtilPlayer.sendPacket(player, barPacket);
	}
	
	public static String getServerDisplay(String server) {
		return C.sThemeA + C.cBold + "Minegage " + C.sThemeB + C.cBold + server;
	}

	public static String getTimer(int seconds) {
		int totalHours = seconds / 3600;
		int totalMinutes = (seconds % 3600) / 60;
		int totalSeconds = seconds % 60;

		String display = totalSeconds + "s";
		if (totalMinutes > 0) {
			display = totalMinutes + "m " + display;
		}
		if (totalHours > 0) {
			display = totalHours + "h " + display;
		}

		return display;
	}
	
	public static Scoreboard getMainScoreboard() {
		return UtilUI.getBoardManager()
				.getMainScoreboard();
	}
	
	public static Scoreboard getNewScoreboard() {
		return UtilUI.getBoardManager()
				.getNewScoreboard();
	}
	
	public static ScoreboardManager getBoardManager() {
		return Bukkit.getScoreboardManager();
	}
	
}
