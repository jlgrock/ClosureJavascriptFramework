package org.mojo.javascriptframework.dependencyOverlay;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * The abstract class that defines the parameters used for all source-overlay
 * plugins.
 */
public abstract class AbstractDependencyOverlay extends AbstractMojo {

	/**
	 * The Maven project.
	 * 
	 * @parameter default-value="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * The directory for the overlayed src.
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}${path.separator}overlayStaging"
	 * @required
	 */
	private File outputDirectory;

	/**
	 * The directory where the src should be copied from.
	 * 
	 * @parameter default-value="${basedir}${path.separator}src"
	 * @required
	 */
	private File srcDirectory;

	/**
	 * The scope in which the plugin is executes.
	 * 
	 * @parameter default-value="compile"
	 */
	private String scope;

	@Override
	public abstract void execute() throws MojoExecutionException,
			MojoFailureException;

	/**
	 * @return the project
	 */
	protected final MavenProject getProject() {
		return project;
	}

	/**
	 * @return the outputDirectory
	 */
	protected final File getOutputDirectory() {
		return outputDirectory;
	}

	/**
	 * @return the srcDirectory
	 */
	protected final File getSrcDirectory() {
		return srcDirectory;
	}

	/**
	 * @return the scope
	 */
	protected final String getScope() {
		return scope;
	}
}
