package com.github.jlgrock.javascriptframework.jsdocs;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.maven.reporting.MavenReportException;
import org.mozilla.javascript.tools.shell.Main;

import com.github.jlgrock.javascriptframework.mavenutils.io.ResourceIO;
import com.github.jlgrock.javascriptframework.mavenutils.io.ZipUtils;
import com.github.jlgrock.javascriptframework.mavenutils.logging.Log4jOutputStream;

/**
 * A set of utility methods that help to create a jsdoc toolkit report once
 * parameters have been gathered.
 * 
 */
public final class ReportGenerator {
	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(ReportGenerator.class);

	/**
	 * Indicates the property to set in Windows/Unix platforms to set relative
	 * working path. In the case that this fails, this will still resolve and
	 * create files properly.
	 */
	private static final String USER_DIR = "user.dir";

	/**
	 * Empty constructor for utilities class.
	 */
	private ReportGenerator() {

	}

	/**
	 * Extract the jsdoc toolkit to a local dir.
	 * 
	 * @param extractDirectory
	 *            the directory to extract to
	 * @throws IOException
	 *             if there is a problem extracting or writing files
	 */
	public static void extractJSDocToolkit(final File extractDirectory)
			throws IOException {
		ZipUtils.unzip(ResourceIO.getResourceAsZipStream("jsdoctoolkit.zip"),
				extractDirectory);
	}

	/**
	 * Execute the jsdoc toolkit executable.
	 * 
	 * @param args
	 *            the list of arguments for the jsdoc toolkit
	 * @param extractDirectory
	 *            The directory to set the execution from
	 * @throws MavenReportException
	 *             for any reporting exception
	 */
	public static void executeJSDocToolkit(final List<String> args,
			final File extractDirectory) throws MavenReportException {
		String tempUserDir = System.getProperty(USER_DIR);
		LOGGER.info("currently at " + tempUserDir);
		try {
			LOGGER.info("changing dir to  " + extractDirectory);
			System.setProperty(USER_DIR, extractDirectory.getAbsolutePath());
			LOGGER.info("currently at " + System.getProperty(USER_DIR));
			setConsoleOuput();
			LOGGER.info("Executing with the following params: '"
					+ args.toString().replaceAll(",", "") + "'");
			Main.exec(args.toArray(new String[0]));
		} finally {
			System.setProperty(USER_DIR, tempUserDir);
			LOGGER.info("JsDocToolkit Reporting completed.");
		}
	}

	/**
	 * Will output the console jsdoc creation logs to log4j.
	 */
	public static void setConsoleOuput() {
		Log4jOutputStream l4jos = new Log4jOutputStream(LOGGER, Level.INFO);
		PrintStream ps = new PrintStream(l4jos, true);
		Main.setOut(ps);
	}
}
