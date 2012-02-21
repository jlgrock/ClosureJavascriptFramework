package com.github.jlgrock.javascriptframework.jspreprocessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.github.jlgrock.javascriptframework.mavenutils.io.DirectoryIO;

/**
 * eventually, this should do a bunch of things, but right now, it will just
 * copy from src/main/javascript to /target/javascript-generated and replace any
 * references to $$assert.
 * 
 * @author <a href="mailto:grantjl@umich.edu">Justin Grant</a>
 * @requiresProject
 * @goal compile-preprocessors
 * @threadSafe
 */
public class CompilePreProcessor extends AbstractPreProcessorFrameworkMojo {
	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(CompilePreProcessor.class);

	/**
	 * The pattern that matches requires, provides, and assignment operators.
	 * Basically, the only ways you should be using $assert statements other
	 * than calling it directly. This is used to ignore these types.
	 */
	public static final String ASSERT_REQUIRE_REGEX_STR = "(\\s*\\$\\$assert\\s*=.*)|"
			+ "(.*=\\s\\$\\$assert.*)|(.*?goog.require\\s*?\\(\\s*[\\\"']"
			+ "\\$\\$assert[\\\"']\\s*\\).*)|(.*?goog.provide\\s*?\\(\\s*[\\\"']\\$\\$assert[\\\"']\\s*\\).*)";

	/**
	 * The regular expression to find assert statements on a string.
	 */
	public static final String ASSERT_REGEX_STR = ".*?(\\$\\$assert).*";

	/**
	 * The pattern based off of the ASSERT_REQUIRE_REGEX_STR.
	 */
	public static final Pattern ASSERT_REQUIRE_PATTERN = Pattern
			.compile(ASSERT_REQUIRE_REGEX_STR);

	/**
	 * The pattern based off of the ASSERT_REGEX_STR.
	 */
	public static final Pattern ASSERT_PATTERN = Pattern
			.compile(ASSERT_REGEX_STR);

	/**
	 * The amount of characters allowed to read ahead in the assertion
	 * replacement before determining that it should stop looking for the ending
	 * character.
	 */
	public static final int READ_AHEAD_LIMIT = 400;

	/**
	 * @parameter default-value=
	 *            "${project.basedir}${file.separator}src${file.separator}main${file.separator}javascript"
	 */
	private File sourceDirectory;

	/**
	 * @parameter default-value=
	 *            "${project.basedir}${file.separator}src${file.separator}main${file.separator}externs"
	 */
	private File externsDirectory;

	/**
	 * @parameter default-value=
	 *            "${project.build.directory}${file.separator}javascriptFramework"
	 */
	private File frameworkTargetDirectory;

	/**
	 * Will skip adding the build information to the header of each processed
	 * file.
	 * 
	 * @parameter default-value="false"
	 */
	private boolean skipBuildInfo;

	@Override
	public final File getSourceDirectory() {
		return sourceDirectory;
	}

	@Override
	public final File getExternsDirectory() {
		return externsDirectory;
	}

	@Override
	public final File getFrameworkTargetDirectory() {
		return frameworkTargetDirectory;
	}

