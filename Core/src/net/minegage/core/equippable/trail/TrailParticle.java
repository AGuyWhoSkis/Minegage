package net.minegage.core.equippable.trail;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers.Particle;
import net.minegage.core.equippable.EquippableManager;
import net.minegage.core.rank.Rank;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class TrailParticle  
		extends Trail {
	
	protected Particle particleType;
	protected boolean longDistance;
	protected float particleOffsetX;
	protected float particleOffsetY;
	protected float particleOffsetZ;
	protected float spawnOffsetX;
	protected float spawnOffsetY;
	protected float spawnOffsetZ;
	protected float particleData;
	protected int particleCount;
	protected int[] data;
	
	protected TrailParticle(EquippableManager manager, String name, ItemStack item, int displaySlot, Rank rank, Particle particleType, boolean longDistance,
			Vector particleOffset, Vector spawnOffset, float particleData, int particleCount, int[] data) {
		super(manager, name, item, displaySlot, rank);
		
		this.particleType = particleType;
		this.longDistance = longDistance;
		this.particleOffsetX = (float) particleOffset.getX();
		this.particleOffsetY = (float) particleOffset.getY();
		this.particleOffsetZ = (float) particleOffset.getZ();
		this.spawnOffsetX = (float) spawnOffset.getX();
		this.spawnOffsetY = (float) spawnOffset.getY() + 0.05F;
		this.spawnOffsetZ = (float) spawnOffset.getZ();
		this.particleData = particleData;
		this.particleCount = particleCount;
		this.data = data;
	}
	
	public void play(Player player, Location location) {
		WrapperPlayServerWorldParticles particle = new WrapperPlayServerWorldParticles();
		
		particle.setParticleType(particleType);
		particle.setLongDistance(longDistance);
		particle.setOffsetX(particleOffsetX);
		particle.setOffsetY(particleOffsetY);
		particle.setOffsetZ(particleOffsetZ);
		particle.setParticleData(particleData);
		particle.setNumberOfParticles(particleCount);
		particle.setData(data);
		
		float x = (float) location.getX() + spawnOffsetX;
		float y = (float) location.getY() + spawnOffsetY;
		float z = (float) location.getZ() + spawnOffsetZ;
		
		particle.setX(x);
		particle.setY(y);
		particle.setZ(z);
		
		modify(particle);
		
		send(player, particle);
	}
	
	public Location getLocation(Player player) {
		return player.getLocation();
	}
	
	/**
	 * @param particle The particle to be modified
	 */
	protected void modify(WrapperPlayServerWorldParticles particle) {
		// Optional override
	}
	
	protected void send(Player player, WrapperPlayServerWorldParticles particle) {
		for (Player other : player.getWorld().getPlayers()) {
			if (other.canSee(player)) {
				particle.sendPacket(other);
			}
		}
	}
	
}
