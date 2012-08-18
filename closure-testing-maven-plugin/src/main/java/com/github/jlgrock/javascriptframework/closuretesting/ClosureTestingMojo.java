package com.github.jlgrock.javascriptframework.closuretesting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.ParseRunner;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.TestResultType;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.TestUnitDriver;
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
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(ClosureTestingMojo.class);

	@Override
	public final void execute() throws MojoExecutionException,
			MojoFailureException {
		MojoLogAppender.beginLogging(this);
		try {
			List<File> files = generateFiles();
			if (!isSkipTests()) {
				List<TestCase> testCases = parseFiles(files,
						getMaximumFailures());
				
				//Encountered Error(s)
				if (testCases.size() > 0) {
					printFailures(testCases);
					throw new MojoFailureException(
							"There were test case failures.");
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
	 * Print failures. Will be done whenever an error is encountered.
	 * 
	 * @param testCases
	 *            the test cases to examine and print (if there is a failure)
	 */
	private void printFailures(final List<TestCase> testCases) {
		StringBuffer sb = new StringBuffer();
		for (TestCase testCase : testCases) {
			if (!testCase.getResult().equals(TestResultType.PASSED)) {
				sb.append(testCase.toString());
			}
		}
		LOGGER.error(sb.toString());
	}

	/**
	 * Will generate the files used in the google testing.
	 * 
	 * @return the set of files that were created
	 * @throws IOException
	 *             if there is a problem reading or writing to the files
	 */
	private List<File> generateFiles() throws IOException {
		File testOutputDir = JsarRelativeLocations
				.getTestSuiteLocation(getFrameworkTargetDirectory());
		File testDepsDir = JsarRelativeLocations
				.getTestLocation(getFrameworkTargetDirectory());
		File depsFileLocation = JsarRelativeLocations
				.getTestDepsLocation(getFrameworkTargetDirectory());
		List<File> returnFiles = new ArrayList<File>();

		DirectoryIO.recursivelyDeleteDirectory(testOutputDir);
		File baseLocation = new File(getClosureLibrarylocation()
				.getAbsoluteFile()
				+ File.separator
				+ "closure"
				+ File.separator + "goog" + File.separator + "base.js");

		LOGGER.info("Generating Test Suite...");
		List<File> fileSet = calculateFileSet();
		List<File> testDeps = FileListBuilder.buildFilteredList(testDepsDir,
				"js");
		List<File> depsFileSet = FileListBuilder.buildFilteredList(
				depsFileLocation, "js");
		File depsFile = null;
		if (depsFileSet.size() == 1) {
			depsFile = depsFileSet.toArray(new File[depsFileSet.size()])[0];
		} else {
			throw new IOException(
					"Could not find debug/deps file (or found more than one) at location '"
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

		returnFiles.addAll(suite.generateTestFiles(getTestSourceDirectory(),
				testOutputDir));

		if (isRunTestsOnCompiled()) {
			File testCompiledOutputDir = JsarRelativeLocations
					.getCompiledTestSuiteLocation(getFrameworkTargetDirectory());
			File compiledFile = new File(
					JsarRelativeLocations
							.getCompileLocation(getFrameworkTargetDirectory()),
					getCompiledFilename());

			SuiteGenerator suiteCompiled = new SuiteGenerator(fileSet,
					baseLocation, compiledFile, testDeps, getPreamble(),
					getPrologue(), getEpilogue());
			returnFiles.addAll(suiteCompiled.generateTestFiles(
					getTestSourceDirectory(), testCompiledOutputDir));
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
	 * @param maxFailures
	 *            the maximum number of failures to allow during the parsing.
	 * @return the set of test cases received from parsing
	 */
	private static List<TestCase> parseFiles(final List<File> files,
			final int maxFailures) {
		LOGGER.info("Parsing Test Files...");
		TestUnitDriver driver = new TestUnitDriver(true);
		
		List<TestCase> testCases = new ArrayList<TestCase>();
		//This list will contain more more test cases than is specified in maxFailures.
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
			//driver.close()
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
	private List<File> calculateFileSet() {
		LOGGER.info("Calculating File Set...");
		List<File> files = new ArrayList<File>();
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
