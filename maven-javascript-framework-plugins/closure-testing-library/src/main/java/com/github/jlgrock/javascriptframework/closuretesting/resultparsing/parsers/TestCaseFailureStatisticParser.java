package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers;

import java.util.regex.Pattern;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.IParsedDivObject;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCase;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCaseFailureStatistic;
import com.github.jlgrock.javascriptframework.mavenutils.parsing.ParseUtils;

/**
 * The line that reads something like the following:
 * 
 * 12 passed, 2 failed.
 *
 */
public class TestCaseFailureStatisticParser implements IDivParser {
	/**
	 * pattern matches "# passed, # failed".
	 */
	private static final String PASSED_FAILED_PATTERN = "\\s*([0-9]*)\\s*passed,\\s*([0-9]*)\\s*failed.*";

	@Override
	public final boolean matches(final TestCase testCase, final String divText) {
		boolean returnVal = false;
		if (testCase != null && testCase.getTestCaseStart() == null) {
			if (Pattern.matches(PASSED_FAILED_PATTERN, divText)) {
				returnVal = true;
			}
		}
		return returnVal;
	}

	@Override
	public final IParsedDivObject parse(final String divText) {
		String[] parsedValues = ParseUtils.parseIntoGroups(PASSED_FAILED_PATTERN, divText);
		int parsedNumPassed = Integer.valueOf(parsedValues[1]);
		int parsedNumFailed = Integer.valueOf(parsedValues[2]);
		return new TestCaseFailureStatistic(parsedNumPassed, parsedNumFailed);
	}
}
