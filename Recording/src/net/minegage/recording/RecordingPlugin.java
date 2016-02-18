package net.minegage.recording;

import net.minegage.common.CommonPlugin;
import net.minegage.common.ticker.Ticker;

public class RecordingPlugin
		extends CommonPlugin {

	private Ticker ticker;
	private RecordingMain recordingMain;

	@Override
	public void onEnable() {
		super.onEnable();

		this.ticker = new Ticker(this);
		this.recordingMain = new RecordingMain(this);
	}


}
