package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers;

import java.util.regex.Pattern;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.IParsedDivObject;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCase;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestTimingStatistic;
import com.github.jlgrock.javascriptframework.mavenutils.parsing.ParseUtils;

/**
 * The parsing for the object representing the average time per test that it took to execute the test cases.
 * 
 * This would be something like the following:
 * 
 * 2 ms/test. 13 files loaded.
 *
 */
public class TestTimingStatisticParser implements IDivParser {
	/**
	 * The position of the time.
	 */
	private static int TIME_POSITION = 1;

	/**
	 * The position of the measurement.
	 */
	private static int MEASUREMENT_POSITION = 2;
	
	/**
	 * The position of the number of files.
	 */
	private static int NUM_FILES_POSITION = 3;
	
	/**
	 * pattern matches "# passed, # failed".
	 */
	private static final String PASSED_FAILED_PATTERN = "\\s*([0-9]*)\\s*(\\w*)/test,\\s*([0-9]*)\\s*files loaded.\\s*";

	@Override
	public final boolean matches(final TestCase testCase, final String divText) {
		boolean returnVal = false;
		if (testCase != null && testCase.getTestCaseStart() == null
				&& testCase.getFailureStatistic() != null
				&& Pattern.matches(PASSED_FAILED_PATTERN, divText)) {
			returnVal = true;
		}
		return returnVal;
	}

	@Override
	public final IParsedDivObject parse(final String divText) {
		String[] parsedValues = ParseUtils.parseIntoGroups(
				PASSED_FAILED_PATTERN, divText);
		int parsedTime = Integer.valueOf(parsedValues[TIME_POSITION]);
		String parsedUnitOfMeasurement = parsedValues[MEASUREMENT_POSITION];
		int parsedNumFiles = Integer.valueOf(parsedValues[NUM_FILES_POSITION]);
		return new TestTimingStatistic(parsedTime, parsedUnitOfMeasurement,
				parsedNumFiles);
	}
}
