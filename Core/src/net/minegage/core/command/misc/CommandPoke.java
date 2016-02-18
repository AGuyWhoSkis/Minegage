package net.minegage.core.command.misc;

import net.minegage.common.command.Flags;
import net.minegage.common.move.MoveManager;
import net.minegage.common.move.MoveToken;
import net.minegage.common.timer.Timer;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilSound;
import net.minegage.common.util.UtilTime;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandPoke
		extends RankedCommand {

	private MoveManager moveManager;

	public CommandPoke(MoveManager moveManager) {
		super(Rank.DEFAULT, "poke");

		this.moveManager = moveManager;
	}

	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		if (args.size() < 1) {
			C.pMain(player, "Poke", "Please specify a player to poke");
			return;
		}

		String targetName = UtilJava.joinList(args, " ");
		Player target     = Bukkit.getPlayer(targetName);

		if (target == null) {
			C.pMain(player, "Poke", "That player is not online!");
			return;
		}

		if (target.equals(player)) {
			C.pMain(player, "Poke", "Stop poking yourself!");
			return;
		}

		MoveToken token = moveManager.getMoveToken(target);

		if (!UtilTime.hasPassedSince(token.lastMoved, 20000L)) {
			C.pMain(player, "Poke", C.fElem(target.getName()) + " isn't afk!");
			return;
		}

		if (!Timer.instance.use(target, "Poked", 10000L, false)) {
			C.pMain(player, "Poke", target.getName() + " has been poked recently; please wait!");
			return;
		}

		UtilSound.playLocal(target, Sound.CHICKEN_EGG_POP, 1F, 0.5F);
		C.pMain(target, "Poke", C.fElem(player.getName()) + " poked you");
		C.pMain(player, "Poke", "You poked " + C.fElem(target.getName()));
	}
}
