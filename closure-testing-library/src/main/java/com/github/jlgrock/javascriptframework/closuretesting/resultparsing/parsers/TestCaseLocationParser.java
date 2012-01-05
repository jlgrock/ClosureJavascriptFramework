package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers;

import java.util.regex.Pattern;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.IParsedDivObject;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCase;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCaseLocation;
import com.github.jlgrock.javascriptframework.mavenutils.parsing.ParseUtils;

/**
 * Parser for the location.
 * 
 * The line that reads something like one of the following:
 * 
 * /C:/Workspaces/maven-plugins/testDependencyResolution/target/javascriptFramework/testSuite/test.html
 * 
 */
public class TestCaseLocationParser implements IDivParser {
	/**
	 * pattern matches "/PATH".
	 */
	private static final String TEST_CASE_PATTERN = "/(.*)\\s*";
	
	@Override
	public final boolean matches(final TestCase testCase, final String divText) {
		boolean returnVal = false;
		if (testCase != null && testCase.getLocation() == null
				&& testCase.getSummary() != null
				&& Pattern.matches(TEST_CASE_PATTERN, divText)) {
			returnVal = true;
		}
		return returnVal;
	}

	@Override
	public final IParsedDivObject parse(final String divText) {
		String[] parsedValues = ParseUtils.parseIntoGroups(TEST_CASE_PATTERN, divText);
		String parsedTestCaseFilename = parsedValues[1];
		return new TestCaseLocation(parsedTestCaseFilename);
	}
}
