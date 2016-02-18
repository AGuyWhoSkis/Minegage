package net.minegage.build.command.map;


import net.minegage.common.command.Flags;
import net.minegage.common.datafile.DataFile;
import net.minegage.common.util.UtilJava;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class CommandMapName
		extends RankedCommand {
		
	public CommandMapName() {
		super(Rank.BUILDER, "name");
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
		
		
		if (args.size() < 1) {
			C.pMain(player, "Map", "Please specify a name");
			return;
		}
		
		String name = UtilJava.joinList(args, " ");
		
		file.delete("name");
		file.write("name", name);
		
		try {
			file.saveFile();
			C.pMain(player, "Map", "Set name to " + C.fElem(name));
		} catch (IOException ex) {
			C.pErr(ex, player, "Unable to save name");
		}
	}
	
}
