package net.minegage.core.command.misc;


import net.minegage.common.command.Flags;
import net.minegage.core.command.RankedCommand;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandSound
		extends RankedCommand {
		
	public CommandSound() {
		super(Rank.BUILDER, "sound", "snd");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {

	}
	
}
