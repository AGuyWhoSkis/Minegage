package net.minegage.recording;

import net.minegage.common.command.Flags;
import net.minegage.common.command.type.PlayerCommand;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandRecording
		extends PlayerCommand {

	private RecordingMain recordingMain;

	public CommandRecording(RecordingMain recordingMain) {
		super("Recording", "rec");

		this.recordingMain = recordingMain;

		setUsage("/recording");
		setDescription("Toggles your recordingMain status");
	}

	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		recordingMain.toggleRecording(player);
	}
}
