package com.github.jlgrock.javascriptframework.jsdocs;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.github.jlgrock.javascriptframework.mavenutils.logging.MojoLogAppender;

/**
 * Generates javascript docs from the jsdoc-toolkit (the final version) and
 * stores them into a js archive.
 * 
 * @goal aggregate-jsar
 * @phase package
 */
public class AggregatorJsDocsJsarMojo extends AbstractJsDocsAggMojo {
	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(AggregatorJsDocsJsarMojo.class);


	/**
	 * Specifies the destination directory where javadoc saves the generated
	 * HTML files. <br/>
	 * See <a href=
	 * "http://download.oracle.com/javase/1.4.2/docs/tooldocs/windows/javadoc.html#d"
	 * >d</a>. <br/>
	 * 
	 * @parameter expression="${destDir}"
	 *            default-value="${project.build.directory}/apidocs"
	 * @required
	 */
	private File outputDirectory;

	@Override
	public final File getOutputDirectory() {
		return outputDirectory;
	}
	
	/**
	 * Specifies the directory to archive.
	 * 
	 * @parameter default-value="${project.build.directory}"
	 */
	private File jsarOutputDirectory;

	@Override
	public final File getArchiveOutputDirectory() {
		return jsarOutputDirectory;
	}

	@Override
	public final void execute() throws MojoExecutionException,
			MojoFailureException {
		LOGGER.debug("starting report execution...");
		MojoLogAppender.beginLogging(this);
		try {
			ReportGenerator
					.extractJSDocToolkit(getToolkitExtractDirectory());
			Set<File> sourceFiles = getSourceFiles();
			List<String> args = createArgumentStack(sourceFiles);
			ReportGenerator.executeJSDocToolkit(getJsDocAppLocation(), args, getToolkitExtractDirectory());
			File innerDestDir = getArchiveOutputDirectory();
			String destFileName = getFinalName() + "-" + getClassifier() + getExtensionFormat();
			File destFile = null;
			if (innerDestDir.exists()) {
				destFile = AbstractJsDocsMojo.generateArchive(this, innerDestDir, destFileName);
			}
			getProjectHelper().attachArtifact(getProject(), getExtensionFormat(), getClassifier(), destFile);

		} catch (Exception e) {
			LOGGER.error("There was an error in the execution of the report: "
					+ e.getMessage(), e);
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			MojoLogAppender.endLogging();
		}
	}

	@Override
	public final String getClassifier() {
		return "jsdocs";
	}

}
