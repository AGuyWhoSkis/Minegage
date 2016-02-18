package net.minegage.build.command.map;


import net.minegage.common.command.Flags;
import net.minegage.common.server.ServerManager;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilZip;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import net.minegage.minigame.game.GameType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class CommandMapGet
		extends RankedCommand {
		
	public CommandMapGet() {
		super(Rank.ADMIN, "fetch", "get");
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		if (args.size() < 1) {
			C.pMain(player, "Map", "Please specify an asset type (world or map)");
			return;
		}
		
		String assetType = args.get(0);
		
		String world;
		File asset;
		if (assetType.equalsIgnoreCase("map")) {
			// map fetch map <game> <map>
			if (args.size() < 2) {
				C.pMain(player, "Map", "Please specify a gametype");
				return;
			}
			
			String typeString = args.get(1);
			GameType type = UtilJava.parseEnum(GameType.class, typeString);
			if (type == null) {
				C.pMain(player, "Rotation", "Invalid gametype \"" + typeString + "\"");
				return;
			}
			
			if (args.size() < 3) {
				C.pMain(player, "Map", "Please specify a map name");
				return;
			}
			
			world = UtilJava.joinList(args, " ", 2);
			
			asset = ServerManager.getMapZip(type.getName(), world);
		} else if (assetType.equalsIgnoreCase("world")) {
			
			if (args.size() < 2) {
				C.pMain(player, "Map", "Please specify a world name");
				return;
			}
			
			world = UtilJava.joinList(args, " ", 1);
			
			asset = ServerManager.getWorldZip(world);
		} else {
			C.pMain(player, "Map", "Invalid asset type " + C.fElem(assetType) + "; must be " + C.fElem("map") + " or " + C
					.fElem("world"));
			return;
		}
		
		if (!asset.exists()) {
			C.pMain(player, "Map", "Couldn't find the " + assetType.toLowerCase() + " " + C.fElem(world) + "!");
			return;
		}
		
		File worldDir = new File(Bukkit.getWorldContainer(), world);
		
		try {
			UtilZip.extract(asset, worldDir);
			C.pMain(player, "Rotation", "Map fetched!");
		} catch (IOException ex) {
			C.pErr(ex, player, "Unable to fetch map");
		}
	}
	
}
