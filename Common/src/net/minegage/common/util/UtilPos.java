package net.minegage.common.util;


import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;


/**
 * General utility methods for Vectors and Locations
 * 
 * NOTE: Vector and Location objects are never cloned. Any operations are performed on the passed
 * objects themselves. The original object is always returned.
 */
public class UtilPos {
	
	public float getPitch(Vector vector) {
		double x = vector.getX();
		double z = vector.getZ();
		
		if (( x == 0.0D ) && ( z == 0.0D )) {
			return ( vector.getY() > 0.0D ? -90 : 90 );
		}
		
		double x2 = x * x;
		double z2 = z * z;
		double xz = Math.sqrt(x2 + z2);
		
		return ( (float) Math.toDegrees(Math.atan(-vector.getY() / xz)) );
	}
	
	public float getYaw(Vector vector) {
		double x = vector.getX();
		double z = vector.getZ();
		
		double theta = Math.atan2(-x, z);
		return ( (float) Math.toDegrees(( theta + 6.283185307179586D ) % 6.283185307179586D) );
	}
	
	public static String LABEL_COLON = ": ";
	public static String LABEL_EQUALS = " = ";
	public static String LABEL_COMMA = ", ";
	
	public static String format(Vector vector, String format) {
		return String.format(format, vector.getX(), vector.getY(), vector.getZ());
	}
	
	public static String getPosFormat(String delim) {
		return "x" + delim + "%s " + "y" + delim + "%s " + "z" + delim + "%s";
	}
	
	public static String format(Vector vector) {
		return format(vector, getPosFormat(LABEL_COLON));
	}
	
	public static String formatEquals(Vector vector) {
		return format(vector, getPosFormat(LABEL_EQUALS));
	}
	
	public static String formatCoord(Vector vector) {
		return "(" + format(vector, getPosFormat(LABEL_COLON)) + ")";
	}
	
	public static String format(Location location, String format) {
		return format(location.toVector(), format);
	}
	
	public static String format(Location location) {
		return format(location.toVector());
	}
	
	public static String formatEquals(Location location) {
		return formatEquals(location.toVector());
	}
	
	public static String formatCoord(Location location) {
		return formatCoord(location.toVector());
	}
	
	public static Vector fromBlockFace(BlockFace face) {
		return new Vector(face.getModX(), face.getModY(), face.getModZ());
	}
	
	public static Location setPosition(Location location, Vector vector) {
		location.setX(vector.getX());
		location.setY(vector.getY());
		location.setZ(vector.getZ());
		return location;
	}
	
	public static Vector round(Vector vector, int xDecimals, int yDecimals, int zDecimals) {
		vector.setX(UtilMath.round(vector.getX(), xDecimals));
		vector.setY(UtilMath.round(vector.getY(), yDecimals));
		vector.setZ(UtilMath.round(vector.getZ(), zDecimals));
		return vector;
	}
	
	public static Vector round(Vector vector, int horizDecimals, int vertDecimals) {
		return round(vector, horizDecimals, vertDecimals, horizDecimals);
	}
	
	public static Vector round(Vector vector, int decimals) {
		round(vector, decimals, decimals, decimals);
		return vector;
	}
	
	public static Location round(Location location, int xDecimals, int yDecimals, int zDecimals, int yawDecimals, int pitchDecimals) {
		roundDir(location, yawDecimals, pitchDecimals);
		round(location, xDecimals, yDecimals, zDecimals);
		return location;
	}
	
	public static Location round(Location location, int xDecimals, int yDecimals, int zDecimals) {
		location.setX(UtilMath.round(location.getX(), xDecimals));
		location.setY(UtilMath.round(location.getY(), yDecimals));
		location.setZ(UtilMath.round(location.getZ(), zDecimals));
		return location;
	}
	
	public static Location roundDir(Location location, int yawDecimals, int pitchDecimals) {
		location.setYaw((float) UtilMath.round(location.getYaw(), yawDecimals));
		location.setPitch((float) UtilMath.round(location.getPitch(), pitchDecimals));
		return location;
	}
	
	public static Location round(Location location, int coordDecimals, int dirDecimals) {
		round(location, coordDecimals, coordDecimals, coordDecimals, dirDecimals, dirDecimals);
		return location;
	}
	
