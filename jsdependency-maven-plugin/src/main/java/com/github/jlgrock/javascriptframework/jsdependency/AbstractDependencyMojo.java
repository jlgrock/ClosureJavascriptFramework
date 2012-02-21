package com.github.jlgrock.javascriptframework.jsdependency;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.github.jlgrock.javascriptframework.mavenutils.io.DirectoryIO;
import com.github.jlgrock.javascriptframework.mavenutils.logging.MojoLogAppender;

/**
 * The Abstract instance of all of the dependecy mojo implementations. This
 * gives common functions for retrieving dependencies for each different type.
 * 
 */
public abstract class AbstractDependencyMojo extends AbstractMojo {
	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(AbstractDependencyMojo.class);

	/**
	 * The default directory to extract dependency files to. This will do
	 * anything with a classifier that is unspecified or "internal".
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}${file.separator}javascriptFramework"
	 */
	private File frameworkTargetDirectory;

	/**
	 * The default directory to extract dependency files marked with classifier
	 * of "external". This likely shouldn't be changed unless there is a
	 * conflict with another plugin.
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}${file.separator}javascriptFramework"
	 */
	private File closureExtractLibDirectory;

	/**
	 * @return the closureExtractLibDirectory
	 */
	public final File getClosureExtractLibDirectory() {
		return closureExtractLibDirectory;
	}

	@Override
	public final void execute() throws MojoExecutionException,
			MojoFailureException {
		MojoLogAppender.beginLogging(this);
		try {
			LOGGER.info("Creating output directory at location \""
					+ getFrameworkTargetDirectory().getAbsolutePath() + "\".");
			DirectoryIO
					.createDir(getFrameworkTargetDirectory().getParentFile());

			extractDependencies();

		} catch (Exception e) {
			throw new MojoExecutionException(
					"Unable to access artifact dependency object: "
							+ e.getMessage());
		} finally {
			MojoLogAppender.endLogging();
		}
	}

	/**
	 * Extract the dependencies from the jsar to the appropriate location(s).
	 * 
	 * @throws IOException
	 *             if there is a problem reading the artifact
	 */
	protected abstract void extractDependencies() throws IOException;

	/**
	 * @return frameworkTargetDirectory
	 */
	public final File getFrameworkTargetDirectory() {
		return frameworkTargetDirectory;
	}

	/**
	 * @return the current maven project
	 */
	public abstract MavenProject getProject();
}
