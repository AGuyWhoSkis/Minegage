package net.minegage.recording;

import net.minegage.common.C;
import net.minegage.common.board.Board;
import net.minegage.common.board.CommonBoardManager;
import net.minegage.common.module.PluginModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;

public class RecordingMain
		extends PluginModule {

	private RecordingPlugin plugin;
	private CommonBoardManager boardManager;
	private Set<Player> recording = new HashSet<>();

	private final char SYMBOL = '\u25CF';

	private final String PREFIX = C.cRed + C.cBold + "REC " + C.cReset;

	private boolean prefixToggle = true;

	public RecordingMain(RecordingPlugin plugin) {
		super("RecordingMain", plugin);

		this.plugin = plugin;
		this.boardManager = new CommonBoardManager(plugin);

		addCommand(new CommandRecording(this));
	}

	public void toggleRecording(Player player) {
		boolean isRecording = !recording.contains(player);

		String message;
		if (isRecording) {
			recording.add(player);
			message = "You are now marked as recording!";
		} else {
			recording.remove(player);
			message = "You are no longer marked as recording!";
		}

		String prefix = null;
		if (isRecording) {
			prefix = PREFIX;
		}

		boardManager.setPrefix(player, prefix);
		player.sendMessage(C.cBold + C.cGray + message);
	}

	@EventHandler
	public void givePrefixes(PlayerJoinEvent event) {
		Board board = new Board();

		for (Player isRecording : recording) {
			board.setPrefix(isRecording, PREFIX);
		}

		boardManager.setBoard(event.getPlayer(), board);
	}

	@EventHandler
	public void clearReference(PlayerQuitEvent event) {
		recording.remove(event.getPlayer());
	}




}
