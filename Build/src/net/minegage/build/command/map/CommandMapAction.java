package net.minegage.build.command.map;

import net.minegage.common.command.Flags;
import net.minegage.common.server.ServerManager;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilWorld;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.rank.Rank;
import net.minegage.minigame.game.GameType;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

public abstract class CommandMapAction
		extends RankedCommand {

	public CommandMapAction(String action, String... actions) {
		super(Rank.ADMIN, action, actions);
	}

	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		if (args.size() < 1) {
			C.pMain(player, "Map", "Please specify an asset type (world or map)");
			return;
		}

		String assetType = args.get(0);

		String world;
		File   asset;
		if (assetType.equalsIgnoreCase("map")) {
			if (args.size() < 2) {
				C.pMain(player, "Map", "Please specify a gametype");
				return;
			}

			String   typeString = args.get(1);
			GameType type       = UtilJava.parseEnum(GameType.class, typeString);
			if (type == null) {
				C.pMain(player, "Rotation", "Invalid gametype \"" + typeString + "\"");
				return;
			}

			world = player.getWorld()
					.getName();

			if (args.size() > 2) {
				world = UtilJava.joinList(args, " ", 2);
			}

			asset = ServerManager.getMapZip(type.getName(), world);
		} else if (assetType.equalsIgnoreCase("world")) {

			world = player.getWorld()
					.getName();
			if (args.size() > 1) {
				world = UtilJava.joinList(args, " ", 1);
			}

			asset = ServerManager.getWorldZip(world);
		} else {
			C.pMain(player, "Map", "Invalid asset type " + C.fElem(assetType) + "; must be " + C.fElem("map") + " or " +
			                       C.fElem("world"));
			return;
		}

		if (!UtilWorld.exists(world)) {
			C.pMain(player, "Map", "World " + C.fElem(world) + "doesn't exist!");
			return;
		}

		doAction(player, args, flags, world, asset);
	}

	protected abstract void doAction(Player player, List<String> args, Flags flags, String world, File asset);




}