	/**
	 * Will replace the section of the code after $$assert (assuming the pattern
	 * "$$assert()") with the augmented version.
	 * 
	 * @param readerIn
	 *            the input to read from - starting at "("
	 * @param fileName
	 *            the filename to use in $$assertion expansion
	 * @param lineNumber
	 *            the lineNumber to use in assertion expansion
	 * @param removeAssertions
	 *            whether to return the empty string or return the augmented
	 *            assertion
	 * @return the new assertion expression
	 * @throws IOException
	 *             if there was a problem reading or writing to the file
	 */
	protected static String assertReplacement(final Reader readerIn,
			final String fileName, final String lineNumber,
			final boolean removeAssertions) throws IOException {
		// assume within the content of the assert statement
		StringBuffer sb1 = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();

		char expressionChar;
		char lastChar = ' ';
		boolean startedExpressionReading = false;
		boolean singleLineComments = false;
		boolean commentBlock = false;
		int parenthesisCount = 0;
		int curlyBraceCount = 0;
		int brackets = 0;
		boolean doubleQuotes = false;
		boolean singleQuotes = false;
		boolean commaBreak = false;
		int expressionCharInt = -1;
		int charCount = 0;

		while ((expressionCharInt = readerIn.read()) != -1) {
			charCount++;
			if (charCount > READ_AHEAD_LIMIT) {
				throw new IllegalArgumentException(
						"Too many characters reached before the end of the $$assert expression at line "
								+ lineNumber
								+ " of file "
								+ fileName
								+ ".  Please keep these under "
								+ READ_AHEAD_LIMIT + " characters.");
			}
			expressionChar = (char) expressionCharInt;
			// if (expressionChar != )
			// eat up whitespace before and after assertion
			if (parenthesisCount == 0) {
				if (isWhitespace(expressionChar)) {
					continue;
					// ignore ending semicolon
				} else if (startedExpressionReading && expressionChar == ';') {
					break;
					// break on anything that is not a semicolon
				} else if (startedExpressionReading
						&& !isWhitespace(expressionChar)) {
					break;
					// if we are running into anything that is not a semicolon
					// or
					// whitespace, throw an exception
				} else if (!startedExpressionReading && expressionChar != '('
						&& !isWhitespace(expressionChar)) {
					throw new IllegalArgumentException(
							"The $$assert expression on line " + lineNumber
									+ " of file " + fileName + " is not valid.");
				}
			}

			// determine comma break
			if (parenthesisCount == 1 && expressionChar == ',' && brackets == 0
					&& !doubleQuotes && !singleQuotes) {
				commaBreak = true;
			}
			// determine whether and which string to append to
			else if (parenthesisCount > 1
					|| (parenthesisCount == 1 && expressionChar != ')')) {
				if (!commaBreak) {
					sb1.append(expressionChar);
				} else {
					sb2.append(expressionChar);
				}
			}

			// comment block
			if (singleLineComments && expressionChar == '\n') {
				singleLineComments = false;
			} else if (commentBlock && lastChar == '*' && expressionChar == '/') {
				commentBlock = false;
			} else {
				// determine comment blocks
				if (lastChar == '/' && expressionChar == '/') {
					singleLineComments = true;
				} else if (lastChar == '/' && expressionChar == '*') {
					commentBlock = true;
					// keep track of double quotes
				} else if (expressionChar == '"' && !doubleQuotes) {
					doubleQuotes = true;
				} else if (expressionChar == '"' && doubleQuotes) {
					if (lastChar != '\\') {
						doubleQuotes = false;
					}
					// keep track of single quotes
				} else if (expressionChar == '\'' && !singleQuotes) {
					singleQuotes = true;
				} else if (expressionChar == '\'' && singleQuotes) {
					if (lastChar != '\\') {
						singleQuotes = false;
					}

					// keep track of brackets
				} else if (expressionChar == '[') {
					brackets++;
				} else if (expressionChar == ']') {
					brackets--;

					// keep track of curly braces
				} else if (expressionChar == '{') {
					curlyBraceCount++;
				} else if (expressionChar == '}') {
					curlyBraceCount--;
					// keep track of parenthesis
				} else if (expressionChar == '(') {
					if (parenthesisCount == 0) {
						startedExpressionReading = true;
					}
					parenthesisCount++;
				} else if (expressionChar == ')') {
					parenthesisCount--;
				}
			}
			lastChar = expressionChar;
		}
		if (expressionCharInt == -1 && parenthesisCount > 0) {
			throw new IllegalArgumentException(
					"The $$assert expression on line "
							+ lineNumber
							+ " of file "
							+ fileName
							+ " has not been terminated before the end of the file.");
		}

		String expression = sb1.toString();
		String expressionString = null;
		if (expression == null || expression.equals("")) {
			expression = null;
			expressionString = "";
		} else {
			expressionString = escapeStringForJson(expression);
		}
		String message = sb2.toString();
		if (message == null || message.equals("")) {
			message = null;
		}
		String assertStmt = null;
		if (!removeAssertions) {
			assertStmt = "$$assert( " + expression + " , " + message
					+ ", { 'file' : '" + fileName + "', 'line' : '"
					+ lineNumber + "', 'expression' : '" + expressionString
					+ "' } );";
		} else {
			assertStmt = "";
		}
		return assertStmt;
	}

	/**
	 * Escapes the string for output in the augmented assert statement.
	 * 
	 * @param stringIn
	 *            the string to escape
	 * @return the esaped string
	 */
	private static String escapeStringForJson(final String stringIn) {
		return stringIn.replaceAll("\r\n", "").replaceAll("\n", "")
				.replaceAll("\r", "").replaceAll("\\\\", "\\\\\\\\")
				.replaceAll("'", "\\\\\\'");
	}