	public static Location round(Location location, int coordDecimals) {
		round(location, coordDecimals, coordDecimals, coordDecimals);
		return location;
	}
	
	public static Vector roundClosestWhole(Vector vector) {
		vector.setX(Math.round(vector.getX()));
		vector.setZ(Math.round(vector.getZ()));
		return vector;
	}
	
	public static Vector roundClosestHalf(Vector vector) {
		vector.setX(UtilMath.roundClosestHalf(vector.getX()));
		vector.setZ(UtilMath.roundClosestHalf(vector.getZ()));
		return vector;
	}
	
	public static Location roundClosestWhole(Location location) {
		UtilPos.setPosition(location, roundClosestWhole(location.toVector()));
		return location;
	}
	
	public static Location roundClosestHalf(Location location) {
		UtilPos.setPosition(location, UtilPos.roundClosestHalf(location.toVector()));
		return location;
	}
	
	public static Vector createRand(double xAmp, double yAmp, double zAmp) {
		double x = Rand.rDouble(-xAmp, xAmp);
		double y = Rand.rDouble(-yAmp, yAmp);
		double z = Rand.rDouble(-zAmp, zAmp);
		
		return new Vector(x, y, z);
	}
	
	public static Vector createRand(double horizAmp, double vertAmp) {
		return createRand(horizAmp, vertAmp, horizAmp);
	}
	
	public static Vector createRand(double amp) {
		return createRand(amp, amp, amp);
	}
	
	/* Vector and Location serializing and deserializing */
	public static String serializeVector(Vector vector) {
		return serializeVector(vector, ",");
	}
	
	public static String serializeVector(Vector vector, String delim) {
		return String.format("%s" + delim + "%s" + delim + "%s", vector.getX(), vector.getY(), vector.getZ());
	}
	
	public static Vector deserializeVector(String serialized) {
		return deserializeVector(serialized, ",");
	}
	
	public static Vector deserializeVector(String serialized, String delim) {
		String[] split = serialized.split(delim);
		if (split.length < 3) {
			throw new IllegalArgumentException("Serialized vector \"" + serialized + "\" must contain at least 3 variables");
		}
		
		try {
			double x = Double.parseDouble(split[0]);
			double y = Double.parseDouble(split[1]);
			double z = Double.parseDouble(split[2]);
			
			return new Vector(x, y, z);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Serialized vector is not formatted correctly; must be x,y,z format");
		}
	}
	
	public static String serializeLocation(Location location) {
		return serializeLocation(location, ",");
	}
	
	public static String serializeLocation(Location location, String delim) {
		return String.format("%s" + delim + "%s" + delim + "%s" + delim + "%s" + delim + "%s", location.getX(), location.getY(), location.getZ(),
				location.getYaw(), location.getPitch());
	}
	
	public static Location deserializeLocation(String serialized, World world) {
		return deserializeLocation(serialized, world, ",");
	}
	
	public static Location deserializeLocation(String serialized, World world, String delim) {
		String[] split = serialized.split(delim);
		
		if (split.length < 5) {
			throw new IllegalArgumentException("Serialized location \"" + serialized + "\" must contain at least 5 variables");
		}
		
		try {
			double x = Double.parseDouble(split[0]);
			double y = Double.parseDouble(split[1]);
			double z = Double.parseDouble(split[2]);
			float yaw = Float.parseFloat(split[3]);
			float pitch = Float.parseFloat(split[4]);
			
			return new Location(world, x, y, z, yaw, pitch);
		} catch (NumberFormatException ex) {
			throw new IllegalArgumentException("Serialized location is not formatted correctly; it must be x,y,z,yaw,pitch format");
		}
	}
	
	public static String serializePosition(Location location, String delim) {
		return serializeVector(location.toVector(), delim);
	}
	
	public static String serializePosition(Location location) {
		return serializePosition(location, ",");
	}
	
	public static Location deserializePosition(String serialized, World world, String delim) {
		Vector position = deserializeVector(serialized, delim);
		return position.toLocation(world);
	}
	
	public static Location deserializePosition(String serialized, World world) {
		return deserializePosition(serialized, world, ",");
	}
	
}
