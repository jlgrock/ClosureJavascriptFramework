package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.IParsedDivObject;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCase;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCaseStart;
import com.github.jlgrock.javascriptframework.mavenutils.parsing.ParseUtils;

/**
 * The start timing div.
 * 
 * meant to parse the following:
 * 
 * 14:42:27.465 Start
 *
 */
public class TestCaseStartParser implements IDivParser {
	/**
	 * pattern matches "[time] Start".
	 */
	private static final String START_TAG_PATTERN = "\\s*([0-9]*):([0-9]*):([0-9]*).([0-9]*)\\s*Start\\s*";

	@Override
	public final boolean matches(final TestCase testCase, final String divText) {
		return Pattern.matches(START_TAG_PATTERN, divText);
	}

	@Override
	public final IParsedDivObject parse(final String divText) {
		String[] parsedValues = ParseUtils.parseIntoGroups(START_TAG_PATTERN, divText);
		GregorianCalendar parsedCompletionTime = new GregorianCalendar();
		Date currentDate = new Date();
		parsedCompletionTime.setTime(currentDate);
		
		parsedCompletionTime.set(Calendar.HOUR, Integer.valueOf(parsedValues[1]));
		parsedCompletionTime.set(Calendar.MINUTE, Integer.valueOf(parsedValues[2]));
		parsedCompletionTime.set(Calendar.SECOND, Integer.valueOf(parsedValues[3]));
		parsedCompletionTime.set(Calendar.MILLISECOND, Integer.valueOf(parsedValues[4]));
		return new TestCaseStart(parsedCompletionTime);
	}
}
