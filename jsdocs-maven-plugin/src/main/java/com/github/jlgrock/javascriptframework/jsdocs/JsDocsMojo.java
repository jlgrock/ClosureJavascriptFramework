package com.github.jlgrock.javascriptframework.jsdocs;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkFactory;
import org.apache.maven.reporting.MavenReportException;

/**
 * Generates javascript docs from the jsdoc-toolkit (the final version).
 * 
 * @goal jsdoc
 * 
 */
public class JsDocsMojo extends AbstractJsDocsMojo {
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(JsDocsMojo.class);

	/**
	 * The path to the JavaScript source directory). Default is
	 * src/main/javascript
	 */
	private ArrayList<File> sourceDirectories;

	/**
	 * Specifies the destination directory where javadoc saves the generated
	 * HTML files.
	 * 
	 * @parameter expression="${project.reporting.outputDirectory}/jsapidocs"
	 *            default-value="${project.reporting.outputDirectory}/jsapidocs"
	 */
	private File reportOutputDirectory;

	/**
	 * Constructor.
	 */
	public JsDocsMojo() {
		LOGGER.debug("initialized JsDocsMojo...");
	}

	@Override
	public ArrayList<File> getSourceDirectories() {
		if (sourceDirectories == null) {
			ArrayList<File> srcDirs = new ArrayList<File>();
			srcDirs.add(new File(getBaseDir(), "src/main/javascript"));
			srcDirs.add(new File(getBaseDir(),
					"target/javascriptFramework/internDependencies/debugSource"));
			return srcDirs;
		}
		return sourceDirectories;
	}

	@Override
	protected boolean isAggregator() {
		return false;
	}

	@Override
	protected String getClassifier() {
		return "jsdocs";
	}

	/**
	 * This method is called when the report generation is invoked by
	 * maven-site-plugin.
	 * 
	 * @param aSink
	 *            the aSink to write for
	 * @param aSinkFactory
	 *            the aSinkFactory provided by Maven
	 * @param aLocale
	 *            the local to adjust for
	 * @throws MavenReportException
	 *             for any exception
	 */
	public final void generate(final Sink aSink,
			final SinkFactory aSinkFactory, final Locale aLocale)
			throws MavenReportException {
		try {
			execute();
		} catch (Exception e) {
			throw new MavenReportException(e.getMessage(), e);
		}
	}

	@Override
	protected File getArchiveOutputDirectory() {
		// does not archive
		return null;
	}

	/**
	 * @return reportOutputDirectory
	 */
	protected File getReportOutputDirectory() {
		return reportOutputDirectory;
	}
}
