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
import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;

import java.util.ArrayList;
import java.util.List;


public class CommandMobSpawn
		extends RankedCommand {
		
	public CommandMobSpawn() {
		super(Rank.ADMIN, "spawn", "create", "s", "c");
		
		addFlag("age", Data.INTEGER);
		addFlag("ai", Data.NULL);
		addFlag("move", Data.NULL);
		addFlag("per", Data.NULL);
		addFlag("silent", Data.NULL);
		addFlag("count", Data.INTEGER);
		addFlag("name", Data.STRING);
		addFlag("namevis", Data.BOOLEAN, "true", "false");
		addFlag("prof", new DataEnum<Profession>(Profession.class));
		addFlag("ocelottype", new DataEnum<Ocelot.Type>(Ocelot.Type.class));
	}
	
	@Override
	public void onCommand(Player player, List<String> args, String raw, Flags flags) {
		String unparsedMob = UtilJava.joinList(args, " ");
		
		DataEnum<MobType> mobData = new DataEnum<>(MobType.class);
		if (UtilCommand.failedParse(mobData, unparsedMob, player, "Mob", "Invalid mob type \"" + unparsedMob + "\"")) {
			return;
		}

		int count = 1;
		if (flags.has("count")) {
			count = flags.getInt();
		}
		
		MobType mob = mobData.getData();
		List<LivingEntity> entities = new ArrayList<>();
		
		for (int i = 0; i < count; i++) {
			entities.add(mob.spawn(player.getLocation()));
		}
		
		for (LivingEntity entity : entities) {
			entity.getEquipment()
					.setArmorContents(player.getEquipment()
							.getArmorContents());
			entity.getEquipment()
					.setItemInHand(player.getItemInHand());
		}
		
		if (flags.has("per")) {
			for (LivingEntity entity : entities) {
				UtilEntity.setPersistent(entity, true);
			}
		}
		
		if (flags.has("ai")) {
			for (LivingEntity entity : entities) {
				UtilEntity.setAI(entity, false);
			}
		}
		
		if (flags.has("move")) {
			for (LivingEntity entity : entities) {
				UtilEntity.clearPathfinderGoals(entity);
			}
		}
		
		if (flags.has("target")) {
			for (LivingEntity entity : entities) {
				UtilEntity.clearTargetSelectors(entity);
			}
		}
		
		if (flags.has("silent")) {
			for (LivingEntity entity : entities) {
				UtilEntity.setSilent(entity, true);
			}
		}
		
		if (flags.has("name")) {
			for (LivingEntity entity : entities) {
				entity.setCustomName(flags.getString());
			}
		}
		
		if (flags.has("namevis")) {
			for (LivingEntity entity : entities) {
				entity.setCustomNameVisible(flags.getBoolean());
			}
		}
		
		if (flags.has("age")) {
			if (UtilCommand.notInstance(entities.get(0), Ageable.class, player, "Mob", "Flag -a requires an ageable mob")) {
				return;
			}
			
			int age = flags.getInt();

			for (LivingEntity entity : entities) {
				Ageable ageable = (Ageable) entity;
				ageable.setAge(age);
			}
		}
		
		if (flags.has("prof")) {
			if (UtilCommand.notInstance(entities.get(0), Villager.class, player, "Mob", "Flag -prof requires a villager")) {
				return;
			}

			String unparsedProf = flags.getString();
			
			DataEnum<Profession> profData = new DataEnum<Profession>(Profession.class);
			if (UtilCommand.failedParse(profData, unparsedProf, player, "Mob", "profession \"" + unparsedProf + "\" not found")) {
				return;
			}
			
			Profession prof = profData.getData();
			for (LivingEntity entity : entities) {
				Villager villager = (Villager) entity;
				villager.setProfession(prof);
			}
		}

		if (flags.has("ocelottype")) {
			if (UtilCommand.notInstance(entities.get(0), Ocelot.class, player, "Mob", "Flag -ocelottype requires an ocelot")) {
				return;
			}

			String unparsedType = flags.getString();

			DataEnum<Ocelot.Type> typeData = new DataEnum<>(Ocelot.Type.class);
			if (UtilCommand.failedParse(typeData, unparsedType, player, "Mob", "ocelot type " + C.fElem(unparsedType) + " not found")) {
				return;
			}

			Ocelot.Type type = typeData.getData();
			for (LivingEntity entity : entities) {
				Ocelot ocelot = (Ocelot) entity;
				ocelot.setCatType(type);
			}


		}
		
		C.pMain(player, "Mob", "Spawned " + count + " " + mob + "(s)");
	}
}
