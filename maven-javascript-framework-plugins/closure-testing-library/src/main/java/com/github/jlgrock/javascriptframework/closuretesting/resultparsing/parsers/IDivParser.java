package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.parsers;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.IParsedDivObject;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCase;

/**
 * An interface that defines what is necessary for matching a div from the
 * google testing output and parsing it.
 * 
 */
public interface IDivParser {
	/**
	 * @param testCase
	 *            the testCase being parsed. This is used to determine what has
	 *            already been parsed, giving a fair idea of the positioning.
	 * @param divText
	 *            the text within the div to parse
	 * @return true if the pattern to match within the document.
	 */
	boolean matches(final TestCase testCase, final String divText);

	/**
	 * The process that will parse the text from a particular div.
	 * 
	 * @param divText
	 *            the text to parse
	 * @return the parsed object
	 */
	IParsedDivObject parse(final String divText);
}
