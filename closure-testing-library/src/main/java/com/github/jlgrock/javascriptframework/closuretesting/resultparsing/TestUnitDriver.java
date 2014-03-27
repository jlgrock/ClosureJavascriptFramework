package com.github.jlgrock.javascriptframework.closuretesting.resultparsing;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;

/**
 * A specific version of the HTML Unit Driver that will store an exception for
 * later use.
 */
public class TestUnitDriver extends HtmlUnitDriver {

	/** Internal name constant for chrome browser. */
	private static final String CHROME = "CHROME";

	/** Internal name constant for firefox browser. */
	private static final String FIREFOX = "FIREFOX";

	/** Internal name constant for firefox browser. */
	private static final String FIREFOX_ABBREVIATION = "FF";

	/** Internal name constant for internet explorer browser. */
	private static final String INTERNET_EXPLORER = "INTERNET EXPLORER";

	/** Internal name constant for internet explorer browser. */
	private static final String INTERNET_EXPLORER_ABBREVIATION = "IE";

	/**
	 * Returns {@link com.gargoylesoftware.htmlunit.BrowserVersion} instance
	 * whose version is derived from input string parameter. Supported input
	 * values are: <code>CHROME</code>, <code>FIREFOX</code> (or <code>FF</code>)
	 * and <code>INTERNET EXPLORER</code> (or <code>IE</code>). If it can not
	 * parse input value a default version is returned.
	 *
	 * @param value string value representing target browser version
	 * @return always return BrowserVersion instance and never return null
	 */
	public static BrowserVersion getBrowserVersionSafe(final String value) {
		// This is a bit hack-ish implementation, it depends a bit
		// on internal implementation of HtmlUnit and specific
		// version of it required by selenium-htmlunit-driver.
		// After each upgrade of HtmlUnit it is recommended to
		// check we do not return deprecated browser versions.
		if (value == null || value.trim().isEmpty()) {
			return BrowserVersion.getDefault();
		} else if (CHROME.equals(value.trim().toUpperCase(Locale.ENGLISH))) {
			return BrowserVersion.CHROME;
		} else if (FIREFOX.equals(value.trim().toUpperCase(Locale.ENGLISH)) ||
				FIREFOX_ABBREVIATION.equals(value.trim().toUpperCase(Locale.ENGLISH))) {
			return BrowserVersion.FIREFOX_17;
		} else if (INTERNET_EXPLORER.equals(value.trim().toUpperCase(Locale.ENGLISH)) ||
				INTERNET_EXPLORER_ABBREVIATION.equals(value.trim().toUpperCase(Locale.ENGLISH))) {
			return BrowserVersion.INTERNET_EXPLORER_10;
		} else {
			return BrowserVersion.getDefault();
		}
	}

	/**
	 * Used to define the error to the user when there is a timeout.
	 */
	private static final String TIMEOUT_PREFIX = "execution time exceeded maximum amount of time of ";
	
	/**
	 * A variable for storing the first exception encountered.
	 */
	private Exception exception = null;

	/**
	 * The default constructor, which just calls the HTMLUnitDriver constructor.
	 * @param enableJavascript whether or not to enable JavaScript in the driver
	 * @param browserVersion requested version of browser
	 * @see TestUnitDriver#getBrowserVersionSafe(String)
	 */
	public TestUnitDriver(final boolean enableJavascript, final BrowserVersion browserVersion) {
		super(browserVersion);
		setJavascriptEnabled(enableJavascript);
	}

	/**
	 * Set the exception. If one is already set, setting will be skipped.
	 * 
	 * @param exceptionIn
	 *            the exception to set.
	 */
	public final void setException(final Exception exceptionIn) {
		exception = exceptionIn;
	}

	/**
	 * Get the first exception that was set.
	 * 
	 * @return the exception, or null if none were set.
	 */
	public final Exception getException() {
		return exception;
	}

	/**
	 * Override the default behavior of the unit driver to provide credentials
	 * or alternate behaviors.
	 * @param client the client to modify
	 * @return the modified client
	 */
	protected final WebClient modifyWebClient(final WebClient client) {
		client.getOptions().setThrowExceptionOnScriptError(true);
		client.getOptions().setThrowExceptionOnFailingStatusCode(true);
		JavaScriptErrorListener javaScriptErrorListener = new JavaScriptErrorListener() {

			@Override
			public void scriptException(final HtmlPage htmlPage,
					final ScriptException scriptException) {
				setException(scriptException);

			}

			@Override
			public void timeoutError(final HtmlPage htmlPage,
					final long allowedTime, final long executionTime) {
				setException(new TimeoutException(
						TIMEOUT_PREFIX + String.valueOf(allowedTime)));
			}

			@Override
			public void malformedScriptURL(final HtmlPage htmlPage,
					final String url,
					final MalformedURLException malformedURLException) {
				setException(malformedURLException);
			}

			@Override
			public void loadScriptError(final HtmlPage htmlPage,
					final URL scriptUrl, final Exception exceptionIn) {
				setException(exceptionIn);
			}

		};
		client.setJavaScriptErrorListener(javaScriptErrorListener);
		return client;
	}
}
