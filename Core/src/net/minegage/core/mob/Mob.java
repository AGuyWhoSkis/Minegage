package net.minegage.core.mob;


import net.minegage.common.util.UtilEntity;
import net.minegage.core.event.EventClickEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * A "wrapper" for a LivingEntity. Contains various flags for changing basic behaviour of the mob.
 * Must be loaded by a {@link MobManager}.
 */
public class Mob {
	
	protected LivingEntity entity = null;
	protected UUID uid;
	
	protected String world;
	protected Vector post;
	protected float yaw;
	protected float pitch;

	protected boolean armourInvulnerable = true;
	protected boolean flammable = false;
	protected boolean invulnerable = true;
	
	protected boolean targettable = false;
	protected boolean targetsOthers = false;
	protected boolean harmsOthers = false;
	
	protected boolean returnOnFall = true;

	protected List<String> tags = new ArrayList<>();

	protected Mob(UUID uid, Location location) {
		this.uid = uid;
		
		this.world = location.getWorld()
				.getName();
		this.post = location.toVector();
		this.yaw = location.getYaw();
		this.pitch = location.getPitch();
	}

	public void teleportToPost() {
		entity.setFallDistance(0F);

		entity.teleport(getPostLocation());

		UtilEntity.getNMSEntityLiving(entity)
				.setPositionRotation(post.getX(), post.getY(), post.getZ(), yaw, pitch);
	}

	protected void load(LivingEntity entity) {
		this.entity = entity;

		entity.setRemoveWhenFarAway(false);
		UtilEntity.setPersistent(entity, true);
		teleportToPost();
	}

	public void unload() {
		this.entity = null;
		this.tags.clear();
	}
	
	public boolean isEntityLoaded() {
		return this.entity != null;
	}
	
	public boolean isPostLoaded() {
		return getPostLocation().getChunk()
				.isLoaded();
	}
	
	public UUID getUid() {
		return uid;
	}
	
	public void setUid(UUID uid) {
		this.uid = uid;
	}
	
	public boolean isArmourInvulnerable() {
		return armourInvulnerable;
	}
	
	public boolean isInvulnerable() {
		return invulnerable;
	}
	
	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}
	
	public boolean isFlammable() {
		return flammable;
	}
	
	public void setFlammable(boolean flammable) {
		this.flammable = flammable;
	}
	
	public boolean isTargettable() {
		return targettable;
	}
	
	public void setTargettable(boolean targettable) {
		this.targettable = targettable;
	}
	
	public boolean targetsOthers() {
		return targetsOthers;
	}
	
	public void setTargetsOthers(boolean targetsOthers) {
		this.targetsOthers = targetsOthers;
	}
	
	public boolean harmsOthers() {
		return harmsOthers;
	}
	
	public void setHarmsOthers(boolean harmsOthers) {
		this.harmsOthers = harmsOthers;
	}
	
	public boolean isReturnOnFall() {
		return returnOnFall;
	}
	
	public void setReturnOnFall(boolean returnOnFall) {
		this.returnOnFall = returnOnFall;
	}

	public LivingEntity getEntity() {
		return entity;
	}

	public void setEntity(LivingEntity entity) {
		this.entity = entity;
	}
	
	public Vector getPost() {
		return post;
	}
	
	public Location getPostLocation() {
		Location ret = post.toLocation(Bukkit.getWorld(world));
		ret.setYaw(yaw);
		ret.setPitch(pitch);
		
		return ret;
	}
	
	/**
	 * @param event
	 *        The interact event fired when interacting with the mob
	 */
	public void onClick(EventClickEntity event) {
		// Optional override
	}
	
}
