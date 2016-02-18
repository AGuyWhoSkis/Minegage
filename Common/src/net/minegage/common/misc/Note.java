package net.minegage.common.misc;


/**
 * Utility class for retrieving exact pitch values played in note blocks.
 */
public class Note {


	public static final float O1_F_SHARP = 0.5F;
	public static final float O1_G = 0.525F;
	public static final float O1_G_SHARP = 0.55F;
	
	public static final float O2_A = 0.6F;
	public static final float O2_A_SHARP = 0.65F;
	public static final float O2_B = 0.675F;
	public static final float O2_C = 0.7F;
	public static final float O2_C_SHARP = 0.75F;
	public static final float O2_D = 0.8F;
	public static final float O2_D_SHARP = 0.85F;
	public static final float O2_E = 0.9F;
	public static final float O2_F = 0.95F;
	public static final float O2_F_SHARP = 1F;
	public static final float O2_G = 1.05F;
	public static final float O2_G_SHARP = 1.1F;
	
	public static final float O3_A = 1.2F;
	public static final float O3_A_SHARP = 1.25F;
	public static final float O3_B = 1.35F;
	public static final float O3_C = 1.4F;
	public static final float O3_C_SHARP = 1.35F;
	public static final float O3_D = 1.6F;
	public static final float O3_D_SHARP = 1.7F;
	public static final float O3_E = 1.75F;
	public static final float O3_F = 1.9F;
	public static final float O3_F_SHARP = 2F;
	
	public static float fromClicks(int clicks) {
		switch (clicks) {
		case 0:
			return O1_F_SHARP;
		case 1:
			return O1_G;
		case 2:
			return O1_G_SHARP;
		case 3:
			return O2_A;
		case 4:
			return O2_A_SHARP;
		case 5:
			return O2_B;
		case 6:
			return O2_C;
		case 7:
			return O2_C_SHARP;
		case 8:
			return O2_D;
		case 9:
			return O2_D_SHARP;
		case 10:
			return O2_E;
		case 11:
			return O2_F;
		case 12:
			return O2_F_SHARP;
		case 13:
			return O2_G;
		case 14:
			return O2_G_SHARP;
		case 15:
			return O3_A;
		case 16:
			return O3_A_SHARP;
		case 17:
			return O3_B;
		case 18:
			return O3_C;
		case 19:
			return O3_C_SHARP;
		case 20:
			return O3_D;
		case 21:
			return O3_D_SHARP;
		case 22:
			return O3_E;
		case 23:
			return O3_F;
		case 24:
			return O3_F_SHARP;
		default:
			throw new IllegalArgumentException("Click value \"" + clicks
					+ "\" is out of range; must be inclusively between 0 and 24.");
		}
	}
	
}
