package com.github.jlgrock.javascriptframework.jsdependency;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import com.github.jlgrock.javascriptframework.mavenutils.io.ResourceIO;
import com.github.jlgrock.javascriptframework.mavenutils.io.ZipUtils;
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
public class JsarDependencyMojo extends AbstractDependencyMojo {
	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(JsarDependencyMojo.class);
	
	/**
	 * The Maven Project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * If you are using a local version of the library, you can skip the google library extraction.
	 * 
	 * @parameter default-value="false"
	 */
	private boolean skipGoogleExtraction;
	
	@Override
	public MavenProject getProject() {
		return project;
	}

	@Override
	protected void extractDependencies() throws IOException {
		File location;

		@SuppressWarnings("unchecked")
		Set<Artifact> artifactSet = (Set<Artifact>) getProject().getArtifacts();
		ArtifactExtractor extractJSArtifacts = new ArtifactExtractor(artifactSet);
		
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

		if (!skipGoogleExtraction) {
			// extract google dependencies (if needed)
			LOGGER.info("Extracting google closure library to location \""
					+ getClosureExtractLibDirectory().getAbsolutePath() + "\"");
			ZipUtils.unzip(
					ResourceIO.getResourceAsZipStream("closure-library.zip"),
					getClosureExtractLibDirectory());
		}
		
	}
	
	/**
	 * @return the skipExtraction
	 */
	public boolean isSkipExtraction() {
		return skipGoogleExtraction;
	}
}
