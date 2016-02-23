package net.minegage.SOTF;

import net.minegage.common.CommonPlugin;

public class SOTFPlugin
		extends CommonPlugin {

	private SOTF instance;

	@Override
	public void onEnable() {
		super.onEnable();

		this.instance = new SOTF(this);
	}
}
