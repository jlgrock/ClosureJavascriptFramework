package com.github.jlgrock.javascriptframework.closuretesting.resultparsing;

import java.util.HashSet;
import java.util.Set;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers.IDivParser;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers.TestCaseEndParser;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers.TestCaseFailureStatisticParser;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers.TestCaseLocationParser;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers.TestCaseStartParser;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers.TestCaseSummaryParser;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers.TestCaseTimingStatisticParser;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers.TestFailureStatisticParser;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers.TestTimingStatisticParser;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.IParsedDivObject;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCase;

/**
 * Factory class for determining the correct parser for the testing output
 * section.
 * 
 */
public final class DivParseFactory {
	/**
	 * Private constructor for a utility class.
	 */
	private DivParseFactory() {
	}

	/**
	 * The registered classes for matching against. This is hard coded for now,
	 * but should likely be dynamic in the future.
	 */
	private static Set<IDivParser> registeredClasses;

	// TODO this could be more dynamic/elegant
	static {
		registeredClasses = new HashSet<IDivParser>();
		registeredClasses.add(new TestCaseEndParser());
		registeredClasses.add(new TestCaseFailureStatisticParser());
		registeredClasses.add(new TestCaseLocationParser());
		registeredClasses.add(new TestCaseStartParser());
		registeredClasses.add(new TestCaseSummaryParser());
		registeredClasses.add(new TestCaseTimingStatisticParser());
		registeredClasses.add(new TestFailureStatisticParser());
		registeredClasses.add(new TestTimingStatisticParser());
	}

	/**
	 * Will search through the registered patterns to find a div parser to
	 * provide for parsing.
	 * 
	 * @param testCase
	 *            the testCase referred to in the factory, used for determining
	 *            whether certain sections have been hit yet in the matcher.
	 * @param divText
	 *            the text to match
	 * @return Returns the parsed DivParser object
	 */
	public static IParsedDivObject factory(final TestCase testCase,
			final String divText) {
		for (IDivParser registeredClass : registeredClasses) {
			if (registeredClass.matches(testCase, divText)) {
				return registeredClass.parse(divText);
			}
		}
		return null;
	}
}
