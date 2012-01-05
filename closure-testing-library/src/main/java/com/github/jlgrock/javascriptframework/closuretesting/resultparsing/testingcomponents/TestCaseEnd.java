package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents;

import java.util.Calendar;

/**
 * The end timing div.
 * 
 * meant to parse the following:
 * 
 * 14:42:27.465 Done
 *
 */
public class TestCaseEnd implements IParsedDivObject {
	/**
	 * Constructor.
	 * 
	 * @param completionTimeIn the time completed
	 */
	public TestCaseEnd(final Calendar completionTimeIn) {
		completionTime = completionTimeIn;
	}
	
	/**
	 * @return the completionTime
	 */
	public final Calendar getCompletionTime() {
		return completionTime;
	}

	/**
	 * The time completed.
	 */
	private final Calendar completionTime;

}
