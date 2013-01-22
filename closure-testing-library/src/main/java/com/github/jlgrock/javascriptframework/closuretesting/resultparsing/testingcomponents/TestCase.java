package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.TestResultType;

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
	 * An additional suffix to the test case name for the test cases where
	 * unable to parse.
	 */
	private static final String TEST_CASE_TIMEOUT = "\n\tTimed out before completion of test case." +
			"\n\tMake sure there is not an infinite loop and that you include \n\t'goog.require(\"goog.testing.jsunit\");' " +
			"at the top of your javascript file.";

	/**
	 * An additional suffix to the test case name for the test cases where
	 * unable to parse.
	 */
	private static final String TEST_CASE_BAD_OUTPUT = "\n\tCompleted execution of test case but was not able to find " +
			"standardized test results.\n\tMake sure there is not an infinite loop " +
			"and that you include \n\t'goog.require(\"goog.testing.jsunit\");' " +
			"at the top of your javascript file.";

	/**
	 * An additional suffix to the test case name for the test cases where
	 * unable to parse.
	 */
	private static final String TEST_SCRIPT_ERROR = "\n\tScript Error: ";
	
	/**
	 * The error message when there is no parseable test cases in the file.
	 */
	private static final String NO_TEST_CASES = "\n\tUnable to scrape the results of any test cases.  " +
			"Please check the output to guarantee that test cases are being executed or remove the test case file.";
	
	/**
	 * An additional suffix to the test case name for the test cases where
	 * unable to parse.
	 */
	private static final String TEST_UNKNOWN_ERROR = "\n\tUnknown Error: ";

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
	 * The result of the overall test case.
	 */
	private TestResultType result;

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
	 * The file that the test case results are based off of.
	 */
	private final File fileBasedOn;

	/**
	 * The message to be used when reporting an error of the entire test case
	 * (only used when there is a processing error).
	 */
	private String errorMessage;

	/**
	 * Error Constructor. This should only be used when constructing an error
	 * with processing.
	 * 
	 * @param fileBasedOnIn
	 *            the file that the test case results are based off of.
	 * @param resultIn
	 *            the initial value to set the Test case to. This is true by
	 *            default and will be overwritten if test cases properties fail.
	 * @param errorMessageIn
	 *            The error message to be used in error reporting.
	 */
	public TestCase(final File fileBasedOnIn, final TestResultType resultIn,
			final String errorMessageIn) {
		this(fileBasedOnIn);
		result = resultIn;
		errorMessage = errorMessageIn;
	}

	/**
	 * Constructor.
	 * 
	 * @param fileBasedOnIn
	 *            the file that the test case results are based off of.
	 */
	public TestCase(final File fileBasedOnIn) {
		testFailureStatistics = new ArrayList<TestFailureStatistic>();
		rawDivs = new HashSet<String>();
		fileBasedOn = fileBasedOnIn;

		// It is assumed that the test case passed until something indicates
		// otherwise
		result = TestResultType.PASSED;
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
		if (summaryIn != null) {
			result = summaryIn.getResult();
		}
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
		// if any of the statistics are marked as a failure, mark the test case
		// as a failure
		for (TestFailureStatistic stat : testFailureStatisticsIn) {
			if (stat == null || stat.getResult() == TestResultType.FAILED) {
				result = TestResultType.FAILED;
			}
		}
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
		sb.append(fileBasedOn.getAbsolutePath());
		sb.append(TestCase.TEST_CASE_SUFFIX);
		if (result == TestResultType.TIMED_OUT) {
			sb.append(TEST_CASE_TIMEOUT);
			sb.append(TestCase.NEWLINE);
		} else if (result == TestResultType.BAD_OUTPUT) {
			sb.append(TEST_CASE_BAD_OUTPUT);
			sb.append(TestCase.NEWLINE);
		} else if (result == TestResultType.SCRIPT_ERROR) {
			sb.append(TEST_SCRIPT_ERROR);
			sb.append(TestCase.NEWLINE);
			sb.append(errorMessage);
			sb.append(TestCase.NEWLINE);
		} else if (result == TestResultType.UNABLE_TO_EXECUTE) {
			sb.append(TEST_UNKNOWN_ERROR);
			sb.append(TestCase.NEWLINE);
			sb.append(errorMessage);
			sb.append(TestCase.NEWLINE);
		} else if (getTestFailureStatistics().size() == 0) {
			sb.append(NO_TEST_CASES);
			sb.append(TestCase.NEWLINE);
		} else {
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
	 * 
	 * @param divText
	 *            the text of the div
	 */
	public final void addToLastTestFailureStatistic(final String divText) {
		if (this.testFailureStatistics.size() > 0) {
			TestFailureStatistic tfs = this.testFailureStatistics
					.get(this.testFailureStatistics.size() - 1);
			tfs.addToFailureReasons(divText);
		}
	}

	/**
	 * @return the file that the test case result object was based on.
	 */
	public final File getFileBasedOn() {
		return fileBasedOn;
	}

	/**
	 * @return whether the result of executing the test case file.
	 */
	public final TestResultType getResult() {
		return result;
	}
}
