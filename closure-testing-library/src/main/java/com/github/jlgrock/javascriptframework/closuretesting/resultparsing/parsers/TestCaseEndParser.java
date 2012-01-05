package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.IParsedDivObject;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCase;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCaseEnd;
import com.github.jlgrock.javascriptframework.mavenutils.parsing.ParseUtils;

/**
 * Parser for the end timing div.
 * 
 * meant to parse the following:
 * 
 * 14:42:27.465 Done
 *
 */
public class TestCaseEndParser implements IDivParser {
	/**
	 * pattern matches "[time] Done".
	 */
	private static final String DONE_TAG_PATTERN = "\\s*([0-9]*):([0-9]*):([0-9]*).([0-9]*)\\s*Done\\s*";
	
	@Override
	public final boolean matches(final TestCase testCase, final String divText) {
		return Pattern.matches(DONE_TAG_PATTERN, divText);
	}

	@Override
	public final IParsedDivObject parse(final String divText) {
		String[] parsedValues = ParseUtils.parseIntoGroups(DONE_TAG_PATTERN, divText);
		Calendar parsedCompletionTime = new GregorianCalendar();
		Date currentDate = new Date();
		parsedCompletionTime.setTime(currentDate);
		
		parsedCompletionTime.set(Calendar.HOUR, Integer.valueOf(parsedValues[1]));
		parsedCompletionTime.set(Calendar.MINUTE, Integer.valueOf(parsedValues[2]));
		parsedCompletionTime.set(Calendar.SECOND, Integer.valueOf(parsedValues[3]));
		parsedCompletionTime.set(Calendar.MILLISECOND, Integer.valueOf(parsedValues[4]));
		return new TestCaseEnd(parsedCompletionTime);
	}
}
