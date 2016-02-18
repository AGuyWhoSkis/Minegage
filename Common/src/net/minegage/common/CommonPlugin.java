package net.minegage.common;

import net.minegage.common.command.BukkitCommandManager;
import net.minegage.common.log.L;
import net.minegage.common.timer.Timer;
import org.bukkit.plugin.java.JavaPlugin;

public class CommonPlugin
		extends JavaPlugin {

	private Timer timer;
	private BukkitCommandManager commandManager;

	@Override
	public void onEnable() {
		super.onEnable();

		L.initialize(this);

		this.timer = new Timer(this);
		this.commandManager = new BukkitCommandManager(this);
	}

	public Timer getTimer() {
		return timer;
	}

	public BukkitCommandManager getCommandManager() {
		return commandManager;
	}
}
