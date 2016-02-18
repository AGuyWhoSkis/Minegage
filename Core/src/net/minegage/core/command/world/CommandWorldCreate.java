package net.minegage.core.command.world;


import net.minegage.common.C;
import net.minegage.common.command.Flags;
import net.minegage.common.data.Data;
import net.minegage.common.data.DataEnum;
import net.minegage.common.util.UtilCommand;
import net.minegage.common.util.UtilJava;
import net.minegage.common.util.UtilWorld;
import net.minegage.core.command.RankedCommand;
import net.minegage.core.rank.Rank;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import java.util.List;


public class CommandWorldCreate
		extends RankedCommand {

	public CommandWorldCreate() {
		super(Rank.ADMIN, "create", "cr");

		addFlag("seed", Data.LONG);
		addFlag("gen", Data.STRING);
		addFlag("set", Data.STRING);
		addFlag("type", new DataEnum<WorldType>(WorldType.class));
		addFlag("env", new DataEnum<Environment>(Environment.class));
		addFlag("struc", Data.BOOLEAN);
	}

	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		if (args.size() == 0) {
			C.pMain(player, "World", "Please specify a world name");
			return;
		}

		String worldName = UtilJava.joinList(args, " ");
		if (UtilWorld.exists(worldName)) {
			C.pMain(player, "World", "World \"" + worldName + "\" already exists!");
			return;
		}

		WorldCreator creator = WorldCreator.name(worldName);

		if (flags.has("gen")) {
			String generatorName = flags.getString();
			creator.generator(generatorName);

			if (UtilCommand.failIfTrue(creator.generator() == null, player, "World",
			                           "Chunk generator \"" + generatorName + "\" not found")) {
				return;
			}
		}

		if (flags.has("set")) {
			String settings = flags.getString();
			creator.generatorSettings(settings);
		}

		if (flags.has("type")) {
			String type = flags.getString();

			WorldType worldType = UtilJava.parseEnum(WorldType.class, type);
			if (UtilCommand.failIfTrue(worldType == null, player, "World", "World type \"" + type + "\" not found")) {
				return;
			}

			creator.type(worldType);
		}

		if (flags.has("env")) {
			String env = flags.getString();
			String processed = env.toUpperCase()
					.replaceAll(" ", "_");

			try {
				Environment environment = Environment.valueOf(processed);
				creator.environment(environment);
			} catch (IllegalArgumentException ex) {
				C.pMain(player, "World", "Environment \"" + env + "\" not found");
				return;
			}
		}

		if (flags.has("struc")) {
			boolean struc = flags.getBoolean();
			creator.generateStructures(struc);
		}

		if (flags.has("seed")) {
			Long seed = flags.getLong();
			creator.seed(seed);
		}


		C.pMain(player, "World", "Generating world \"" + worldName + "\"...");

		try {
			UtilWorld.load(creator);
			C.pMain(player, "World", "Done");
		} catch (Exception ex) {
			C.pErr(ex, player, "Unable to create world");
		}

	}

}
