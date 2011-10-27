package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents;


/**
 * The object containing timing statistics for the Test Case.
 * 
 * Parses something like the following:
 * 
 * 2 of 2 tests run in 28ms.
 * 
 */
public class TestCaseTimingStatistic implements IParsedDivObject {
	/**
	 * the number of test cases run.
	 */
	private final int numberRun;
	
	/**
	 * the number of total test cases (run, or not run).
	 */
	private final int numberTotal;
	
	/**
	 * the time that it took to run.
	 */
	private final int timeValue;
	
	/**
	 * the units applied to the time (ms, sec, etc.).
	 */
	private final String timeUnits;

	/**
	 * Constructor.
	 * 
	 * @param numberRunIn the number of test cases run.
	 * @param numberTotalIn the number of total test cases (run, or not run).
	 * @param timeValueIn the time that it took to run.
	 * @param timeUnitsIn the units applied to the time (ms, sec, etc.).
	 */
	public TestCaseTimingStatistic(final int numberRunIn, final int numberTotalIn,
			final int timeValueIn, final String timeUnitsIn) {
		numberRun = numberRunIn;
		numberTotal = numberTotalIn;
		timeValue = timeValueIn;
		timeUnits = timeUnitsIn;
	}

	/**
	 * @return the numberRun
	 */
	public final int getNumberRun() {
		return numberRun;
	}

	/**
	 * @return the numberTotal
	 */
	public final int getNumberTotal() {
		return numberTotal;
	}

	/**
	 * @return the timeValue
	 */
	public final int getTimeValue() {
		return timeValue;
	}

	/**
	 * @return the timeUnits
	 */
	public final String getTimeUnits() {
		return timeUnits;
	}

}
