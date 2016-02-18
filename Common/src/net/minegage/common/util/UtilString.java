package net.minegage.common.util;


import org.apache.commons.lang.WordUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * General String utilities, including regex-free alternatives to traditional string methods
 */
public class UtilString {
	
	/**
	 * Corrects case and replaces underscores with spaces
	 */
	public static String format(String string) {
		String copy = string.replaceAll("_", " ");
		return WordUtils.capitalizeFully(copy);
	}
	
	public static List<String> split(String string, String splitBy) {
		List<String> split = new ArrayList<>();
		
		int lastIndex = 0;
		while (true) {
			int index = string.indexOf(splitBy, lastIndex);
			if (index == -1) {
				break;
			}
			
			split.add(string.substring(lastIndex, index));
			lastIndex = index + 1;
		}
		
		split.add(string.substring(lastIndex, string.length()));
		return split;
	}
	
	public static String removeAll(String string, String remove) {
		return replaceAll(string, remove, "");
	}
	
	public static String removeLast(String string, String remove) {
		return replaceLast(string, remove, "");
	}
	
	public static String removeFirst(String string, String remove) {
		return replaceFirst(string, remove, "");
	}
	
	public static String replaceAll(String string, String toReplace, String replacement) {
		while (true) {
			int index = string.indexOf(toReplace);
			
			if (index == -1) {
				return string;
			}
			
			string = replaceIndex(string, toReplace, replacement, index);
		}
	}
	
	public static String replaceLast(String string, String toReplace, String replacement) {
		return replaceIndex(string, toReplace, replacement, string.lastIndexOf(toReplace));
	}
	
	public static String replaceFirst(String string, String toReplace, String replacement) {
		return replaceIndex(string, toReplace, replacement, string.indexOf(toReplace));
	}
	
	private static String replaceIndex(String string, String toReplace, String replacement, int leftEnd) {
		int leftStart = 0;
		
		if (leftEnd == -1) {
			return string;
		}
		
		int rightStart = leftEnd + toReplace.length();
		int rightEnd = string.length();
		
		String left = string.substring(leftStart, leftEnd);
		String right = string.substring(rightStart, rightEnd);
		
		return left + replacement + right;
	}
	
}
