package com.github.jlgrock.javascriptframework.closuretestingreport;

import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.AbstractMavenReportRenderer;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCase;

/**
 * Will write the output for the unit testing.
 * 
 */
public class ClosureTestingReportGenerator extends AbstractMavenReportRenderer {
	/**
	 * Denoting the first character of the upper case ASCII letters.
	 */
	static final int UPPERCASE_ASCII_LOWER_BOUND = 65;
	/**
	 * Denoting the last character of the upper case ASCII letters.
	 */
	static final int UPPERCASE_ASCII_UPPER_BOUND = 90;
	/**
	 * Denoting the first character of the lower case ASCII letters.
	 */
	static final int LOWERCASE_ASCII_LOWER_BOUND = 97;
	/**
	 * Denoting the last character of the lower case ASCII letters.
	 */
	static final int LOWERCASE_ASCII_UPPER_BOUND = 122;

	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(ClosureTestingReportGenerator.class);

	/**
	 * the parsed test cases, used for writing statistical output.
	 */
	private final Set<TestCase> testCases;

	/**
	 * Constructor.
	 * 
	 * @param sink
	 *            the sink to use for writing
	 * @param testCasesIn
	 *            the parsed test cases, used for writing statistical output
	 */
	public ClosureTestingReportGenerator(final Sink sink,
			final Set<TestCase> testCasesIn) {
		super(sink);
		this.testCases = testCasesIn;
	}

	@Override
	public final String getTitle() {
		return "Google Closure Unit Testing";
	}

	@Override
	protected final void renderBody() {

		sink.body();

		// Summary section
		summarySection(sink);

		// File Link section
		linkSection(sink);

		// Raw Dump section
		rawDumpSection(sink);

		sink.body_();
		sink.flush();
		sink.close();
	}

	/**
	 * Writes the entire raw dump section.
	 * 
	 * @param sink
	 *            the sink used for writing
	 */
	private void rawDumpSection(final Sink sink) {
		sink.horizontalRule();
		sink.lineBreak();
		sink.section2();
		sink.sectionTitle2();
		sink.text("Closure Unit Testing Raw Dump");
		sink.sectionTitle2_();
		makeRawDump(sink);
		sink.section2_();
	}

	/**
	 * Writes the entire link section.
	 * 
	 * @param sink
	 *            the sink used for writing
	 */
	private void linkSection(final Sink sink) {
		sink.section2();
		sink.lineBreak();
		sink.sectionTitle2();
		sink.text("Files");
		sink.sectionTitle2_();
		makeLinks(sink);
		sink.section2_();
	}

	/**
	 * Writes the entire summary section.
	 * 
	 * @param sink
	 *            the sink used for writing
	 */
	private void summarySection(final Sink sink) {
		sink.section1();
		sink.sectionTitle1();
		sink.text("Google Closure Unit Testing");
		sink.sectionTitle1_();

		sink.text("List of executed Google Closure Library Unit Test results.");
		sink.section1_();
	}

	/**
	 * Iterates over all the raw input and writes them to the sink.
	 * 
	 * @param sink
	 *            the sink used for writing
	 */
	private void makeRawDump(final Sink sink) {
		for (TestCase testCase : testCases) {
			LOGGER.debug("doing raw dump for test case: "
					+ testCase.getSummary().getRelativeLocation());
			sink.paragraph();
			sink.bold();
			String pathAnchor = encodeAnchor(testCase.getSummary()
					.getRelativeLocation());
			sink.anchor(pathAnchor);
			sink.text(testCase.getSummary().getRelativeLocation());
			sink.anchor_();
			sink.bold_();
			sink.lineBreak();
			sink.lineBreak();
			for (String div : testCase.getRawDivs()) {
				sink.text(div);
			}
			sink.paragraph_();
			sink.horizontalRule();
		}
	}

	/**
	 * Replace all characters that are unsafe in a url string (past the anchor
	 * mark "#").
	 * 
	 * @param relativeLocation
	 *            the anchor location
	 * @return return the url safe string
	 */
	private String encodeAnchor(final String relativeLocation) {
		StringBuffer calcString = new StringBuffer();
		calcString.append("fileref");
		for (int i = 0; i < relativeLocation.length(); i++) {
			int asciiVal = (int) relativeLocation.charAt(i);
			if ((asciiVal >= UPPERCASE_ASCII_LOWER_BOUND && asciiVal <= UPPERCASE_ASCII_UPPER_BOUND)
					|| (asciiVal >= LOWERCASE_ASCII_LOWER_BOUND && asciiVal <= LOWERCASE_ASCII_UPPER_BOUND)) {
				calcString.append((char) asciiVal);
			} else {
				calcString.append(Integer.toHexString(asciiVal));
			}
		}

		return calcString.toString();
	}

	/**
	 * Iterates over all of the information to make the files table.
	 * 
	 * @param sink
	 *            the sink used for writing
	 */
	private void makeLinks(final Sink sink) {
		sink.table();

		sink.tableRow();
		sink.tableHeaderCell();
		sink.text("Relative Filename");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("Number Passed");
		sink.tableHeaderCell_();

		sink.tableHeaderCell();
		sink.text("Number Failed");
		sink.tableHeaderCell_();

		sink.tableRow_();
		for (TestCase testCase : testCases) {
			// TODO create the output dump for later review of metrics and such.
			createRow(testCase, sink);
		}
		sink.table_();
	}

	/**
	 * Create a single row in the files table.
	 * 
	 * @param testCase
	 *            the test case to output to the table
	 * @param sink
	 *            the sink used for writing
	 */
	private void createRow(final TestCase testCase, final Sink sink) {
		// Output
		sink.tableRow();
		// Relative Filename
		sink.tableCell();

		// link to anchored section below
		sink.link("#"
				+ encodeAnchor(testCase.getSummary().getRelativeLocation()));
		sink.text(testCase.getSummary().getRelativeLocation());
		sink.link_();
		sink.tableCell_();

		// Number Passed
		sink.tableCell();
		sink.text(String.valueOf(testCase.getFailureStatistic().getNumPassed()));
		sink.link_();
		sink.tableCell_();

		// Number Failed
		sink.tableCell();
		sink.text(String.valueOf(testCase.getFailureStatistic().getNumFailed()));
		sink.link_();
		sink.tableCell_();

		sink.tableRow_();
	}
}
