package net.minegage.minigame.kit.attrib;


import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker.Tick;
import net.minegage.common.timer.Timer;
import net.minegage.common.util.Rand;
import net.minegage.common.util.UtilEntity;
import net.minegage.common.util.UtilSound;
import net.minegage.common.util.UtilTime;
import net.minegage.common.C;
import net.minegage.minigame.game.Game;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;


public class AttribDoubleJump
		extends Attrib {
		
	private long rechargeTime = 0L;
	
	public AttribDoubleJump(double rechargeSeconds) {
		super("Double Jump", C.sOut + "Double tap space " + C.sBody + "to " + C.sOut2 + "double jump");
		
		this.explainUse = false;
		this.rechargeTime = UtilTime.toMillis(rechargeSeconds);
	}
	
	@Override
	public void apply(Player player) {
		player.setAllowFlight(true);
	}
	
	@EventHandler
	public void onDoubleJump(PlayerToggleFlightEvent event) {
		if (!event.isFlying()) {
			return;
		}
		
		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		if (!appliesTo(player)) {
			return;
		}
		
		if (Timer.instance.use(player, "Ability", "Double Jump", rechargeTime, true)) {
			Vector velocity = player.getVelocity();
			velocity.add(player.getLocation()
					.getDirection()
					.multiply(0.75));
					
			velocity.setY(Math.min(velocity.getY(), 0.5));
			player.setVelocity(velocity);
			
			UtilSound.playPhysical(player.getLocation(), Sound.ZOMBIE_INFECT, 1F, Rand.rFloat(0.9F, 1.2F));
			
			event.setCancelled(true);
			player.setAllowFlight(false);
		}
	}
	
	@EventHandler
	public void groundCheck(TickEvent event) {
		if (event.isNot(Tick.TICK_1)) {
			return;
		}
		
		Game game = getGame();
		if (game == null || !game.isPlaying()) {
			return;
		}
		
		for (Player player : game.getPlayersIn()) {
			if (appliesTo(player)) {
				if (UtilEntity.isGrounded(player)) {
					player.setAllowFlight(true);
				}
			}
		}
		
		
	}
	
}
