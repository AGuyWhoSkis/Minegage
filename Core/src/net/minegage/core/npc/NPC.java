package net.minegage.core.npc;


import net.minecraft.server.v1_8_R3.EntityCreature;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomLookaround;
import net.minegage.common.log.L;
import net.minegage.common.util.UtilEntity;
import net.minegage.core.mob.Mob;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.UUID;


public class NPC
		extends Mob {

	protected String name;
	protected double radius;

	protected boolean returning = false;
	protected int failedPostChecks = 0;
	protected double moveSpeed = 0.0;

	public NPC(UUID uid, Location location, String name, double radius) {
		super(uid, location);

		this.tags.add(name);
		this.radius = radius;
	}
	
	@Override
	public void load(LivingEntity entity) {
		super.load(entity);
		
		this.moveSpeed = UtilEntity.getSpeed(entity);

		if (!(UtilEntity.getNMSEntityLiving(entity) instanceof EntityCreature)) {
			L.warn("Unable to load NPC " + entity.getUniqueId() + "; unsupported entity type " + entity.getType().name());
			return;
		}

		EntityCreature creature = UtilEntity.getNMSEntityCreature(entity);
		UtilEntity.clearPathfinderGoals(entity);

		creature.goalSelector.a(0, new PathfinderGoalRandomStrollCustom(creature, moveSpeed, getPost(), radius));
		creature.goalSelector.a(1, new PathfinderGoalLookAtPlayer(creature, EntityHuman.class, 3.0F));
		creature.goalSelector.a(2, new PathfinderGoalRandomLookaround(creature));

		UtilEntity.setAI(entity, true);
		UtilEntity.setSilent(entity, false);

	}

	public boolean isInPost() {
		Vector currentPos = entity.getLocation()
				.toVector();
		return post.distanceSquared(currentPos) <= Math.pow(radius, 2D);
	}
	
	public boolean isReturning() {
		return returning;
	}
	
	public void setReturning(boolean returning) {
		this.returning = returning;
	}
	
	public void returnToPost() {
		UtilEntity.setTarget(entity, post.getX(), post.getY(), post.getZ(), moveSpeed);
		setReturning(true);
	}
	
	public double getRange() {
		return radius;
	}
	
	public int getFailedPostChecks() {
		return failedPostChecks;
	}
	
	public void setFailedPostChecks(int failedPostChecks) {
		this.failedPostChecks = failedPostChecks;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	public double getMoveSpeed() {
		return moveSpeed;
	}
	
	public void setMoveSpeed(double moveSpeed) {
		this.moveSpeed = moveSpeed;
	}
	
}
