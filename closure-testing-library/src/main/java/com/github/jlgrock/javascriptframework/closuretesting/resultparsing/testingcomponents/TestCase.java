package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The container for parsed Google Test Case objects.
 */
public class TestCase {
	/**
	 * The prefix to the test case name for the test case error.
	 */
	private static final String TEST_CASE_PREFIX = "There was a problem with the '";
	/**
	 * The suffix to the test case name for the test case error.
	 */
	private static final String TEST_CASE_SUFFIX = "' Test Case.";

	/**
	 * Simple newline representation.
	 */
	private static final String NEWLINE = "\n";

	/**
	 * Simple tab representation.
	 */
	private static final String TAB = "\t";
	
	/**
	 * the prefix for a test for the test case error output.
	 */
	private static final String TEST_NAME_PREFIX = "* Test = ";
	/**
	 * the suffix for a test for the test case error output.
	 */
	private static final String RESULT_PREFIX = " : ";

	/**
	 * The summary section div, detailing the overall status.
	 */
	private TestCaseSummary summary;

	/**
	 * The absolute location div.
	 */
	private TestCaseLocation location;

	/**
	 * The timing statistics div that details how long the entire test took.
	 */
	private TestCaseTimingStatistic timingStatistic;

	/**
	 * The number of successes and failures for the entire test case.
	 */
	private TestCaseFailureStatistic failureStatistic;

	/**
	 * the timing for the entire test case.
	 */
	private TestTimingStatistic testTimingStatistic;

	/**
	 * The start information for the tests.
	 */
	private TestCaseStart testCaseStart;

	/**
	 * The set of information of all tests.
	 */
	private List<TestFailureStatistic> testFailureStatistics;

	/**
	 * The end information for the tests.
	 */
	private TestCaseEnd testCaseEnd;

	/**
	 * The div text for every div, even if it is not parsed into an object.
	 */
	private Set<String> rawDivs;

	/**
	 * Constructor.
	 */
	public TestCase() {
		testFailureStatistics = new ArrayList<TestFailureStatistic>();
		rawDivs = new HashSet<String>();
	}

	/**
	 * @return the summary
	 */
	public final TestCaseSummary getSummary() {
		return summary;
	}

	/**
	 * @param summaryIn
	 *            the summary to set
	 */
	public final void setSummary(final TestCaseSummary summaryIn) {
		this.summary = summaryIn;
	}

	/**
	 * @return the location
	 */
	public final TestCaseLocation getLocation() {
		return location;
	}

	/**
	 * @param locationIn
	 *            the location to set
	 */
	public final void setLocation(final TestCaseLocation locationIn) {
		this.location = locationIn;
	}

	/**
	 * @return the timingStatistic
	 */
	public final TestCaseTimingStatistic getTimingStatistic() {
		return timingStatistic;
	}

	/**
	 * @param timingStatisticIn
	 *            the timingStatistic to set
	 */
	public final void setTimingStatistic(
			final TestCaseTimingStatistic timingStatisticIn) {
		this.timingStatistic = timingStatisticIn;
	}

	/**
	 * @return the failureStatistic
	 */
	public final TestCaseFailureStatistic getFailureStatistic() {
		return failureStatistic;
	}

	/**
	 * @param failureStatisticIn
	 *            the failureStatistic to set
	 */
	public final void setFailureStatistic(
			final TestCaseFailureStatistic failureStatisticIn) {
		this.failureStatistic = failureStatisticIn;
	}

	/**
	 * @return the testTimingStatistic
	 */
	public final TestTimingStatistic getTestTimingStatistic() {
		return testTimingStatistic;
	}

	/**
	 * @param testTimingStatisticIn
	 *            the testTimingStatistic to set
	 */
	public final void setTestTimingStatistic(
			final TestTimingStatistic testTimingStatisticIn) {
		this.testTimingStatistic = testTimingStatisticIn;
	}

	/**
	 * @return the testCaseStart
	 */
	public final TestCaseStart getTestCaseStart() {
		return testCaseStart;
	}

	/**
	 * @param testCaseStartIn
	 *            the testCaseStart to set
	 */
	public final void setTestCaseStart(final TestCaseStart testCaseStartIn) {
		this.testCaseStart = testCaseStartIn;
	}

	/**
	 * @return the testFailureStatistics
	 */
	public final List<TestFailureStatistic> getTestFailureStatistics() {
		return testFailureStatistics;
	}

	/**
	 * @param testFailureStatisticsIn
	 *            the testFailureStatistics to set
	 */
	public final void setTestFailureStatistics(
			final List<TestFailureStatistic> testFailureStatisticsIn) {
		this.testFailureStatistics = testFailureStatisticsIn;
	}

	/**
	 * @return the testCaseEnd
	 */
	public final TestCaseEnd getTestCaseEnd() {
		return testCaseEnd;
	}

	/**
	 * @param testCaseEndIn
	 *            the testCaseEnd to set
	 */
	public final void setTestCaseEnd(final TestCaseEnd testCaseEndIn) {
		this.testCaseEnd = testCaseEndIn;
	}

	/**
	 * @return the rawDivs
	 */
	public final Set<String> getRawDivs() {
		return rawDivs;
	}

	/**
	 * @param rawDivsIn
	 *            the rawDivs to set
	 */
	public final void setRawDivs(final Set<String> rawDivsIn) {
		this.rawDivs = rawDivsIn;
	}

	/**
	 * @param rawDivIn
	 *            the rawDiv to set
	 */
	public final void addToRawDivs(final String rawDivIn) {
		this.rawDivs.add(rawDivIn);
	}

	@Override
	public final String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(TestCase.NEWLINE);
		sb.append(TestCase.TEST_CASE_PREFIX);
		if (getSummary() != null && !getLocation().equals("")) {
			sb.append(getLocation());
		} else {
			sb.append("Error.  Could not parse contents of file.  Please review previous errors above for more information.");
			sb.append(TestCase.NEWLINE);
		}
		sb.append(TestCase.TEST_CASE_SUFFIX);
		sb.append(TestCase.NEWLINE);
		for (TestFailureStatistic singleTest : getTestFailureStatistics()) {
			sb.append(TestCase.TEST_NAME_PREFIX);
			sb.append(singleTest.getNameOfTest());
			sb.append(TestCase.RESULT_PREFIX);
			sb.append(singleTest.getResult());
			sb.append(TestCase.NEWLINE);
			for (String reason : singleTest.getFailureReasons()) {
				sb.append(TestCase.TAB);
				sb.append(reason);
				sb.append(TestCase.NEWLINE);
			}
		}
		return sb.toString();
	}

	/**
	 * Will add a testFailureStatistic to the set.
	 * 
	 * @param testFailureStatistic
	 *            the object to add
	 */
	public final void addToTestFailureStatistics(
			final TestFailureStatistic testFailureStatistic) {
		this.testFailureStatistics.add(testFailureStatistic);
	}

	/**
	 * Will add a lastTestFailureStatistic to the set.
	 * @param divText the text of the div
	 */
	public final void addToLastTestFailureStatistic(final String divText) {
		TestFailureStatistic tfs = this.testFailureStatistics.get(this.testFailureStatistics.size()-1);
		tfs.addToFailureReasons(divText);
	}
}
