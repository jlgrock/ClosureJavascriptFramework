package com.github.jlgrock.javascriptframework.closuretesting;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.ParseRunner;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.TestResultType;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.generators.SuiteGenerator;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCase;
import com.github.jlgrock.javascriptframework.mavenutils.io.DirectoryIO;
import com.github.jlgrock.javascriptframework.mavenutils.logging.MojoLogAppender;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.JsarRelativeLocations;
import com.github.jlgrock.javascriptframework.mavenutils.pathing.FileListBuilder;

/**
 * Will execute the jsclosure suite in a selenium testbed and execute it,
 * parsing values for problems. If problems arise, this can stop the build.
 * 
 * @goal js-closure-test
 * @phase test
 */
public class ClosureTestingMojo extends AbstractClosureTestingMojo {

	/**
	 * Whether or not to print all values of the test cases. This will show for
	 * passing and failing tests.
	 * 
	 * @parameter default-value="false"
	 */
	private boolean verbose;

	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(ClosureTestingMojo.class);

	@Override
	public final void execute() throws MojoExecutionException,
			MojoFailureException {
		MojoLogAppender.beginLogging(this);
		try {
			Set<File> files = generateFiles();
			if (!isSkipTests()) {
				Set<TestCase> testCases = parseFiles(files);
				boolean encounteredError = checkForFailuresInTestCases(testCases);
				if (verbose) {
					printAllRecords(testCases);
				}
				if (encounteredError && !verbose) {
					printFailures(testCases);
				}
				if (encounteredError) {
					throw new MojoFailureException("There were test case failures.");
				}
			}
		} catch (MojoFailureException mje) {
			throw mje;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new MojoExecutionException(e.getMessage());
		} finally {
			MojoLogAppender.endLogging();
		}
	}

	/**
	 * Check over the parsed Closure Testing objects and determine if an error
	 * has occured.
	 * 
	 * @param testCases
	 *            the test cases to examine
	 * @return true if any test case has failed, otherwise false
	 */
	private boolean checkForFailuresInTestCases(final Set<TestCase> testCases) {
		for (TestCase testCase : testCases) {
			if (testCase.getSummary() == null
					|| testCase.getSummary().getResult()
							.equals(TestResultType.FAILED)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Print all of the records. Good for debugging.
	 * 
	 * @param testCases
	 *            the test cases to output
	 */
	private void printAllRecords(final Set<TestCase> testCases) {
		StringBuffer sb = new StringBuffer();
		for (TestCase testCase : testCases) {
			sb.append(testCase.toString());
		}
		LOGGER.info(sb.toString());
	}

	/**
	 * Print failures. Will be done whenever an error is encountered.
	 * 
	 * @param testCases
	 *            the test cases to examine and print (if there is a failure)
	 */
	private void printFailures(final Set<TestCase> testCases) {
		StringBuffer sb = new StringBuffer();
		for (TestCase testCase : testCases) {
			if (testCase.getSummary() == null
					|| testCase.getSummary().getResult()
							.equals(TestResultType.FAILED)) {
				sb.append(testCase.toString());
			}
		}
		LOGGER.error(sb.toString()); //TODO if var is not defined, it doesn't parse?
	}

	/**
	 * Will generate the files used in the google testing.
	 * 
	 * @return the set of files that were created
	 * @throws IOException
	 *             if there is a problem reading or writing to the files
	 */
	private Set<File> generateFiles() throws IOException {
		File testOutputDir = JsarRelativeLocations.getTestSuiteLocation(getFrameworkTargetDirectory());
		File testDepsDir = JsarRelativeLocations.getTestLocation(getFrameworkTargetDirectory());
		File depsFileLocation = JsarRelativeLocations.getTestDepsLocation(getFrameworkTargetDirectory());
		Set<File> returnFiles = new HashSet<File>();
		
		DirectoryIO.recursivelyDeleteDirectory(testOutputDir);
		File baseLocation = new File(getClosureLibrarylocation()
				.getAbsoluteFile()
				+ File.separator
				+ "closure"
				+ File.separator + "goog" + File.separator + "base.js");

		LOGGER.info("Generating Test Suite");
		Set<File> fileSet = calculateFileSet();
		Set<File> testDeps = FileListBuilder.buildFilteredList(testDepsDir, "js");
		Set<File> depsFileSet = FileListBuilder.buildFilteredList(depsFileLocation, "js");
		File depsFile = null;
		if (depsFileSet.size() == 1) {
			depsFile = depsFileSet.toArray(new File[depsFileSet.size()])[0];
		} else {
			throw new IOException("Could not find debug/deps file (or found more than one) at location '"
					+ depsFileLocation + "'.");
		}
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Files that will be included in testing:" + fileSet);
			LOGGER.debug("Base Location:" + baseLocation);
			LOGGER.debug("calc deps Location:" + depsFile);
			LOGGER.debug("Testing Dependency Location:" + testDepsDir);
			LOGGER.debug("Testing Source Directory:" + getTestSourceDirectory());
		}
		
		
		SuiteGenerator suite = new SuiteGenerator(fileSet, baseLocation,
				depsFile, testDeps, getPreamble(), getPrologue(), getEpilogue());
		
		returnFiles.addAll(suite.generateTestFiles(getTestSourceDirectory(), testOutputDir));

		if (isRunTestsOnCompiled()) {
			File testCompiledOutputDir = JsarRelativeLocations.getCompiledTestSuiteLocation(getFrameworkTargetDirectory());
			File compiledFile = new File(
					JsarRelativeLocations
					.getCompileLocation(getFrameworkTargetDirectory()),
			getCompiledFilename());
			
			SuiteGenerator suiteCompiled = new SuiteGenerator(fileSet, baseLocation,
					compiledFile, testDeps, getPreamble(), getPrologue(), getEpilogue());
			returnFiles.addAll(suiteCompiled.generateTestFiles(getTestSourceDirectory(), testCompiledOutputDir));
		}

		for (File file : returnFiles) {
			LOGGER.debug("filename: " + file.getAbsolutePath());
		}
		LOGGER.debug("baseLocation: " + baseLocation.getAbsolutePath());
		LOGGER.debug("testOutputDir: " + testOutputDir.getAbsolutePath());
		
		return returnFiles;
	}

	/**
	 * Parse the files created.
	 * 
	 * @param files
	 *            the files to parse
	 * @return the set of test cases received from parsing
	 */
	private static Set<TestCase> parseFiles(final Set<File> files) {
		WebDriver driver = new HtmlUnitDriver(true);
		
		Set<TestCase> testCases = null;
		try {
			ParseRunner parseRunner = new ParseRunner(files, driver);
			testCases = parseRunner.parseFiles();
		} finally {
			driver.quit();
		}
		return testCases;
	}

	/**
	 * Will calculate the set of files.
	 * 
	 * @return the file set
	 */
	private Set<File> calculateFileSet() {
		LOGGER.info("Calculating File Set");
		Set<File> files = new HashSet<File>();
		files.addAll(FileListBuilder.buildFilteredList(
				getTestSourceDirectory(), "js"));
		if (getIncludes() != null) {
			files.addAll(getIncludes());
		}
		if (getExcludes() != null) {
			files.removeAll(getExcludes());
		}
		return files;
	}
}
