package net.minegage.SOTF;

import net.minegage.common.module.PluginModule;

public class SOTF
		extends PluginModule {

	private SOTFPlugin plugin;



	public SOTF(SOTFPlugin plugin) {
		super("SOTF", plugin);

		this.plugin = plugin;
	}


}
