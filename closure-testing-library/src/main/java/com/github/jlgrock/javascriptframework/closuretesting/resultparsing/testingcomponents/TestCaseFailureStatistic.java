package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents;


/**
 * The line that reads something like the following:
 * 
 * 12 passed, 2 failed.
 *
 */
public class TestCaseFailureStatistic implements IParsedDivObject {
	/**
	 * The number of test cases that passed.
	 */
	private final int numPassed;
	/**
	 * The number of test cases that failed.
	 */
	private final int numFailed;
	
	/**
	 * Constructor.
	 * 
	 * @param numPassedIn The number of test cases that passed
	 * @param numFailedIn The number of test cases that failed
	 */
	public TestCaseFailureStatistic(final int numPassedIn, final int numFailedIn) {
		numPassed = numPassedIn;
		numFailed = numFailedIn;
	}

	/**
	 * @return the numPassed
	 */
	public final int getNumPassed() {
		return numPassed;
	}

	/**
	 * @return the numFailed
	 */
	public final int getNumFailed() {
		return numFailed;
	}
}
