package net.minegage.common.token;


import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;


public class LocToken
		extends WorldToken {
		
	public Vector locDirection;
	public Vector locPosition;
	
	public LocToken(Location loc) {
		super(loc.getWorld());
		this.locPosition = loc.toVector();
		this.locDirection = loc.getDirection();
	}
	
	public Location getLocation() {
		World bukkitWorld = getWorld();
		if (bukkitWorld != null) {
			Location location = locPosition.toLocation(bukkitWorld);
			location.setDirection(locDirection);
			return location;
		}
		return null;
	}
	
}
