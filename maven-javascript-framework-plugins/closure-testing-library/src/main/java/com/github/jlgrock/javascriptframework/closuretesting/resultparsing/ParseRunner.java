package com.github.jlgrock.javascriptframework.closuretesting.resultparsing;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers.TestCaseParser;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCase;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCaseSummary;

/**
 * Parse all of the test cases to test case objects.
 */
public class ParseRunner {
	/**
	 * The maximum time to wait for the page to load.
	 */
	private static final int MAX_TIME_TO_WAIT_FOR_LOAD = 10;

	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(ParseRunner.class);

	/**
	 * The files to parse.
	 */
	private Set<File> filesToParse;

	/**
	 * The driver to use to execute the web pages.
	 */
	private WebDriver driver;

	/**
	 * Constructor.
	 * 
	 * @param files
	 *            the files to parse
	 * @param webDriver
	 *            the webdriver to execute the web pages
	 */
	public ParseRunner(final Set<File> files, final WebDriver webDriver) {
		this.filesToParse = files;
		this.driver = webDriver;
	}

	/**
	 * Parse the files.
	 * 
	 * @return the set of parsed test cases
	 */
	public final Set<TestCase> parseFiles() {
		Set<TestCase> testCases = new HashSet<TestCase>();
		for (File fileToParse : filesToParse) {
			boolean encounteredError = false;
			try {
				String uri = fileToParse.toURI().toString();
				LOGGER.debug("parsing file: " + uri);
				driver.get(uri);
				(new WebDriverWait(driver, MAX_TIME_TO_WAIT_FOR_LOAD))
						.until(new ExpectedCondition<WebElement>() {
							@Override
							public WebElement apply(final WebDriver d) {
								return d.findElement(By
										.linkText("Run again without reloading"));
							}
						});
			} catch (Exception e) {
				LOGGER.error("The test Runner was unable to receive standardized "
						+ "Google Testing results for the file at location: "
						+ fileToParse.getAbsolutePath()
						+ ".  Please check test case to ensure that google testing "
						+ "dependencies and the file that is being tested have been properly required.  "
						+ "Note that making changes to the test case doesn't require a rerunof within maven."
						+ "You can just execute the html generated in \"target/testSuite\" "
						+ "in a browser to determine results.");

				encounteredError = true;
			}
			WebElement body = driver.findElement(By.tagName("body"));
			TestCaseParser testCaseParser = new TestCaseParser();

			// if the web driver was unable to execute the page, mark the test
			// case as a failure, otherwise parse results
			TestCase testCase = null;
			if (encounteredError) {
				testCase = new TestCase();
				testCase.setSummary(new TestCaseSummary("",
						TestResultType.FAILED));
			} else {
				testCase = testCaseParser.parse(body);
			}
			testCases.add(testCase);

		}
		return testCases;
	}

	/**
	 * Parse the files.
	 * 
	 * @return the set of parsed test cases
	 */
	public final Set<String> parseDump() {
		Set<String> testCaseResults = new HashSet<String>();
		for (File fileToParse : filesToParse) {
			String uri = fileToParse.toURI().toString();
			LOGGER.debug("parsing file: " + uri);
			driver.get(uri);
			(new WebDriverWait(driver, MAX_TIME_TO_WAIT_FOR_LOAD))
					.until(new ExpectedCondition<WebElement>() {
						@Override
						public WebElement apply(final WebDriver d) {
							return d.findElement(By
									.linkText("Run again without reloading"));
						}
					});

			WebElement body = driver.findElement(By.tagName("body"));
			testCaseResults.add(body.getText());
		}
		return testCaseResults;
	}
}
