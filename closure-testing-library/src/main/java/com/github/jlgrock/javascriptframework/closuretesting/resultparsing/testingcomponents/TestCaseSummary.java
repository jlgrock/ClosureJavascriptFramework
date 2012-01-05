package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.TestResultType;

/**
 * The object storing the summary.
 * 
 * The line that reads something like one of the following:
 * 
 * Test for ../../../src/test/javascript/test.js [FAILED]
 * Test for ../../../src/test/javascript/test.js [PASSED]
 *
 */
public class TestCaseSummary implements IParsedDivObject {
	/**
	 * The relative location of the test file.
	 */
	private final String relativeLocation;
	
	/**
	 * The result of the overall test case.
	 */
	private final TestResultType result;
	
	/**
	 * Constructor.
	 * 
	 * @param relativeLocationIn The relative location of the test file.
	 * @param resultIn The result of the overall test case.
	 */
	public TestCaseSummary(final String relativeLocationIn, final TestResultType resultIn) {
		relativeLocation = relativeLocationIn;
		result = resultIn;
	}
	
	/**
	 * @return the relativeLocation
	 */
	public final String getRelativeLocation() {
		return relativeLocation;
	}

	/**
	 * @return the result
	 */
	public final TestResultType getResult() {
		return result;
	}

}
