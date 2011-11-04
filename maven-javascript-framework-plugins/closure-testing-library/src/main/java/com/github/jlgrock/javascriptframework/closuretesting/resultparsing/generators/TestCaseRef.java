package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.generators;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;

import com.github.jlgrock.javascriptframework.mavenutils.pathing.RelativePath;

/**
 * The object to store and write the test case.
 */
public class TestCaseRef {
	/**
	 * The location of the closure base file.
	 */
	private final File closureBaseLocation;
	/**
	 * The location of the test case output file.
	 */
	private final File testCaseFileLocation;
	/**
	 * The location of the dependency file.
	 */
	private final File dependencyLocation;
	/**
	 * The location of the test file.
	 */
	private final File testFileLocation;

	/**
	 * A set of files that will serve as script tag includes to the test case
	 */
	private final Set<File> testDeps;

	/**
	 * The begin tag for html.
	 */
	private static final String BEGIN_HTML = "<html>";

	/**
	 * The end tag for html.
	 */
	private static final String END_HTML = "</html>";

	/**
	 * The begin tag for heading with title.
	 */
	private static final String BEGIN_HEADING = "<head><title>Test for ";

	/**
	 * The end tag for heading with title.
	 */
	private static final String END_HEADING = "</title></head>";
	/**
	 * The begin tag for body.
	 */
	private static final String BEGIN_BODY = "<body>";
	/**
	 * The end tag for body.
	 */
	private static final String END_BODY = "</body>";
	/**
	 * The begin tag for script.
	 */
	private static final String BEGIN_SCRIPT = "<script src=\"";
	/**
	 * The end tag for script.
	 */
	private static final String END_SCRIPT = "\"></script>";

	/**
	 * The Constructor.
	 * 
	 * @param closureLocation
	 *            the location of the closure library
	 * @param depsLocation
	 *            the location of the dependency file
	 * @param testFile
	 *            the location of the test file
	 * @param testCase
	 *            the file to be written
	 */
	public TestCaseRef(final File closureLocation, final File depsLocation,
			final File testFile, final File testCase, final Set<File> testDepsIn) {
		closureBaseLocation = closureLocation;
		testCaseFileLocation = testCase;
		dependencyLocation = depsLocation;
		testFileLocation = testFile;
		testDeps = testDepsIn;
	}

	/**
	 * The method that will begin writing to the file.
	 * 
	 * @throws IOException
	 *             if there is a problem writing to the file
	 */
	public final void writeToFile() throws IOException {
		// create the directories if they haven't been made already
		// TODO what if this is at the root? side case to eventually handle
		testCaseFileLocation.getParentFile().mkdirs();

		FileWriter fileWriter = new FileWriter(testCaseFileLocation);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		try {
			bufferedWriter.write(BEGIN_HTML);

			// name of the test case
			bufferedWriter.write(BEGIN_HEADING);
			String title = StringEscapeUtils
					.escapeHtml(RelativePath.getRelPathFromBase(
							testCaseFileLocation, testFileLocation));
			bufferedWriter.write(title);
			bufferedWriter.write(END_HEADING);

			bufferedWriter.write(BEGIN_BODY);

			// test requires
			for (File testDep : testDeps) {
				bufferedWriter.write(BEGIN_SCRIPT);
				bufferedWriter.write(RelativePath.getRelPathFromBase(
					testCaseFileLocation, testDep));
				bufferedWriter.write(END_SCRIPT);
			}
			
			// goog.base script
			bufferedWriter.write(BEGIN_SCRIPT);
			bufferedWriter.write(RelativePath.getRelPathFromBase(
					testCaseFileLocation, closureBaseLocation));
			bufferedWriter.write(END_SCRIPT);
			
			// deps script
			bufferedWriter.write(BEGIN_SCRIPT);
			bufferedWriter.write(RelativePath.getRelPathFromBase(
					testCaseFileLocation, dependencyLocation));
			bufferedWriter.write(END_SCRIPT);
			
			// test case
			bufferedWriter.write(BEGIN_SCRIPT);
			bufferedWriter.write(RelativePath.getRelPathFromBase(
					testCaseFileLocation, testFileLocation));
			bufferedWriter.write(END_SCRIPT);

			bufferedWriter.write(END_BODY);
			bufferedWriter.write(END_HTML);
		} finally {
			IOUtils.closeQuietly(bufferedWriter);
		}
	}
}
