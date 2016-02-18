package net.minegage.core.equippable.trail;


import net.minegage.core.equippable.EquippableManager;
import net.minegage.core.rank.Rank;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;


public class TrailEffect
		extends Trail {
	
	protected Effect effect;
	protected int id;
	protected int data;
	protected float speed;
	protected int numParticles;
	protected int radius;
	
	protected float offsetX;
	protected float offsetY;
	protected float offsetZ;
	
	protected Vector positionOffset;
	
	public TrailEffect(EquippableManager manager, String name, ItemStack item, int displaySlot, Rank rank, Effect effect, int id,
			int data, int particleCount, Vector effectOffset, float speed, Vector positionOffset, int radius) {
		super(manager, name, item, displaySlot, rank);
		
		this.effect = effect;
		this.id = id;
		this.data = data;
		
		this.offsetX = (float) effectOffset.getX();
		this.offsetY = (float) effectOffset.getY();
		this.offsetZ = (float) effectOffset.getZ();
		
		this.speed = speed;
		this.numParticles = particleCount;
		this.radius = radius;
		
		this.positionOffset = positionOffset;
	}
	
	@Override
	public void play(Player player, Location location) {
		for (Player other : player.getWorld().getPlayers()) {
			if (other.canSee(player)) {
				other.spigot().playEffect(location, effect, id, data, offsetX, offsetY, offsetZ, speed, numParticles, radius);
			}
		}
	}
	
	// TODO: Clean up
	@Override
	public Location getLocation(Player player) {
		Location playerLocation = player.getLocation();
		Location ret = new Location(playerLocation.getWorld(), playerLocation.getX() + positionOffset.getX(),
				playerLocation.getY() + positionOffset.getY(), playerLocation.getZ() + positionOffset.getZ(), 0F, 0F);
		
		return ret;
	}
	
}