	/**
	 * Whether the character evaluated is whitespace, for assert parsing
	 * purposes.
	 * 
	 * @param expressionChar
	 *            the character to evaluate
	 * @return true if it is a form of whitespace, otherwise false
	 */
	private static boolean isWhitespace(final char expressionChar) {
		boolean returnVal = false;
		switch (expressionChar) {
		case ' ':
		case '\t':
		case '\n':
		case '\f':
		case '\r':
			returnVal = true;
			break;
		default:
			returnVal = false;
			break;
		}
		return returnVal;
	}

	@Override
	protected final void copyAndPreprocessFile(final File srcFile,
			final File destFile, final boolean removeAssertions)
			throws IOException {
		DirectoryIO.createDir(destFile.getParentFile());

		destFile.getParentFile().mkdirs();

		Reader fis = null;
		Writer fos = null;
		try {
			fis = new FileReader(srcFile);
			fos = new FileWriter(destFile);

			if (skipBuildInfo) {
				addHeaderInfo(fos);
			}

			readAndWriteBuffer(fis, fos, srcFile.getName(), removeAssertions);
		} finally {
			IOUtils.closeQuietly(fos);
			IOUtils.closeQuietly(fis);
		}
	}

	/**
	 * Will read from a source, augment or remove the assertion statements, and
	 * write the output to the buffer.
	 * 
	 * @param iReader
	 *            the place to read from
	 * @param oWriter
	 *            the place to write to
	 * @param srcFileName
	 *            the name of the current source file for use in writing
	 * @param removeAssertions
	 *            if true, removes them, if false, augments the assertion
	 *            objects
	 * @throws IOException
	 *             if there is a problem reading or writing the files
	 */
	private static void readAndWriteBuffer(final Reader iReader,
			final Writer oWriter, final String srcFileName,
			final boolean removeAssertions) throws IOException {
		int lineNumber = 0;
		BufferedReader bufferedIn = new BufferedReader(iReader);
		BufferedWriter bufferedOut = new BufferedWriter(oWriter);

		String line = null;
		bufferedIn.mark(READ_AHEAD_LIMIT);
		while ((line = bufferedIn.readLine()) != null) {
			lineNumber++;
			Matcher matcher = ASSERT_PATTERN.matcher(line);
			Matcher exceptionsMatcher = ASSERT_REQUIRE_PATTERN.matcher(line);
			if (matcher.matches() && !exceptionsMatcher.matches()) {
				bufferedIn.reset();
				// gobble up the part before the assertion on this line
				for (int i = 1; i <= matcher.start(1); i++) {
					int c = bufferedIn.read();
					bufferedOut.write(c);
				}

				// read past the assert statement
				for (int i = 1; i <= "$$assert".length(); i++) {
					bufferedIn.read();
				}
				// adjust $$assert statement
				String out = CompilePreProcessor.assertReplacement(bufferedIn,
						srcFileName, String.valueOf(lineNumber),
						removeAssertions);
				bufferedOut.write(out);

				lineNumber--;
			} else {
				bufferedOut.write(line);
				bufferedOut.newLine();
			}
			bufferedIn.mark(READ_AHEAD_LIMIT);
		}
		bufferedOut.flush();
	}

	/**
	 * Add the header info via a Writer object.
	 * 
	 * @param out
	 *            the writer object
	 * @throws IOException
	 *             if there are problems writing
	 */
	private void addHeaderInfo(final Writer out) throws IOException {
		BufferedWriter bufferedOut = new BufferedWriter(out);
		bufferedOut.write("// Time of Build="
				+ CompilePreProcessor.now("yyyy.MM.dd G 'at' hh:mm:ss z"));
		bufferedOut.newLine();
		bufferedOut.write("// Version=" + getProject().getVersion());
		bufferedOut.newLine();
		bufferedOut.write("// Coordinates=" + getProject().getGroupId() + ":"
				+ getProject().getArtifactId() + ":"
				+ getProject().getPackaging());
		bufferedOut.newLine();
	}

	/**
	 * Get the current time format in a string.
	 * 
	 * @param dateFormat
	 *            the format to print it
	 * @return the current time in the format designated
	 */
	public static String now(final String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

}
