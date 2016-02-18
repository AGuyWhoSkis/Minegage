package net.minegage.common.command.commands;


import net.minegage.common.C;
import net.minegage.common.command.Flags;
import net.minegage.common.command.type.PlayerCommand;
import net.minegage.common.datafile.DataFile;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilPos;
import net.minegage.common.util.UtilString;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class CommandData
		extends PlayerCommand {
		
	public CommandData() {
		super("mapdata");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {

		String fileName = "mapdata.dat";
		DataFile file = new DataFile(new File(player.getWorld()
				.getWorldFolder(), fileName));
				
		try {
			file.loadFile();
		} catch (IOException ex) {
			C.pErr(ex, player, "Unable to load file");
			return;
		}
		
		String action = "write";
		String desc = args.get(0);
		
		if (desc.startsWith("delete:") && desc.length() > "delete:".length()) {
			action = "delete";
			desc = UtilString.removeFirst(desc, "delete:");
		}
		
		String data = null;
		
		if (args.size() < 2) {
			if (action.equals("write")) {
				C.pMain(player, "Data", "Please specify the data to be written");
				return;
			}
		} else {
			data = args.get(1);
		}
		
		String string = null;
		
		if (data != null) {
			if (data.length() <= 4 && data.toLowerCase()
					.endsWith("loc")) {
				char c = data.charAt(0);
				Location loc = player.getLocation();
				
				if (c == 'h') {
					UtilPos.roundClosestHalf(loc);
				} else if (c == 'w') {
					UtilPos.roundClosestWhole(loc);
				} else if (Character.isDigit(c)) {
					int decimals = Character.getNumericValue(c);
					UtilPos.round(loc, decimals);
				} else if (data.length() == 4) {
					C.pMain(player, "Data", "Invalid loc type '" + c + "'");
					return;
				}
				
				string = UtilPos.serializeLocation(loc);
			} else if (data.length() <= 4 && data.toLowerCase()
					.endsWith("pos")) {
				char c = data.charAt(0);
				Vector loc = player.getLocation()
						.toVector();
						
				if (c == 'h') {
					UtilPos.roundClosestHalf(loc);
				} else if (c == 'w') {
					UtilPos.roundClosestWhole(loc);
				} else if (Character.isDigit(c)) {
					int decimals = Character.getNumericValue(c);
					UtilPos.round(loc, decimals);
				} else if (data.length() == 4) {
					C.pMain(player, "Data", "Invalid pos type '" + c + "'; valid types are h (round half), w (round whole), or 1-9 (decimal places)");
					return;
				}
				
				string = UtilPos.serializeVector(loc);
			} else if (data.equalsIgnoreCase("hand")) {
				ItemStack hand = player.getItemInHand();
				if (hand == null) {
					C.pMain(player, "Data", "Can't add null item");
					return;
				}

				MaterialData handData = hand.getData();
				string = handData.getItemTypeId() + ":" + handData.getData();
			}


			else {
				string = UtilJava.joinList(args, " ", 2);
			}
		}
		
		try {
			if (action.equals("write")) {
				file.append(desc, string);
				file.saveFile();
				
				C.pMain(player, "Data", "Appended " + C.fElem(string) + " to data " + C.fElem(desc));
			} else if (action.equals("delete")) {
				if (file.read(desc) == null) {
					C.pMain(player, "Data", "Data " + C.fElem(desc) + " doesn't exist");
					return;
				}
				
				file.remove(desc, string);
				file.saveFile();
				
				C.pMain(player, "Data", "Removed " + C.fElem(string) + " from data " + C.fElem(desc));
			} else {
				throw new Error("Invalid action \"" + action + "\"");
			}
			
		} catch (IOException ex) {
			C.pErr(ex, player, "Unable to save changes made to file");
			return;
		}
		
		
	}
	
}
