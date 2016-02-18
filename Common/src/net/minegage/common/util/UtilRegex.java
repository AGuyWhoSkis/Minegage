package net.minegage.common.util;


import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class UtilRegex {
	
	public static final Pattern QUOTATION = Pattern.compile("\"([^\"]+)\"|'([^']+)'|\\S+");
	public static final Pattern ARGUMENTS = Pattern.compile("([^\"]\\S*|\".+?\")\\s*");
	private static final Pattern STRIP_PATTERN = Pattern.compile("(?i)[&" + ChatColor.COLOR_CHAR + "][0-9A-FK-OR]");
	
	public static List<String> matchQuotation(String string) {
		return match(QUOTATION, string);
	}
	
	/**
	 * @param string
	 *        The string to be split
	 * @return The string split into arguments. An argument is a single word, or multiple words
	 *         surrounded by quotes.
	 *         <p>
	 *         For example: This argument is passed:
	 *         <p>
	 *         These are single arguments "This is one argument"
	 *         <p>
	 *         This will return an array of [These, are, single, arguments, This is one argument]
	 */
	public static List<String> matchArguments(String string) {
		Matcher matcher = ARGUMENTS.matcher(string);
		
		List<String> matches = new ArrayList<>();
		while (matcher.find()) {
			matches.add(matcher.group(1)
					.replace("\"", "")
					.trim());
		}
		
		return matches;
	}
	
	public static List<String> match(Pattern pattern, String string) {
		return getMatches(pattern.matcher(string));
	}
	
	public static List<String> getMatches(Matcher matcher) {
		List<String> ret = new ArrayList<>();
		while (matcher.find()) {
			ret.add(matcher.group());
		}
		return ret;
	}
	
	public static String strip(String string) {
		return STRIP_PATTERN.matcher(string)
				.replaceAll("");
	}
	
}
