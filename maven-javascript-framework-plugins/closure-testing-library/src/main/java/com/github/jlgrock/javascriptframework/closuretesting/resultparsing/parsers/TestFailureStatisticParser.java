package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.TestResultType;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.IParsedDivObject;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCase;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestFailureStatistic;
import com.github.jlgrock.javascriptframework.mavenutils.parsing.ParseUtils;

/**
 * The parser for the failure statistic for an individual test.
 * 
 * This would be something like the following:
 * 
 * 14:42:28.346 testSomething3 : PASSED
 * 
 * or 
 * 
 * 14:42:28.346 testSomething3 : FAILED (run single test)
 * 
 */
public class TestFailureStatisticParser implements IDivParser {
	/**
	 * pattern matches "[time] testname : PASSED/FAILED".
	 */
	private static final String SINGLE_TEST_PATTERN = "\\s*([0-9]*):([0-9]*):([0-9]*)\\.([0-9]*)\\s*(\\w*)\\s:\\s(PASSED|FAILED)\\s*.*";
	@Override
	public final boolean matches(final TestCase testCase, final String divText) {
		boolean returnVal = false;
		if (testCase != null && testCase.getTestCaseStart() != null && testCase.getTestCaseEnd() == null) {
			if (Pattern.matches(SINGLE_TEST_PATTERN, divText)) {
				returnVal = true;
			}
		}
		return returnVal;
	}
	
	@Override
	public final IParsedDivObject parse(final String divText) {
		String[] parsedValues = ParseUtils.parseIntoGroups(SINGLE_TEST_PATTERN, divText);
		GregorianCalendar parsedCompletionTime = new GregorianCalendar();
		Date currentDate = new Date();
		parsedCompletionTime.setTime(currentDate);
		
		parsedCompletionTime.set(Calendar.HOUR, Integer.valueOf(parsedValues[1]));
		parsedCompletionTime.set(Calendar.MINUTE, Integer.valueOf(parsedValues[2]));
		parsedCompletionTime.set(Calendar.SECOND, Integer.valueOf(parsedValues[3]));
		parsedCompletionTime.set(Calendar.MILLISECOND, Integer.valueOf(parsedValues[4]));
		
		String parsedName = new String(parsedValues[5]);
		TestResultType parsedResult = TestResultType.getByName(parsedValues[6]);
		
		return new TestFailureStatistic(parsedCompletionTime, parsedName, parsedResult);
	}
}
