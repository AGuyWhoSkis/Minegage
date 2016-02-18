package net.minegage.common.util;


import java.util.Random;


public class Rand {
	
	private static Random random = new Random();
	
	public static boolean rBool() {
		return random.nextBoolean();
	}
	
	public static double r() {
		return random.nextDouble();
	}
	
	public static double r(double upBound) {
		return rDouble(upBound);
	}
	
	public static byte rByte(byte upBound) {
		return (byte) random.nextInt(upBound);
	}
	
	public static byte rByte(byte min, byte upBound) {
		return (byte) ( rInt(upBound - min) + min );
	}
	
	public static short rShort(short upBound) {
		return (short) random.nextInt(upBound);
	}
	
	public static short rShort(short min, short upBound) {
		return (short) ( rInt(upBound - min) + min );
	}
	
	public static int rInt(int upBound) {
		return random.nextInt(upBound);
	}
	
	public static int rInt(int min, int upBound) {
		return rInt(upBound - min) + min;
	}
	
	public static float rFloat(float upBound) {
		return random.nextFloat() * upBound;
	}
	
	public static float rFloat(float min, float upBound) {
		return rFloat(upBound - min) + min;
	}
	
	public static double rDouble(double upBound) {
		return random.nextDouble() * upBound;
	}
	
	public static double rDouble(double min, double upBound) {
		return rDouble(upBound - min) + min;
	}
	
	public static long rLong(long upBound) {
		return (long) rDouble(upBound);
	}
	
	public static long rLong(long min, long upBound) {
		return rLong(upBound - min) + min;
	}
	
	public static boolean chance(double percentChance) {
		return rDouble(1) < ( percentChance / 100 );
	}

	public static double gauss(double mean, double deviation) {
		return random.nextGaussian() * deviation + mean;
	}

	public static int gaussInt(int mean, int deviation) {
		return (int) Math.round(gauss(mean, deviation));
	}
}
