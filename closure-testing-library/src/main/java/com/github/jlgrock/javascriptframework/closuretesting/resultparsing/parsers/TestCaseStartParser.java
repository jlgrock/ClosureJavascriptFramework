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
	 * The position of the hour.
	 */
	private static final int HOUR_POSITION = 1;

	/**
	 * The position of the minute.
	 */
	private static final int MINUTE_POSITION = 2;
	
	/**
	 * The position of the second.
	 */
	private static final int SECOND_POSITION = 3;

	/**
	 * The position of the millisecond.
	 */
	private static final int MILLISECOND_POSITION = 4;
	
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
		
		parsedCompletionTime.set(Calendar.HOUR, Integer.valueOf(parsedValues[HOUR_POSITION]));
		parsedCompletionTime.set(Calendar.MINUTE, Integer.valueOf(parsedValues[MINUTE_POSITION]));
		parsedCompletionTime.set(Calendar.SECOND, Integer.valueOf(parsedValues[SECOND_POSITION]));
		parsedCompletionTime.set(Calendar.MILLISECOND, Integer.valueOf(parsedValues[MILLISECOND_POSITION]));
		return new TestCaseStart(parsedCompletionTime);
	}
}
