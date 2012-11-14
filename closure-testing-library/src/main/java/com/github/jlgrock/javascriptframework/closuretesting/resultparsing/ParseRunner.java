package com.github.jlgrock.javascriptframework.closuretesting.resultparsing;

import java.io.File;
import java.net.MalformedURLException;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers.TestCaseParser;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCase;

/**
 * Parse all of the test cases to test case objects.
 */
public class ParseRunner {
	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(ParseRunner.class);

	/**
	 * The driver to use to execute the web pages.
	 */
	private TestUnitDriver driver;

	/**
	 * The configurable amount of time to test an individual file.
	 */
	private long testTimeoutSeconds;

	/**
	 * Constructor.
	 * 
	 * @param webDriver
	 *            the webdriver to execute the web pages
	 * @param testTimeoutSeconds 
	 */
	public ParseRunner(final TestUnitDriver webDriver, long testTimeoutSeconds) {
		this.driver = webDriver;
		this.testTimeoutSeconds = testTimeoutSeconds;
	}

	/**
	 * Parse the files.
	 * 
	 * @param fileToParse
	 *            File to parse
	 * @return the set of parsed test cases
	 */
	public final TestCase parseFile(final File fileToParse) {
		TestCase testCase = null;
		
		boolean encounteredError = false;
		try {
			driver.setException(null);
			String uri = fileToParse.toURI().toString();
			LOGGER.debug("parsing file: " + uri);
			driver.get(uri);
			(new WebDriverWait(driver, testTimeoutSeconds))
					.until(new ExpectedCondition<WebElement>() {
						@Override
						public WebElement apply(final WebDriver d) {
							return d.findElement(By
									.linkText("Run again without reloading"));
						}
					});
			if (driver.getException() != null) {
				throw driver.getException();
			}
		} catch (TimeoutException te) {
			testCase = new TestCase(fileToParse,
					TestResultType.TIMED_OUT, te.getMessage());
			encounteredError = true;
		} catch (NotFoundException nfe) {
			testCase = new TestCase(fileToParse,
					TestResultType.BAD_OUTPUT, nfe.getMessage());
			encounteredError = true;
		} catch (WebDriverException se) {
			testCase = new TestCase(fileToParse,
					TestResultType.SCRIPT_ERROR, se.getMessage());
			encounteredError = true;
		} catch (ScriptException se) {
			testCase = new TestCase(fileToParse,
					TestResultType.SCRIPT_ERROR, se.getMessage());
			encounteredError = true;
		} catch (MalformedURLException mue) {
			testCase = new TestCase(fileToParse,
					TestResultType.SCRIPT_ERROR, mue.getMessage());
			encounteredError = true;
		} catch (Exception e) {
			testCase = new TestCase(fileToParse,
					TestResultType.UNABLE_TO_EXECUTE, e.getMessage());
			encounteredError = true;
		}
		WebElement body = driver.findElement(By.tagName("body"));
		TestCaseParser testCaseParser = new TestCaseParser(fileToParse);

		// if the web driver was unable to execute the page, mark the test
		// case as a failure, otherwise parse results
		if (!encounteredError) {
			testCase = testCaseParser.parse(body);
		}
		return testCase;
	}
	
	/**
	 * Closes all test resources required by this parse runner.
	 */
	public void quit() {
		driver.quit();
	}
}
