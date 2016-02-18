package net.minegage.core.move;

import net.minegage.common.C;
import net.minegage.common.misc.Note;
import net.minegage.common.module.PluginModule;
import net.minegage.common.move.MoveManager;
import net.minegage.common.ticker.TickEvent;
import net.minegage.common.ticker.Ticker;
import net.minegage.common.util.UtilSound;
import net.minegage.common.util.UtilTime;
import net.minegage.core.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class AFKManager
		extends PluginModule {

	private MoveManager moveManager;
	private final int warnTime = 10;
	public int kickTime = 120;

	public AFKManager(MoveManager moveManager, int kickTime) {
		super("AFK Manager", moveManager);
		this.moveManager = moveManager;
		this.kickTime = kickTime;
	}

	@EventHandler
	public void tick(TickEvent event) {
		if (event.is(Ticker.Tick.SEC_1)) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				Rank rank = Rank.get(player);
				if (!rank.includes(Rank.MODERATOR)) {
					double afkTime = UtilTime
							.toSeconds(UtilTime.timePassedSince(this.moveManager.getMoveToken(player).lastMoved));

					double timeUntilKick = this.kickTime - afkTime;
					if ((timeUntilKick < 10.0D) && (timeUntilKick > 0.0D)) {
						int note = (int) Math.round(timeUntilKick) + 14;

						C.pMain(player, "AFK",
						        C.cGreen + C.cBold + "You will be removed for inactivity in " + C.cYellow + C.cBold +

						        Math.round(timeUntilKick) + "s");
						UtilSound.playLocal(player, Sound.NOTE_BASS, 1.0F, Note.fromClicks(note));
					} else if (afkTime >= this.kickTime) {
						String message = C.cGreen + C.cBold + "You were moved because you were inactive for too long";
						player.sendMessage(message);
						player.kickPlayer(message);
					}
				}
			}
		} else if (event.is(Ticker.Tick.MIN_5)) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				Rank rank = Rank.get(player);
				if ((rank.includes(Rank.MODERATOR)) &&
				    (UtilTime.hasPassedSince(this.moveManager.getMoveToken(player).lastMouseMoved, 300000L))) {
					UtilSound.playLocal(player, Sound.SUCCESSFUL_HIT, 1.0F, 0.675F);
					C.pMain(player, "AFK", "GET BACK TO WORK FOOL!!! STAFF AREN'T SUPPOSED TO BE AFK!! -skis");
				}
			}
		}
	}
}
