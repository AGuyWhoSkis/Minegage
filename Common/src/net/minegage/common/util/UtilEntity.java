package net.minegage.common.util;


import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityAgeable;
import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.PathfinderGoal;
import net.minecraft.server.v1_8_R3.PathfinderGoalSelector;
import net.minegage.common.C;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class UtilEntity {

	private static final BlockFace[] SUPPORT_CHECK = new BlockFace[] {BlockFace.SELF, BlockFace.NORTH_EAST,
	                                                                  BlockFace.SOUTH_EAST,
	                                                                  BlockFace.NORTH_WEST, BlockFace.SOUTH_WEST};

	public static Set<Block> getSupportingBlocks(LivingEntity entity) {
		Set<Block> blocks = new HashSet<>();
		if (!isGrounded(entity)) {
			return blocks;
		}

		Location base = entity.getLocation()
				.add(0, -0.5, 0);

		float width = getNMSEntityLiving(entity).width;

		for (BlockFace face : SUPPORT_CHECK) {
			Vector add = UtilPos.fromBlockFace(face)
					.multiply(width / 2);

			Block block = base.clone()
					.add(add)
					.getBlock();

			if (block.getType()
					.isSolid()) {
				blocks.add(block);
			}
		}

		return blocks;
	}


	/**
	 * Removes the specified portal, as well as any passengers. Ejects players.
	 */
	public static void kill(Entity entity) {
		if (entity.getPassenger() != null) {
			kill(entity.getPassenger());
		}

		entity.eject();

		if (entity.getType() != EntityType.PLAYER) {
			entity.remove();
		}
	}

	public static Block getLookBlock(LivingEntity entity, int range) {
		BlockIterator blockIt = new BlockIterator(entity, range);

		while (blockIt.hasNext()) {
			Block next = blockIt.next();
			if (next.getType()
					.isSolid()) {
				return next;
			}
		}

		return null;
	}

	public static boolean isGrounded(LivingEntity entity) {
		return ((CraftEntity) entity).getHandle().onGround;
	}

	public static void clearPotionEffects(LivingEntity entity) {
		for (PotionEffect potionEffect : new ArrayList<>(entity.getActivePotionEffects())) {
			entity.removePotionEffect(potionEffect.getType());
		}
	}

	public static Entity getEntity(World world, UUID uid) {
		for (Entity entity : world.getEntities()) {
			if (entity.getUniqueId()
					.equals(uid)) {
				return entity;
			}
		}
		return null;
	}


	public static PathfinderGoalSelector getGoalSelector(LivingEntity entity) {
		return getNMSEntityInsentient(entity).goalSelector;
	}

	public static PathfinderGoalSelector getTargetSelector(LivingEntity entity) {
		return getNMSEntityInsentient(entity).targetSelector;
	}

	public static void addGoalSelector(LivingEntity entity, PathfinderGoal goal) {
		getGoalSelector(entity).a(goal);
	}

	public static void addTargetSelector(LivingEntity entity, PathfinderGoal goal) {
		getTargetSelector(entity).a(goal);
	}

	public static void clearPathfinderGoals(LivingEntity entity) {
		clearGoalSelectors(entity);
		clearTargetSelectors(entity);
	}

	private static Field selectorB;
	private static Field selectorC;

	public static void clearGoalSelectors(LivingEntity entity) {
		if (selectorB == null) {
			selectorB = UtilReflect.getField(PathfinderGoalSelector.class, "b");
			selectorC = UtilReflect.getField(PathfinderGoalSelector.class, "c");
		}

		PathfinderGoalSelector goalSelector = getGoalSelector(entity);

		List<?> goalB = (List<?>) UtilReflect.get(selectorB, goalSelector);
		List<?> goalC = (List<?>) UtilReflect.get(selectorC, goalSelector);

		goalB.clear();
		goalC.clear();
	}

	public static void clearTargetSelectors(LivingEntity entity) {
		if (selectorB == null) {
			selectorB = UtilReflect.getField(PathfinderGoalSelector.class, "b");
			selectorC = UtilReflect.getField(PathfinderGoalSelector.class, "c");
		}

		PathfinderGoalSelector targetSelector = getTargetSelector(entity);

		List<?> targetB = (List<?>) UtilReflect.get(selectorB, targetSelector);
		List<?> targetC = (List<?>) UtilReflect.get(selectorC, targetSelector);

		targetB.clear();
		targetC.clear();
	}

	public static void clearTarget(LivingEntity entity) {
		Location loc = entity.getLocation();
		UtilEntity.setTarget(entity, loc.getX(), loc.getY(), loc.getZ(), getSpeed(entity));
	}

	public static void setTarget(LivingEntity entity, double x, double y, double z, double speed) {
		EntityInsentient insentient = getNMSEntityInsentient(entity);
		insentient.getNavigation()
				.a(x, y, z, speed);
	}

	public static void lockAge(LivingEntity entity) {
		if (entity instanceof Ageable) {
			((Ageable) entity).setAgeLock(true);
		}
	}

	public static void setPersistent(LivingEntity entity, boolean persistent) {
		getNMSEntityInsentient(entity).persistent = persistent;
	}

	public static void setSilent(Entity entity, boolean silent) {
		getNMSEntity(entity).b(silent);
	}

	public static boolean isSilent(Entity entity) {
		return getNMSEntity(entity).R();
	}

	public static double getSpeed(LivingEntity entity) {
		return getNMSEntityLiving(entity).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED)
				.getValue();
	}

	public static void setSpeed(LivingEntity entity, double speed) {
		getNMSEntityLiving(entity).getAttributeInstance(GenericAttributes.c)
				.setValue(speed);
	}

	public static net.minecraft.server.v1_8_R3.Entity getNMSEntity(Entity entity) {
		return ((CraftEntity) entity).getHandle();
	}

	public static EntityLiving getNMSEntityLiving(LivingEntity entity) {
		return (EntityLiving) getNMSEntity(entity);
	}

	public static EntityCreature getNMSEntityCreature(LivingEntity entity) {
		return (EntityCreature) getNMSEntityLiving(entity);
	}

	public static EntityInsentient getNMSEntityInsentient(LivingEntity entity) {
		return (EntityInsentient) getNMSEntityLiving(entity);
	}

	public static EntityAgeable getNMSEntityAgeable(LivingEntity entity) {
		return (EntityAgeable) getNMSEntityLiving(entity);
	}

	@Deprecated
	public static void removeAI(LivingEntity entity) {
		net.minecraft.server.v1_8_R3.Entity nmsEntity = getNMSEntity(entity);

		NBTTagCompound compound = new NBTTagCompound();
		nmsEntity.c(compound);
		compound.setByte("NoAI", (byte) 1);
		nmsEntity.f(compound);
	}

	public static void setAI(LivingEntity entity, boolean ai) {
		net.minecraft.server.v1_8_R3.Entity nmsEntity = getNMSEntity(entity);

		NBTTagCompound compound = new NBTTagCompound();
		nmsEntity.c(compound);

		byte noAI = (ai) ? (byte) 0 : (byte) 1;

		compound.setByte("NoAI", noAI);
		nmsEntity.f(compound);
	}

	public static NBTTagCompound getNBTCopy(Entity entity) {
		NBTTagCompound nbt = new NBTTagCompound();
		getNMSEntity(entity).c(nbt);
		return nbt;
	}

	public static void setNBT(Entity entity, NBTTagCompound nbt) {
		getNMSEntity(entity).f(nbt);
	}

	/**
	 * Resets the health and the max health of the portal
	 */
	public static void resetHealth(LivingEntity entity) {
		entity.resetMaxHealth();
		entity.setHealth(entity.getMaxHealth());
	}

	public static void damage(LivingEntity entity, double amount, DamageSource source) {
		getNMSEntityLiving(entity).damageEntity(source, (float) amount);
	}

	/**
	 * Removes all passengers of the specified portal from top to bottom. Players are ejected instead of removed.
	 */
	public static void removePassengers(Entity entity) {
		removePassenger(entity.getPassenger());
	}

	private static void removePassenger(Entity passenger) {
		if (passenger == null) {
			return;
		}

		// Remove top passenger first
		removePassenger(passenger.getPassenger());

		if (passenger.getType() == EntityType.PLAYER) {
			passenger.eject();
		} else {
			passenger.remove();
		}
	}

	public static void addTag(LivingEntity entity, String... tags) {
		for (String tag : tags) {
			if (entity.getPassenger() == null) {

				// Add 1 slime to lower the first armor stand
				Slime spacer = createTagSlime(entity.getLocation());
				entity.setPassenger(spacer);

				ArmorStand stand = createArmorStand(entity.getLocation(), tag);
				spacer.setPassenger(stand);
			} else {
				// Presume there is more than 1 nametag

				Entity topPassenger = entity.getPassenger();
				while (topPassenger.getPassenger() != null) {
					topPassenger = topPassenger.getPassenger();
				}

				// Add 3 slimes to lower the next armor stand
				Entity vehicle = topPassenger;
				for (int i = 0; i < 3; i++) {
					Slime slime = createTagSlime(entity.getLocation());
					vehicle.setPassenger(slime);
					vehicle = slime;
				}

				ArmorStand stand = createArmorStand(entity.getLocation(), tag);
				vehicle.setPassenger(stand);
			}
		}
	}

	private static Slime createTagSlime(Location location) {
		Slime slime = (Slime) location.getWorld().spawnEntity(location, EntityType.SLIME);

		slime.setSize(-1);
		UtilEntity.setPersistent(slime, true);
		slime.setRemoveWhenFarAway(false);

		slime.addPotionEffect(PotionEffectType.INVISIBILITY.createEffect(Integer.MAX_VALUE, 0));

		return slime;
	}

	private static ArmorStand createArmorStand(Location location, String name) {
		ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);

		stand.setVisible(false);
		stand.setRemoveWhenFarAway(false);

		stand.setSmall(true);
		stand.setCustomNameVisible(true);
		stand.setCustomName(C.translate(name));

		return stand;
	}

	public static List<ArmorStand> getTags(LivingEntity entity) {
		List<ArmorStand> stands = new ArrayList<>();

		Entity search = entity;
		while (search.getPassenger() != null) {
			if (search.getPassenger().getType() == EntityType.ARMOR_STAND) {
				stands.add((ArmorStand) search.getPassenger());
			}

			search = search.getPassenger();
		}

		return stands;
	}

	/**
	 * Updates or adds a nametag to the portal
	 */
	public static void setNametag(LivingEntity entity, String name) {
		if (name == null) {
			if (entity.getPassenger() != null) {
				entity.getPassenger().remove();
			}

			return;
		}


		ArmorStand nametag;

		Entity passenger = entity.getPassenger();
		if (passenger != null && passenger.getType() == EntityType.ARMOR_STAND) {
			nametag = (ArmorStand) passenger;
		} else {
			if (passenger != null) {
				passenger.remove();
			}

			nametag = (ArmorStand) entity.getLocation()
					.getWorld()
					.spawnEntity(entity.getLocation(), EntityType.ARMOR_STAND);
		}

		nametag.setVisible(false); // We only want the nametag to show
		nametag.setSmall(true);
		nametag.setCustomName(name);
		nametag.setCustomNameVisible(true);
		nametag.setGravity(false);

		if (entity.getPassenger() == null) {
			entity.setPassenger(nametag);
		}
	}

}
