package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers;

import java.io.File;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.DivParseFactory;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.IParsedDivObject;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCase;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCaseEnd;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCaseFailureStatistic;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCaseLocation;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCaseStart;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCaseSummary;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCaseTimingStatistic;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestFailureStatistic;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestTimingStatistic;

/**
 * The parser for parsed Google Test Case objects.
 */
public final class TestCaseParser {
	/**
	 * The test case that will be created and parsed during use.
	 */
	private TestCase testCase;

	/**
	 * Constructor.
	 * @param fileBasedOn the file that the test case is run on.
	 */
	public TestCaseParser(final File fileBasedOn) {
		testCase = new TestCase(fileBasedOn);
	}

	/**
	 * The parser for the test case. Will parse the body into java objects.
	 * 
	 * @param body
	 *            the body to parse
	 * @return the java objects representing the google test cases
	 */
	public TestCase parse(final WebElement body) {
		List<WebElement> divs = parseBody(body);
		WebElement div = null;
		int i = 0;
		while (i < divs.size()) {
			div = divs.get(i);
			String divText = div.getText();
			IParsedDivObject divParser = DivParseFactory.factory(testCase,
					divText);
			setDivProperty(divText, divParser);
			i++;
		}
		return testCase;
	}

	/**
	 * Parsing the body into divs.
	 * 
	 * @param body
	 *            the body to parse
	 * @return the list of div web elements
	 */
	private List<WebElement> parseBody(final WebElement body) {
		return body.findElements(By.tagName("div"));
	}

	/**
	 * store a div to the appropriate place.
	 * 
	 * @param divText the raw div text
	 * @param divParsed
	 *            the div that was parsed
	 */
	private void setDivProperty(final String divText, final IParsedDivObject divParsed) {
		if (divParsed instanceof TestCaseSummary) {
			testCase.setSummary((TestCaseSummary) divParsed);
		}

		if (divParsed instanceof TestCaseLocation) {
			testCase.setLocation((TestCaseLocation) divParsed);
		}

		if (divParsed instanceof TestCaseTimingStatistic) {
			testCase.setTimingStatistic((TestCaseTimingStatistic) divParsed);
		}

		if (divParsed instanceof TestCaseFailureStatistic) {
			testCase.setFailureStatistic((TestCaseFailureStatistic) divParsed);
		}

		if (divParsed instanceof TestTimingStatistic) {
			testCase.setTestTimingStatistic((TestTimingStatistic) divParsed);
		}

		if (divParsed instanceof TestCaseStart) {
			testCase.setTestCaseStart((TestCaseStart) divParsed);
		}

		if (divParsed instanceof TestFailureStatistic) {
			testCase.addToTestFailureStatistics((TestFailureStatistic) divParsed);
		}
		
		if (testCase.getTestCaseStart() != null && testCase.getTestCaseEnd() == null 
				&& divParsed == null) {
			//add to last failure statistic
			testCase.addToLastTestFailureStatistic(divText);
		}
		if (divParsed instanceof TestCaseEnd) {
			testCase.setTestCaseEnd((TestCaseEnd) divParsed);
		}
		testCase.addToRawDivs(divText);
	}
}
