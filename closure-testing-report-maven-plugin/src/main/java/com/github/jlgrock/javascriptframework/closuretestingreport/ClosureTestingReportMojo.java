package com.github.jlgrock.javascriptframework.closuretestingreport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.ParseRunner;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.TestResultType;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.TestUnitDriver;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCase;
import com.github.jlgrock.javascriptframework.mavenutils.logging.MojoLogAppender;
import com.github.jlgrock.javascriptframework.mavenutils.pathing.FileListBuilder;

/**
 * Will execute the jsclosure suite in a selenium testbed and execute it,
 * parsing values for problems. If problems arise, this can stop the build.
 * 
 * @goal report
 * @phase site
 * @execute phase="test"
 */
public class ClosureTestingReportMojo extends AbstractMavenReport {
	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(ClosureTestingReportMojo.class);

	/**
	 * The framework directory.  This allows us to get the relative pathing to the expected generated source
	 * 
	 * @parameter expression=
	 *            "${project.build.directory}${file.separator}javascriptFramework"
	 * @required
	 */
	private File frameworkTargetDirectory;

	/**
	 * Directory where reports will go.
	 * 
	 * @parameter expression="${project.reporting.outputDirectory}"
	 * @required
	 * @readonly
	 */
	private String outputDirectory;

	/**
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * @component
	 * @required
	 * @readonly
	 */
	private Renderer siteRenderer;

	/**
	 * Add this if you want to create the report without using Doxia, e.g. via
	 * XSL transformation from some XML file, then simply add the following
	 * method to your Mojo
	 * 
	 * @return
	 */

	/**
	 * Gets the description for the report.
	 * 
	 * @param locale
	 *            the locale
	 * @return the description
	 */
	public final String getDescription(final Locale locale) {
		return "Report on Closure Unit Testing";
	}

	@Override
	public final String getName(final Locale locale) {
		return "Closure Unit Testing";
	}

	@Override
	public final String getOutputName() {
		return "closure-unit-testing-report";
	}

	@Override
	protected final void executeReport(final Locale locale)
			throws MavenReportException {
		MojoLogAppender.beginLogging(this);
		try {
			List<File> files = FileListBuilder.buildFilteredList(getFrameworkTargetDirectory(),
					"html");
			// Reporting will always show all failures
			List<TestCase> testCases = parseFiles(files, -1);
			ClosureTestingReportGenerator renderer = new ClosureTestingReportGenerator(
					getSink(), testCases);
			renderer.render();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new MavenReportException(e.getMessage());
		} finally {
			MojoLogAppender.endLogging();
		}
	}

	/**
	 * * Parse the files created.
	 * 
	 * @param files
	 *            the files to parse
	 * @param maxFailures
	 *            the maximum number of failures to allow during the parsing.
	 * @return the set of parsed test cases
	 */
	private static List<TestCase> parseFiles(final List<File> files,
			final int maxFailures) {
		TestUnitDriver driver = new TestUnitDriver(true);
		List<TestCase> testCases = new ArrayList<TestCase>();
		try {
			ParseRunner parseRunner = new ParseRunner(driver);
			for (File file : files) {
				TestCase testCase = parseRunner.parseFile(file);
				if (!testCase.getResult().equals(TestResultType.PASSED)) {
					testCases.add(testCase);
				}
				if (testCases.size() > maxFailures && maxFailures != -1) {
					break;
				}
			}
		} finally {
			driver.quit();
		}
		return testCases;
	}

	@Override
	protected final String getOutputDirectory() {
		return outputDirectory;
	}

	@Override
	protected final MavenProject getProject() {
		return project;
	}

	@Override
	protected final Renderer getSiteRenderer() {
		return siteRenderer;
	}
	
	/**
	 * @return frameworkTargetDirectory
	 */
	protected final File getFrameworkTargetDirectory() {
		return frameworkTargetDirectory;
	}

}
