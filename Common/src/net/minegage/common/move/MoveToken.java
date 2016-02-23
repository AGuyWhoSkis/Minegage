package net.minegage.common.move;


import org.bukkit.Location;


public class MoveToken {

	public long lastMoved = System.currentTimeMillis();
	public long lastMouseMoved = System.currentTimeMillis();
	public long lastPhysicalMoved = System.currentTimeMillis();

	public long lastSneaking = System.currentTimeMillis();
	public long lastStanding = System.currentTimeMillis();

	public Location lastLocation;
	
	public MoveToken(Location location) {
		this.lastLocation = location;
	}
	
}
