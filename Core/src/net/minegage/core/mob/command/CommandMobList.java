package net.minegage.core.mob.command;


import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilJava;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.mob.MobType;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;


public class CommandMobList
		extends RankedCommand {
		
	public CommandMobList() {
		super(Rank.ADMIN, "list", "l");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		
		String message = "";
		Iterator<MobType> mobsIt = UtilJava.arrayIterator(MobType.values());
		while (mobsIt.hasNext()) {
			MobType type = mobsIt.next();
			message = message + type.toString();
			if (mobsIt.hasNext()) {
				message = message + ", ";
			}
		}
		
		C.pRaw(player, "");
		C.pRaw(player, message);
		C.pRaw(player, "");
	}
	
}
