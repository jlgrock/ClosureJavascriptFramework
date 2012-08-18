package com.github.jlgrock.javascriptframework.closuretesting.resultparsing;

import java.net.MalformedURLException;
import java.net.URL;

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
	/**
	 * A variable for storing the first exception encountered.
	 */
	private Exception exception = null;

	/**
	 * The default constructor, which just calls the HTMLUnitDriver constructor.
	 * @param enableJavascript whether or not to enable JavaScript in the driver
	 */
	public TestUnitDriver(final boolean enableJavascript) {
		super(enableJavascript);
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
		client.setThrowExceptionOnScriptError(true);
		client.setThrowExceptionOnFailingStatusCode(true);
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
						"execution time exceeded maximum amount of time of "
								+ String.valueOf(allowedTime) + "."));
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
