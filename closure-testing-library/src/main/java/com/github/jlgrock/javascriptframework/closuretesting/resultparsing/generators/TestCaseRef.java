package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.generators;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;

import com.github.jlgrock.javascriptframework.mavenutils.pathing.RelativePath;

/**
 * The object to store and write the test case.
 */
public class TestCaseRef {
	/**
	 * Constant property to store preamble.
	 */
	public static final String PREAMBLE_PROPERTY = "jsframework.testing.preamble";

	/**
	 * Constant property to store epilogue.
	 */
	public static final String EPILOGUE_PROPERTY = "jsframework.testing.epilogue";

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
	 * A set of files that will serve as script tag includes to the test case.
	 */
	private final List<File> testDeps;

	/**
	 * The begin tag for html.
	 */
	private static final String BEGIN_HTML = "<html>\n";

	/**
	 * The end tag for html.
	 */
	private static final String END_HTML = "</html>";

	/**
	 * The begin tag for the head.
	 */
	private static final String BEGIN_HEAD = "\t<head>\n";

	/**
	 * The beginning of the title.
	 */
	private static final String BEGIN_TITLE = "\t\t<title>Test for ";

	/**
	 * The end of the title.
	 */
	private static final String END_TITLE = "</title>\n";

	/**
	 * The end tag for the head.
	 */
	private static final String END_HEAD = "\t</head>\n";
	/**
	 * The begin tag for body.
	 */
	private static final String BEGIN_BODY = "\t<body>\n";
	/**
	 * The end tag for body.
	 */
	private static final String END_BODY = "\t</body>\n";
	/**
	 * The begin tag for script.
	 */
	private static final String BEGIN_SCRIPT = "\t\t<script src=\"";
	/**
	 * The end tag for script.
	 */
	private static final String END_SCRIPT = "\"></script>\n";

	/**
	 * The preamble for the test.
	 */
	private final String preamble;

	/**
	 * The prologue for the test.
	 */
	private final String prologue;

	/**
	 * The epilogue for the test.
	 */
	private final String epilogue;

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
	 * @param testDepsIn
	 *            the set of files that will serve as script tag includes to the test case
	 * @param preambleIn
	 * 			  the preamble to the test case
	 * @param prologueIn
	 * 			  the prologue to the test case
	 * @param epilogueIn
	 * 			  the epilogue to the test case
	 */
	public TestCaseRef(final File closureLocation, final File depsLocation,
			final File testFile, final File testCase, final List<File> testDepsIn,
			final String preambleIn, final String prologueIn, final String epilogueIn) {
		closureBaseLocation = closureLocation;
		testCaseFileLocation = testCase;
		dependencyLocation = depsLocation;
		testFileLocation = testFile;
		testDeps = testDepsIn;
		preamble = preambleIn;
		prologue = prologueIn;
		epilogue = epilogueIn;
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
			bufferedWriter.write(BEGIN_HEAD);

			// preamble
			if (preamble != null && !preamble.isEmpty()) {
                bufferedWriter.write(preamble);
            }

            // name of the test case
			bufferedWriter.write(BEGIN_TITLE);
			String title = StringEscapeUtils
					.escapeHtml(RelativePath.getRelPathFromBase(
							testCaseFileLocation, testFileLocation));
			bufferedWriter.write(title);
			bufferedWriter.write(END_TITLE);

			bufferedWriter.write(END_HEAD);
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
			
			// prologue
            if (prologue != null && !prologue.isEmpty()) {
                bufferedWriter.write(prologue);
            }


			// test case
			bufferedWriter.write(BEGIN_SCRIPT);
			bufferedWriter.write(RelativePath.getRelPathFromBase(
					testCaseFileLocation, testFileLocation));
			bufferedWriter.write(END_SCRIPT);

			// epilogue 
            if (epilogue != null && !epilogue.isEmpty()) {
                bufferedWriter.write(this.epilogue);
            }

			bufferedWriter.write(END_BODY);
			bufferedWriter.write(END_HTML);
		} finally {
			IOUtils.closeQuietly(bufferedWriter);
		}
	}
}
