package com.github.jlgrock.javascriptframework.jsdocs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.maven.reporting.MavenReportException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.tools.shell.Global;
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
	 * @param jsDocApp
	 *            the location of the run.js within the jsDocToolkit
	 * @param args
	 *            the list of arguments for the jsdoc toolkit
	 * @param extractDirectory
	 *            The directory to set the execution from
	 * @throws MavenReportException
	 *             for any reporting exception
	 */
	public static void executeJSDocToolkit(final File jsDocApp,
			final List<String> args, final File extractDirectory)
			throws MavenReportException {
		try {
			setConsoleOuput();
			LOGGER.info("Executing with the following params: '"
					+ args.toString().replaceAll(",", "") + "'");

			Context cx = Context.enter();
			cx.setLanguageVersion(Context.VERSION_1_6);
			Global global = new Global();
			PrintStream sysOut = new PrintStream(new Log4jOutputStream(LOGGER, Level.INFO), true);
			global.setErr(sysOut);
			global.setOut(sysOut);
			global.init(cx);
			
			Scriptable argsObj = cx.newArray(global,
					args.toArray(new Object[] {}));
			global.defineProperty("arguments", argsObj,
					ScriptableObject.DONTENUM);
			cx.evaluateReader(global, new FileReader(jsDocApp), jsDocApp.getName(), 1, null);
		} catch (FileNotFoundException e) {
			LOGGER.error("Not able to find jsdoc file at location "
					+ jsDocApp.getAbsolutePath());
		} catch (IOException e) {
			LOGGER.error("Not able to read jsdoc file at location "
					+ jsDocApp.getAbsolutePath());
		}
		LOGGER.info("JsDocToolkit Reporting completed.");
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
