package com.github.jlgrock.javascriptframework.jsdocs;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.github.jlgrock.javascriptframework.mavenutils.logging.MojoLogAppender;

/**
 * Generates javascript docs from the jsdoc-toolkit (the final version).
 * 
 * @goal test-jsdoc
 * 
 */
public class JsTestDocsMojo extends AbstractJsDocsNonAggMojo {
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(JsTestDocsMojo.class);

	/**
	 * Specifies the destination directory where javadoc saves the generated
	 * HTML files. <br/>
	 * See <a href=
	 * "http://download.oracle.com/javase/1.4.2/docs/tooldocs/windows/javadoc.html#d"
	 * >d</a>. <br/>
	 * 
	 * @parameter expression="${destDir}"
	 *            default-value="${project.build.directory}/testapidocs"
	 * @required
	 */
	private File outputDirectory;

	@Override
	public final File getOutputDirectory() {
		return outputDirectory;
	}
	
	@Override
	public final String getClassifier() {
		return "jsdocs";
	}
	
	@Override
	public final void execute() throws MojoExecutionException, MojoFailureException {
		LOGGER.debug("starting report execution...");
		MojoLogAppender.beginLogging(this);
		try {
			ReportGenerator.extractJSDocToolkit(getToolkitExtractDirectory());
			Set<File> sourceFiles = getSourceFiles();
			List<String> args = createArgumentStack(sourceFiles);
			ReportGenerator.executeJSDocToolkit(getJsDocAppLocation(), args, getToolkitExtractDirectory());
		} catch (Exception e) {
			LOGGER.error("There was an error in the execution of the report: "
					+ e.getMessage(), e);
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			MojoLogAppender.endLogging();
		}
	}
	
	@Override
	public final File getArchiveOutputDirectory() {
		return null;
	}
}
