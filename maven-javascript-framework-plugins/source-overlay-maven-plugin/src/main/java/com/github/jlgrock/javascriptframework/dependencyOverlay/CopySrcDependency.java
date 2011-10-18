package org.mojo.javascriptframework.dependencyOverlay;

import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.mojo.javascriptframework.mavenutils.logging.MojoLogAppender;
import org.mojo.javascriptframework.mavenutils.mavenobjects.ExtractSrcAftifacts;

/**
 * Will copy the source dependency to a specified location.
 * 
 * @goal copySrcDependency
 * @threadSafe
 * @requiresDependencyResolution runtime
 */
public class CopySrcDependency extends AbstractDependencyOverlay {

	/**
	 * The logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(CopySrcDependency.class);

	@Override
	public final void execute() throws MojoExecutionException, MojoFailureException {
		// extract files to webapp
		extractDependencies();
	}

	/**
	 * Extract the dependencies to the location specified in the settings.
	 * 
	 * @throws MojoExecutionException when unable to extract the dependencies
	 */
	protected final void extractDependencies() throws MojoExecutionException {
		MojoLogAppender.beginLogging(this);
		try {
			LOGGER.info("Beginning extraction of source files to location \""
					+ getOutputDirectory() + "\"");

			// TODO removed unchecked argument
			// this will remained unchecked until it is changed in the maven
			// plugin code
			@SuppressWarnings("unchecked")
			Set<Artifact> srcArtifacts = getProject().getDependencyArtifacts();

			ExtractSrcAftifacts.extract(srcArtifacts, getScope(), getOutputDirectory());
		} catch (Exception e) {
			throw new MojoExecutionException(
					"Unable to access artifact dependency object: "
							+ e.getMessage());
		} finally {
			MojoLogAppender.endLogging();
		}
	}
}
