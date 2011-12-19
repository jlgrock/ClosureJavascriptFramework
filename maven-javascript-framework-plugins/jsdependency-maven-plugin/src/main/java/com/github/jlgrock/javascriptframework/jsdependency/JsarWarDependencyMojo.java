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
import com.github.jlgrock.javascriptframework.mavenutils.io.ResourceIO;
import com.github.jlgrock.javascriptframework.mavenutils.io.ZipUtils;
import com.github.jlgrock.javascriptframework.mavenutils.logging.MojoLogAppender;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.ArtifactExtractor;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.JsarRelativeLocations;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.PackagingType;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.ScopeType;

/**
 * Get files.
 * 
 * @goal js-war-dependency
 * @phase initialize
 * @requiresDependencyResolution compile
 */
public class JsarWarDependencyMojo extends AbstractMojo {
	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(JsarWarDependencyMojo.class);

	/**
	 * The Maven Project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * If you are using a local version of the library, you can skip the google
	 * library extraction.
	 * 
	 * @parameter default-value="false"
	 */
	private boolean skipExtraction;

	/**
	 * Whether or not to expand the debug and assertion library. This can be
	 * useful when including files for an api. When doing this, make sure to
	 * include the appropriate internal dependencies and closure compiler.
	 * 
	 * @parameter default-value="true"
	 */
	private boolean extractDebug;

	/**
	 * Whether or not to expand the debug and assertion library. This can be
	 * useful when including files for an api. When doing this, make sure to
	 * include the appropriate internal dependencies and closure compiler.
	 * 
	 * @parameter default-value="true"
	 */
	private boolean extractAssert;

	/**
	 * Whether or not to expand the compressed library files. This can be useful
	 * when including files for an api. When doing this, make sure to include
	 * the appropriate internal dependencies and closure compiler.
	 * 
	 * @parameter default-value="true"
	 */
	private boolean extractCompiled;

	/**
	 * Whether or not to include the Requires files. This can be set to false if
	 * not including debug/assert or if when using the debug/assert you are
	 * using it within a Google closure project.
	 * 
	 * @parameter default-value="true"
	 */
	private boolean includeRequiresFiles;
	
	/**
	 * The default directory to extract dependency files to. This will do
	 * anything with a classifier that is unspecified or "internal".
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}${file.separator}${project.build.finalName}${file.separator}javascript"
	 */
	private File frameworkTargetDirectory;
	
	/**
	 * The default directory to extract dependency files to. This will do
	 * anything with a classifier that is unspecified or "internal".
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}${file.separator}${project.build.finalName}${file.separator}javascript${file.separator}compiled"
	 */
	private File compiledFileDirectory;
	
	/**
	 * @return the maven project
	 */
	public MavenProject getProject() {
		return project;
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
	 * @param extractJSArtifacts
	 *            the artifacts to extract
	 * @throws IOException
	 *             if there is a problem reading the artifact
	 */
	protected void extractDependencies() throws IOException {
		File location;

		//for debugging files, get transitive dependencies
		@SuppressWarnings("unchecked")
		Set<Artifact> artifactSet = getProject().getArtifacts();
		ArtifactExtractor extractJSArtifacts = new ArtifactExtractor(
				artifactSet);

		// extract internal dependencies to assert dir
		location = JsarRelativeLocations.getInternsLocation(getFrameworkTargetDirectory());
		LOGGER.info("Extracting internal dependencies to location \""
				+ location.getAbsolutePath() + "\"");
		extractJSArtifacts.extract(
				JsarRelativeLocations.JSAR_PROCESSED_SOURCE_LOCATION + "/",
				PackagingType.JSAR, ScopeType.ANY, location);

		// extract extern files
		location = JsarRelativeLocations.getExternsLocation(getFrameworkTargetDirectory());
		LOGGER.info("Extracting external dependencies to location \""
				+ location.getAbsolutePath() + "\"");
		extractJSArtifacts.extract(JsarRelativeLocations.JSAR_EXTERN_LOCATION
				+ "/", PackagingType.JSAR, ScopeType.ANY, location);

		if (!skipExtraction) {
			// extract google dependencies (if needed) - it could be provided by
			// something else
			LOGGER.info("Extracting google closure library to location \""
					+ getFrameworkTargetDirectory().getAbsolutePath() + "\"");
			ZipUtils.unzip(
					ResourceIO.getResourceAsZipStream("closure-library.zip"),
					getFrameworkTargetDirectory());
		}
		
		//for the rest of this, only get specified dependency (not transitive)
		
		@SuppressWarnings("unchecked")
		Set<Artifact> artifactSetNonTransitive = getProject().getDependencyArtifacts();
		extractJSArtifacts = new ArtifactExtractor(artifactSetNonTransitive);
		
		location = JsarRelativeLocations.getOutputLocation(getFrameworkTargetDirectory());
		if (extractAssert) {
			extractJSArtifacts.extract(
					JsarRelativeLocations.JSAR_ASSERT_LOCATION + "/",
					PackagingType.JSAR, ScopeType.ANY, location);

			if (includeRequiresFiles) {
				location = JsarRelativeLocations
						.getOutputLocation(getFrameworkTargetDirectory());
				extractJSArtifacts.extract(
						JsarRelativeLocations.JSAR_ASSERT_REQUIRES_LOCATION
								+ "/", PackagingType.JSAR, ScopeType.ANY,
						location);
			}
		}
		if (extractDebug) {
			extractJSArtifacts.extract(
					JsarRelativeLocations.JSAR_DEBUG_LOCATION + "/",
					PackagingType.JSAR, ScopeType.ANY, location);
			if (includeRequiresFiles) {
				location = JsarRelativeLocations
						.getOutputLocation(getFrameworkTargetDirectory());
				extractJSArtifacts.extract(
						JsarRelativeLocations.JSAR_DEBUG_REQUIRES_LOCATION
								+ "/", PackagingType.JSAR, ScopeType.ANY,
						location);
			}
		}
		if (extractCompiled) {
			LOGGER.info("Extracting compiled dependencies to location \""
					+ compiledFileDirectory.getAbsolutePath() + "\"");
			extractJSArtifacts.extract(
					JsarRelativeLocations.JSAR_COMPILE_LOCATION + "/",
					PackagingType.JSAR, ScopeType.ANY, compiledFileDirectory);
		}
	}
	
	/**
	 * @return frameworkTargetDirectory
	 */
	public final File getFrameworkTargetDirectory() {
		return frameworkTargetDirectory;
	}

}
