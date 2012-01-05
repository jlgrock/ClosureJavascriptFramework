package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents;


/**
 * The object representing the average time per test that it took to execute the test cases.
 * 
 * This would be something like the following:
 * 
 * 2 ms/test. 13 files loaded.
 *
 */
public class TestTimingStatistic implements IParsedDivObject {
	/**
	 * the time that it took to execute the test.
	 */
	private final int time;
	
	/**
	 * the unit of measurement (ms, sec, etc.).
	 */
	private final String unitOfMeasurement;
	
	/**
	 * the number of files loaded.
	 */
	private final int numFiles;
	
	/**
	 * Constructor.
	 * 
	 * @param timeIn the time that it took to execute the test.
	 * @param unitOfMeasurementIn the unit of measurement (ms, sec, etc.).
	 * @param numFilesIn the number of files loaded.
	 */
	public TestTimingStatistic(final int timeIn, final String unitOfMeasurementIn, final int numFilesIn) {
		time = timeIn;
		unitOfMeasurement = unitOfMeasurementIn;
		numFiles = numFilesIn;
	}

	/**
	 * @return the time
	 */
	public final int getTime() {
		return time;
	}

	/**
	 * @return the unitOfMeasurement
	 */
	public final String getUnitOfMeasurement() {
		return unitOfMeasurement;
	}

	/**
	 * @return the numFiles
	 */
	public final int getNumFiles() {
		return numFiles;
	}


}
