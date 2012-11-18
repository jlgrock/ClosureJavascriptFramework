package com.github.jlgrock.javascriptframework.jsdependency;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.github.jlgrock.javascriptframework.mavenutils.io.DirectoryIO;
import com.github.jlgrock.javascriptframework.mavenutils.logging.MojoLogAppender;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.ArtifactExtractor;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.JsarRelativeLocations;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.PackagingType;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.ScopeType;

/**
 * The Abstract instance of all of the dependecy mojo implementations. This
 * gives common functions for retrieving dependencies for each different type.
 * 
 */
public abstract class AbstractDependencyMojo extends AbstractMojo {

	/**
	 * @return the current maven project
	 */
	public abstract MavenProject getProject();

	/**
	 * If you are using a local version of the library, you can skip the google
	 * library extraction.
	 * 
	 * @parameter default-value="false"
	 */
	private boolean skipGoogleExtraction;

	/**
	 * @return the skipExtraction
	 */
	public final boolean isSkipGoogleExtraction() {
		return skipGoogleExtraction;
	}

	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(AbstractDependencyMojo.class);

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
	 * @throws MojoFailureException
	 *             from any Mojo API specific calls
	 * @throws MojoExecutionException
	 *             from any Mojo API specific calls
	 */
	protected abstract void extractDependencies() throws IOException,
			MojoFailureException, MojoExecutionException;

	/**
	 * Extract the interns (assert/debug) files from the package.
	 * 
	 * @param extractAssert
	 *            whether to extract assert files
	 * @throws IOException
	 *             if unable to write files
	 * @throws MojoFailureException
	 *             from any Mojo API specific calls
	 * @throws MojoExecutionException
	 *             from any Mojo API specific calls
	 */
	protected final void extractInterns(final boolean extractAssert) throws IOException,
			MojoFailureException, MojoExecutionException {

		@SuppressWarnings("unchecked")
		Set<Artifact> artifactSet = (Set<Artifact>) getProject().getArtifacts();
		ArtifactExtractor extractJSArtifacts = new ArtifactExtractor(
				artifactSet);

		// extract internal assert dependencies
		if (extractAssert) {
			File assertLocation = JsarRelativeLocations
					.getInternsAssertLocation(getFrameworkTargetDirectory());
			LOGGER.info("Extracting internal assert dependencies to location \""
					+ assertLocation.getAbsolutePath()
					+ File.separator
					+ JsarRelativeLocations.JSAR_ASSERTION_SOURCE_LOCATION
					+ "\"");
			extractJSArtifacts.extract(
					JsarRelativeLocations.JSAR_ASSERTION_SOURCE_LOCATION + "/",
					PackagingType.JSAR, ScopeType.COMPILE, assertLocation);
		}

		// extract internal debug dependencies
		File debugLocation = JsarRelativeLocations
				.getInternsDebugLocation(getFrameworkTargetDirectory());
		LOGGER.info("Extracting internal debug dependencies to location \""

		+ debugLocation.getAbsolutePath() + File.separator
				+ JsarRelativeLocations.JSAR_PROCESSED_SOURCE_LOCATION
				+ "\"");
		extractJSArtifacts.extract(
				JsarRelativeLocations.JSAR_PROCESSED_SOURCE_LOCATION + "/",
				PackagingType.JSAR, ScopeType.COMPILE, debugLocation);
	}

	/**
	 * Extract the externs from the package.
	 * 
	 * @param artifactSet
	 *            the set to extract
	 * @throws IOException
	 *             if unable to write files
	 * @throws MojoFailureException
	 *             from any Mojo API specific calls
	 * @throws MojoExecutionException
	 *             from any Mojo API specific calls
	 */
	protected final void extractExterns(final Set<Artifact> artifactSet)
			throws IOException, MojoFailureException, MojoExecutionException {
		ArtifactExtractor extractJSArtifacts = new ArtifactExtractor(
				artifactSet);
		File location = JsarRelativeLocations
				.getExternsLocation(getFrameworkTargetDirectory());

		// extract extern files
		LOGGER.info("Extracting external dependencies to location \""
				+ location.getAbsolutePath() + "\"");
		extractJSArtifacts.extract(JsarRelativeLocations.JSAR_EXTERN_LOCATION
				+ "/", PackagingType.JSAR, ScopeType.COMPILE, location);
	}

	/**
	 * @return frameworkTargetDirectory
	 */
	public abstract File getFrameworkTargetDirectory();
}
