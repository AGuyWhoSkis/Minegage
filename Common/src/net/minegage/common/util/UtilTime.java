package net.minegage.common.util;


public class UtilTime {
	
	public static boolean hasPassed(int timePassed, int minPassed) {
		return timePassed >= minPassed;
	}
	
	public static boolean hasPassed(int startTime, int endTime, int minPassed) {
		int passed = endTime - startTime;
		return hasPassed(passed, minPassed);
	}
	
	public static boolean hasPassedSince(int startTime, int minPassed) {
		return hasPassed(startTime, UtilServer.currentTick(), minPassed);
	}
	
	public static int timePassed(int startTime, int endTime) {
		return ( endTime - startTime );
	}
	
	public static int timePassedSince(int startTime) {
		return timePassed(startTime, UtilServer.currentTick());
	}
	
	public static int timeLeft(int ticks) {
		return ticks - UtilServer.currentTick();
	}
	
	public static boolean hasPassed(int ticks) {
		return timeLeft(ticks) <= 0;
	}
	
	/* Millisecond time checks */
	
	public static boolean hasPassed(long timePassed, long minPassed) {
		return timePassed >= minPassed;
	}
	
	public static boolean hasPassed(long startTime, long endTime, long minPassed) {
		long passed = endTime - startTime;
		return hasPassed(passed, minPassed);
	}
	
	public static boolean hasPassedSince(long startTime, long minPassed) {
		return hasPassed(startTime, System.currentTimeMillis(), minPassed);
	}
	
	public static long timePassed(long startTime, long endTime) {
		return ( endTime - startTime );
	}
	
	public static long timePassedSince(long startTime) {
		return timePassed(startTime, System.currentTimeMillis());
	}
	
	public static long timeLeft(long millis) {
		return millis - System.currentTimeMillis();
	}
	
	public static boolean hasPassed(long millis) {
		return timeLeft(millis) <= 0;
	}
	
	/*
	 * Time conversions
	 */
	
	public static double toSeconds(long millis) {
		return millis / 1000D;
	}
	
	public static double toSeconds(int ticks) {
		return ticks / 20D;
	}
	
	public static int toTicks(long millis) {
		return (int) ( millis / 50L );
	}
	
	public static int toTicks(double seconds) {
		return (int) ( seconds * 20D );
	}
	
	public static long toMillis(int ticks) {
		return ticks * 50L;
	}
	
	public static long toMillis(double seconds) {
		return (long) ( seconds * 1000L );
	}
	
	
}
