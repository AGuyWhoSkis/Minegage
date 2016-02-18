package net.minegage.core.command.misc;


import net.minegage.common.command.Flags;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.condition.VisibilityManager;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandVanish
		extends RankedCommand {
		
	private VisibilityManager visibilityManager;
	
	public CommandVanish(VisibilityManager visibilityManager) {
		super(Rank.MODERATOR, "vanish", "van");

		this.visibilityManager = visibilityManager;
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		boolean vanished = !visibilityManager.isVanished(player);
		visibilityManager.setVanished(player, vanished);
		
		String state = ( vanished ) ? "vanished" : "visible";
		C.pMain(player, "Vanish", "You are now " + C.fElem(state));
	}

}
