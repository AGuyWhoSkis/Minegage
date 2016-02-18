package net.minegage.core.mob.command;


import net.minegage.common.command.Flags;
import net.minegage.common.data.Data;
import net.minegage.common.data.DataEnum;
import net.minegage.common.util.UtilCommand;
import net.minegage.common.util.UtilEntity;
import net.minegage.common.util.UtilJava;
import net.minegage.core.command.RankedCommand;
import net.minegage.common.C;
import net.minegage.core.mob.MobType;
import net.minegage.core.rank.Rank;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CommandMobClear
		extends RankedCommand {
		
	public CommandMobClear() {
		super(Rank.ADMIN, "clear", "remove", "delete");
		addFlag("radius", Data.DOUBLE);
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		List<MobType> mobTypes = new ArrayList<>();
		if (args.size() < 1) {
			C.pMain(player, "Mob", "Please specify a mob type, or \"all\"");
			return;
		}
		
		String unparsedMob = UtilJava.joinList(args, " ");
		
		if (unparsedMob.equalsIgnoreCase("all")) {
			for (MobType mobType : MobType.values()) {
				mobTypes.add(mobType);
			}
			mobTypes.remove(MobType.PLAYER);
		} else {
			DataEnum<MobType> mobType = new DataEnum<>(MobType.class);
			if (UtilCommand
					.failedParse(mobType, unparsedMob, player, "Mob", "Mob type \"" + unparsedMob + "\" not found")) {
				return;
			}
			mobTypes.add(mobType.getData());
		}
		
		double radius = Double.MAX_VALUE;
		if (flags.has("radius")) {
			radius = Math.pow(flags.getDouble(), 2);
		}
		
		Set<Entity> deleting = new HashSet<>();
		Location playerLoc = player.getLocation();
		for (Entity entity : player.getWorld()
				.getEntities()) {
			for (MobType mobType : mobTypes) {
				if (mobType.equals(entity) && playerLoc.distanceSquared(entity.getLocation()) < radius) {
					deleting.add(entity);
				}
			}
		}
		
		for (Entity entity : deleting) {
			UtilEntity.removePassengers(entity);
			entity.remove();
		}
		
		C.pMain(player, "Mob", "Removed " + deleting.size() + " mobs");
	}
	
}
