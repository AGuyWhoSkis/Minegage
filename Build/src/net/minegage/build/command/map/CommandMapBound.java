package net.minegage.build.command.map;


import net.minecraft.server.v1_8_R3.EnumDirection;
import net.minegage.common.command.Flags;
import net.minegage.common.datafile.DataFile;
import net.minegage.common.util.UtilPlayer;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class CommandMapBound
		extends RankedCommand {
		
	public CommandMapBound() {
		super(Rank.BUILDER, "bound", "boundary");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		
		
		DataFile file = new DataFile(new File(player.getWorld()
				.getWorldFolder(), DataFile.FILE_NAME));
				
		try {
			file.loadFile();
		} catch (IOException ex) {
			C.pErr(ex, player, "Unable to load map data file");
			return;
		}
		
		EnumDirection direction = UtilPlayer.getNmsPlayer(player)
				.getDirection();
				
		Location loc = player.getLocation();
		
		int value = 0;
		String bound = null;
		switch (direction) {
		case NORTH:
			bound = "maxz";
			value = loc.getBlockZ();
			break;
		case EAST:
			bound = "minx";
			value = loc.getBlockX();
			break;
		case SOUTH:
			bound = "minz";
			value = loc.getBlockZ();
			break;
		case WEST:
			bound = "maxx";
			value = loc.getBlockX();
			break;
		default:
			break;
		}
		
		String boundName;
		
		if (loc.getPitch() > 70) {
			bound = "maxy";
			boundName = "top";
			value = loc.getBlockY();
		} else if (loc.getPitch() < -70) {
			bound = "miny";
			boundName = "bottom";
			value = loc.getBlockY();
		} else {
			boundName = direction.name();
		}
		
		
		file.delete(bound);
		file.write(bound, value + "");
		
		try {
			file.saveFile();
			C.pMain(player, "Map", "Set " + C.fElem(boundName.toLowerCase()) + " boundary to " + C.fElem2(value + ""));
		} catch (IOException ex) {
			C.pErr(ex, player, "Unable to save boundary");
		}
		
		
	}
	
}
