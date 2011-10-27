package com.github.jlgrock.javascriptframework.closuretesting.resultparsing;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers.TestCaseParser;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCase;



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
	private static final Logger LOGGER = Logger.getLogger( ParseRunner.class );
	
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
	 * @param files the files to parse
	 * @param webDriver the webdriver to execute the web pages
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
			String uri = fileToParse.toURI().toString();
			LOGGER.debug("parsing file: " + uri);
			driver.get(uri);
			(new WebDriverWait(driver, MAX_TIME_TO_WAIT_FOR_LOAD))
			  .until(new ExpectedCondition<WebElement>(){
				@Override
				public WebElement apply(final WebDriver d) {
					return d.findElement(By.linkText("Run again without reloading"));
				}});
	
			WebElement body = driver.findElement(By.tagName("body"));
			TestCaseParser testCaseParser = new TestCaseParser();
			testCases.add(testCaseParser.parse(body));
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
			  .until(new ExpectedCondition<WebElement>(){
				@Override
				public WebElement apply(final WebDriver d) {
					return d.findElement(By.linkText("Run again without reloading"));
				}});
	
			WebElement body = driver.findElement(By.tagName("body"));
			testCaseResults.add(body.getText());
		}
		return testCaseResults;
	}
}
