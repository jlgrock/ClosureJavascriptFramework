package com.github.jlgrock.javascriptframework.mavenutils.parsing;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities for parsing strings.
 */
public final class ParseUtils {
	/**
	 * Private constructor.
	 */
	private ParseUtils() {}
	
	/**
	 * Parse the test into groups based on the regular expression.
	 * @param regex the regex to use
	 * @param parseText the text to parse
	 * @return an array of strings from the parsing.
	 */
	public static String[] parseIntoGroups(final String regex, final String parseText) {
		ArrayList<String> parsedValues = new ArrayList<String>();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(parseText);
		boolean matches = matcher.find();
		if (matches) {
		    // Get all groups for this match
		    for (int i=0; i<=matcher.groupCount(); i++) {
		        String groupStr = matcher.group(i);
		        parsedValues.add(groupStr);
		    }
		}
		return parsedValues.toArray(new String[parsedValues.size()]);
	}
}
