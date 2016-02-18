package net.minegage.common.timer;


import net.minegage.common.C;
import net.minegage.common.java.SafeMap;
import net.minegage.common.module.PluginModule;
import net.minegage.common.util.UtilMath;
import net.minegage.common.util.UtilTime;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class Timer
		extends PluginModule {
		
	public static Timer instance;
	
	private SafeMap<Player, SafeMap<String, TimerToken>> timers = new SafeMap<>();
	
	public Timer(JavaPlugin plugin) {
		super("Timer", plugin);
		Timer.instance = this;
	}
	
	public boolean use(Player player, String ability, long chargeTime, boolean inform) {
		return use(player, "Ability", ability, chargeTime, inform);
	}
	
	public boolean use(Player player, String title, String ability, long chargeTime, boolean inform) {
		SafeMap<String, TimerToken> abilities = timers.get(player);
		
		TimerToken charge = abilities.get(ability);
		if (charge == null) {
			charge = new TimerToken();
		}
		
		long passed = UtilTime.timePassedSince(charge.lastUsed);
		if (UtilTime.hasPassed(passed, chargeTime)) {
			charge.lastUsed = System.currentTimeMillis();
			abilities.put(ability, charge);
			
			return true;
		} else if (inform) {
			double secondsLeft = UtilTime.toSeconds(chargeTime - passed);
			double remaining = UtilMath.round(secondsLeft, 1);
			
			C.pMain(player, title, "You can't use " + C.fElem(ability) + " for " + C.sOut2 + remaining + "s");
		}
		
		return false;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onQuit(PlayerQuitEvent event) {
		timers.remove(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(PlayerJoinEvent event) {
		timers.put(event.getPlayer(), new SafeMap<String, TimerToken>());
	}
	
}
