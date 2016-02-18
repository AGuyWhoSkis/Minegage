package net.minegage.common.ticker;


import net.minegage.common.module.PluginModule;
import net.minegage.common.util.UtilEvent;
import net.minegage.common.util.UtilServer;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;


public class Ticker
		extends PluginModule {
		
	public Ticker(JavaPlugin plugin) {
		super("Ticker", plugin);
	}
	
	@Override
	public void onEnable() {
		runSyncTimer(1L, 1L, new BukkitRunnable() {
			@Override
			public void run() {
				int currentTick = UtilServer.currentTick();

				for (Tick type : Tick.values()) {
					if (type.hasPassed(currentTick)) {
						type.last = currentTick;

						try {
							UtilEvent.call(new TickEvent(type));
						} catch (Throwable ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		});
	}
	
	public enum Tick {
		MIN_5(6000),
		MIN_1(1200),
		SEC_30(600),
		SEC_20(400),
		SEC_15(300),
		SEC_10(200),
		SEC_5(100),
		SEC_2(40),
		SEC_1(20),
		TICK_10(10),
		TICK_5(5),
		TICK_2(2),
		TICK_1(1);
		
		protected int interval;
		protected int last;
		
		Tick(int interval) {
			this.interval = interval;
			this.last = UtilServer.currentTick();
		}
		
		private boolean hasPassed(int currentTick) {
			return currentTick - last >= interval;
		}
	}
	
}
