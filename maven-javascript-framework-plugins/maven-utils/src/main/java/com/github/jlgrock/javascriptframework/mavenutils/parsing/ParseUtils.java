package com.github.jlgrock.javascriptframework.mavenutils.parsing;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseUtils {
	private ParseUtils() {}
		
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
