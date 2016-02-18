package net.minegage.core.command.misc;


import net.minegage.common.command.Flags;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilString;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandColour
		extends RankedCommand {
		
	private static final char SQUARE = '\u2B1B';
	private static final String SQUARES;
	
	static {
		String squaresTemp = "";
		for (int i = 0; i < 15; i++) {
			squaresTemp += SQUARE;
		}
		
		SQUARES = squaresTemp;
	}
	
	public CommandColour() {
		super(Rank.BUILDER, "colour", "colours", "color", "colors");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		String type = "colour";
		
		if (args.size() > 0) {
			type = args.get(0)
					.toLowerCase();
		}
		
		if (type.startsWith("colour") || type.startsWith("color")) {
			for (String str : C.bow()) {
				
				// Convert to chatcolour so it can be printed
				for (ChatColor colour : ChatColor.values()) {
					if (colour.toString()
							.equals(str)) {
						print(player, colour);
					}
				}
			}
		} else if (type.equals("all")) {
			for (ChatColor colour : ChatColor.values()) {
				print(player, colour);
			}
		} else if (type.equals("format")) {
			for (ChatColor colour : ChatColor.values()) {
				if (colour.isFormat()) {
					print(player, colour);
				}
			}
		} else {
			player.sendMessage(C.translate(UtilJava.joinList(args, " ")));
		}
	}
	
	private void print(Player player, ChatColor colour) {
		String name = UtilString.format(colour.name());
		
		char character = colour.getChar();
		C.pGeneral(player, " " + Character.toUpperCase(character), colour + SQUARES + C.cReset + " (" + name + ")");
	}
	
}
