package net.minegage.core.command.misc;


import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilPos;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandSnap
		extends RankedCommand {
		
	public CommandSnap() {
		super(Rank.BUILDER, "snap", "sn");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		String type = "whole";
		
		if (args.size() > 0) {
			type = UtilJava.joinList(args, " ");
		}
		
		Location loc = player.getLocation();
		
		if (type.equals("whole") || type.equals("w")) {
			loc = UtilPos.roundClosestWhole(loc);
		} else if (type.equals("half") || type.equals("h")) {
			loc = UtilPos.roundClosestHalf(loc);
		} else {
			C.pMain(player, "Snap", "Unknown snap type \"" + type + "\"");
			return;
		}
		
		player.teleport(loc);
	}
	
}
