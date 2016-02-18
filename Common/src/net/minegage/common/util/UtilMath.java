package net.minegage.common.util;


import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;


public class UtilMath {
	
	public static Vector flat(Vector v) {
		return v.setY(0);
	}
	
	public static void flat(Vector a, Vector b) {
		flat(a);
		flat(b);
	}
	
	public static double offset(Vector a, Vector b) {
		return a.subtract(b)
				.length();
	}
	
	public static double offset2D(Vector a, Vector b) {
		flat(a, b);
		return offset(a, b);
	}
	
	public static double offsetSq(Vector a, Vector b) {
		return a.subtract(b)
				.lengthSquared();
	}
	
	public static double offsetSq2D(Vector a, Vector b) {
		flat(a, b);
		return offsetSq(a, b);
	}
	
	/**
	 * Ignores world, skips validation checks
	 */
	public static double offset(Location a, Location b) {
		return offset(a.toVector(), b.toVector());
	}
	
	public static double offset2D(Location a, Location b) {
		return offset2D(a.toVector(), b.toVector());
	}
	
	public static double offsetSq(Location a, Location b) {
		return offsetSq(a.toVector(), b.toVector());
	}
	
	public static double offsetSq2D(Location a, Location b) {
		return offsetSq2D(a.toVector(), b.toVector());
	}
	
	public static double offset(Entity a, Entity b) {
		return offset(a.getLocation(), b.getLocation());
	}
	
	public static double offset2D(Entity a, Entity b) {
		return offset2D(a.getLocation(), b.getLocation());
	}
	
	public static double offsetSq(Entity a, Entity b) {
		return offsetSq(a.getLocation(), b.getLocation());
	}
	
	public static double offsetSq2D(Entity a, Entity b) {
		return offsetSq2D(a.getLocation(), b.getLocation());
	}
	
	public static double round(double d, int precision) {
		if (!Double.isFinite(d)) {
			return d;
		}
		
		int a = (int) Math.pow(10, precision);
		d *= a;
		d = Math.round(d);
		d /= a;
		return d;
	}
	
	public static double roundClosestHalf(double d) {
		return Math.floor(d) + 0.5;
	}
	
	@Deprecated
	public static long roundClosestWhole(double d) {
		return Math.round(d);
	}
	
}
