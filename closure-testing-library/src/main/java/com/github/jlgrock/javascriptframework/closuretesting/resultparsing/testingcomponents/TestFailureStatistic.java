package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.TestResultType;

/**
 * The failure statistic for an individual test.
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
public class TestFailureStatistic implements IParsedDivObject {
	/**
	 * the time of completion of the test.
	 */
	private final Calendar completionTime;

	/**
	 * the name of the test.
	 */
	private final String nameOfTest;

	/**
	 * the result of the test.
	 */
	private final TestResultType result;
	
	/**
	 * The reasons behind the failure, if there was one.
	 */
	private final List<String> failureReasons;
	/**
	 * Constructor.
	 * 
	 * @param completionTimeIn the time of completion of the test
	 * @param nameOfTestIn the name of the test
	 * @param resultIn the result of the test
	 */
	public TestFailureStatistic(final Calendar completionTimeIn, 
			final String nameOfTestIn, final TestResultType resultIn) {
		completionTime = completionTimeIn;
		nameOfTest = nameOfTestIn;
		result = resultIn;
		failureReasons = new ArrayList<String>();
	}
	
	/**
	 * @return the completionTime
	 */
	public final Calendar getCompletionTime() {
		return completionTime;
	}
	
	/**
	 * @return the nameOfTest
	 */
	public final String getNameOfTest() {
		return nameOfTest;
	}
	
	/**
	 * @return the result
	 */
	public final TestResultType getResult() {
		return result;
	}
	
	/**
	 * Adds to the failureReasons ArrayList.
	 * 
	 * @param reason the string to add to the array
	 */
	public final void addToFailureReasons(final String reason) {
		failureReasons.add(reason);
	}
	
	/**
	 * @return failureReasons
	 */
	public final List<String> getFailureReasons() {
		return failureReasons;
	}
}
