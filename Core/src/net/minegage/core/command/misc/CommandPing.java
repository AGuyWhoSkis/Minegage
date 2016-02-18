package net.minegage.core.command.misc;

import net.minegage.common.C;
import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilPlayer;
import net.minegage.core.command.RankedCommand;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandPing
		extends RankedCommand {


	public CommandPing() {
		super(Rank.DEFAULT, "ping", "lag", "latency");
	}

	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {

		String ping = UtilPlayer.getNmsPlayer(player).ping + "ms";

		C.pMain(player, "System", "Your ping is " + C.fElem(UtilPlayer.getNmsPlayer(player).ping + "ms"));
	}
}
