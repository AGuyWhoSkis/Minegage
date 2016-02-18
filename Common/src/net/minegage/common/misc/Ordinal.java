package net.minegage.common.misc;


public class Ordinal {
	
	public static String shortForm(int num) {
		int leastSig = num % 10;
		
		String end = null;
		switch (leastSig) {
		case 1:
			end = "st";
			break;
		case 2:
			end = "nd";
			break;
		case 3:
			end = "rd";
			break;
		default:
			end = "th";
			break;
		}
		
		return num + end;
	}
	
}
