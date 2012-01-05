package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers;

import java.util.regex.Pattern;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.TestResultType;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.IParsedDivObject;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCase;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCaseSummary;
import com.github.jlgrock.javascriptframework.mavenutils.parsing.ParseUtils;

/**
 * The object used for parsing the summary.
 * 
 * The line that reads something like one of the following:
 * 
 * Test for ../../../src/test/javascript/test.js [FAILED]
 * Test for ../../../src/test/javascript/test.js [PASSED]
 *
 */
public class TestCaseSummaryParser implements IDivParser {
	/**
	 * The pattern used to match the summary line.
	 */
	private static final String TEST_CASE_PATTERN = ".*Test\\sfor\\s(.*)\\s*\\[(PASSED|FAILED)\\]\\s*";


	@Override
	public final boolean matches(final TestCase testCase, final String divText) {
		boolean returnVal = false;
		if (testCase != null && testCase.getSummary() == null) {
			if (Pattern.matches(TEST_CASE_PATTERN, divText)) {
				returnVal = true;
			}
		}
		return returnVal;
	}

	@Override
	public final IParsedDivObject parse(final String divText) {
		String[] parsedValues = ParseUtils.parseIntoGroups(TEST_CASE_PATTERN, divText);
		String parsedRelativeLocation = parsedValues[1];
		TestResultType parsedResult = TestResultType.getByName(parsedValues[2]);
		return new TestCaseSummary(parsedRelativeLocation, parsedResult);
	}
}
