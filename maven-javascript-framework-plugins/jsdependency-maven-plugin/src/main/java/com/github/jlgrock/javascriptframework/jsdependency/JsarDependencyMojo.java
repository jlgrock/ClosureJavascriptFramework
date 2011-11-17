package com.github.jlgrock.javascriptframework.jsdependency;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
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
 * @goal js-dependency
 * @phase compile
 * @requiresDependencyResolution runtime
 */
public class JsarDependencyMojo extends AbstractMojo {
	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(JsarDependencyMojo.class);

	/**
	 * The default directory to extract dependency files to. This will do
	 * anything with a classifier that is unspecified or "internal".
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}${file.separator}javascriptFramework"
	 */
	private File frameworkTargetDirectory;

	/**
	 * If you are using a local version of the library, you can skip this.
	 * 
	 * @parameter default-value="false"
	 */
	private boolean skipExtraction;
	
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
	 * The Maven Project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

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
	 * Extract the dependencies from the jsar.
	 * 
	 * @throws IOException
	 *             if there is a problem with the extraction
	 */
	private void extractDependencies() throws IOException {
		@SuppressWarnings("unchecked")
		// TODO this doesn't get artifacts of type=test... will be an issue. May
		// be due to lazy loading. May have to be done at appropriate times
		// instead of all at once
		ArtifactExtractor extractJSArtifacts = new ArtifactExtractor(
				project.getArtifacts());
		File location;

		// extract internal dependencies
		location = JsarRelativeLocations
				.getInternsLocation(getFrameworkTargetDirectory());
		LOGGER.info("Extracting internal dependencies to location \""
				+ location.getAbsolutePath() + "\"");
		extractJSArtifacts.extract(
				JsarRelativeLocations.JSAR_PROCESSED_SOURCE_LOCATION + "/",
				PackagingType.JSAR, ScopeType.COMPILE, location);

		// extract extern files
		location = JsarRelativeLocations
				.getExternsLocation(getFrameworkTargetDirectory());
		LOGGER.info("Extracting external dependencies to location \""
				+ location.getAbsolutePath() + "\"");
		extractJSArtifacts.extract(JsarRelativeLocations.JSAR_EXTERN_LOCATION
				+ "/", PackagingType.JSAR, ScopeType.COMPILE, location);

		// extract test dependencies
		location = JsarRelativeLocations
				.getTestLocation(getFrameworkTargetDirectory());
		LOGGER.info("Extracting test dependencies (scope=test) to location \""
				+ location.getAbsolutePath() + "\"");
		extractJSArtifacts.extract(JsarRelativeLocations.JSAR_COMPILE_LOCATION
				+ "/", PackagingType.JSAR, ScopeType.TEST, location);

		if (!skipExtraction) {
			// extract google dependencies (if needed)
			LOGGER.info("Extracting google closure library to location \""
					+ closureExtractLibDirectory.getAbsolutePath() + "\"");
			ZipUtils.unzip(
					ResourceIO.getResourceAsZipStream("closure-library.zip"),
					closureExtractLibDirectory);
		}
	}

	/**
	 * @return frameworkTargetDirectory
	 */
	public final File getFrameworkTargetDirectory() {
		return frameworkTargetDirectory;
	}
}
