package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers;

import java.util.regex.Pattern;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.IParsedDivObject;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCase;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCaseTimingStatistic;
import com.github.jlgrock.javascriptframework.mavenutils.parsing.ParseUtils;

/**
 * The parser for the object containing timing statistics for the Test Case.
 * 
 * Parses something like the following:
 * 
 * 2 of 2 tests run in 28ms.
 * 
 */
public class TestCaseTimingStatisticParser implements IDivParser {
	/**
	 * The position of the hour.
	 */
	private static final int NUMBER_RUN_POSITION = 1;

	/**
	 * The position of the minute.
	 */
	private static final int NUMBER_TOTAL_POSITION = 2;
	
	/**
	 * The position of the time value.
	 */
	private static final int TIME_VALUE_POSITION = 3;

	/**
	 * The position of the time units.
	 */
	private static final int TIME_UNITS_POSITION = 4;
	
	/**
	 * pattern matches "# ms/test.  # files loaded."
	 */
	private static final String TEST_CASE_TIMING_PATTERN = "\\s*([0-9]*)\\s*of\\s*([0-9]*)\\s*tests run in\\s*([0-9]*)(\\w)*\\.";

	@Override
	public final IParsedDivObject parse(final String divText) {
		String[] parsedValues = ParseUtils.parseIntoGroups(TEST_CASE_TIMING_PATTERN, divText);
		int parsedNumberRun = Integer.valueOf(parsedValues[NUMBER_RUN_POSITION]);
		int parsedNumberTotal = Integer.valueOf(parsedValues[NUMBER_TOTAL_POSITION]);
		int parsedTimeValue = Integer.valueOf(parsedValues[TIME_VALUE_POSITION]);
		String parsedTimeUnits = parsedValues[TIME_UNITS_POSITION];
		return new TestCaseTimingStatistic(parsedNumberRun, parsedNumberTotal, parsedTimeValue, parsedTimeUnits);
	}

	@Override
	public final boolean matches(final TestCase testCase, final String divText) {
		boolean retVal = false;
		if (testCase != null && testCase.getLocation() != null
				&& testCase.getTimingStatistic() == null
				&& Pattern.matches(TEST_CASE_TIMING_PATTERN, divText)) {
			retVal = true;
		}
		return retVal;
	}
}
