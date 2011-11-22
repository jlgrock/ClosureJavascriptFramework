package com.github.jlgrock.javascriptframework.jsdependency;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.project.MavenProject;

import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.ArtifactExtractor;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.JsarRelativeLocations;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.PackagingType;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.ScopeType;

/**
 * Get files.
 * 
 * @goal js-test-dependency
 * @phase test
 * @requiresDependencyResolution test
 */
public class JsarTestDependencyMojo extends AbstractDependencyMojo {
	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(JsarTestDependencyMojo.class);

	/**
	 * The Maven Project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * Whether or not to expand the debug and assertion library.  This can be useful 
	 * when including files for an api.  When doing this, make sure to include the 
	 * appropriate internal dependencies and closure compiler.
	 * 
	 * @parameter default-value="false"
	 */
	private boolean expandDebug;

	@Override
	public MavenProject getProject() {
		return project;
	}

	@Override
	protected void extractDependencies() throws IOException {
		File location;

		@SuppressWarnings("unchecked")
		Set<Artifact> artifactSet = getProject().getDependencyArtifacts();
		ArtifactExtractor extractJSArtifacts = new ArtifactExtractor(
				artifactSet);

		// extract test dependencies
		location = JsarRelativeLocations
				.getTestLocation(getFrameworkTargetDirectory());
		LOGGER.info("Extracting test dependencies (scope=test) to location \""
				+ location.getAbsolutePath() + "\"");
		extractJSArtifacts.extract(JsarRelativeLocations.JSAR_COMPILE_LOCATION
				+ "/", PackagingType.JSAR, ScopeType.TEST, location);

		if (expandDebug) {
			location = JsarRelativeLocations
					.getOutputLocation(getFrameworkTargetDirectory());
			extractJSArtifacts.extract(
					JsarRelativeLocations.JSAR_DEBUG_LOCATION + "/",
					PackagingType.JSAR, ScopeType.TEST, location);

			location = JsarRelativeLocations
					.getOutputLocation(getFrameworkTargetDirectory());
			extractJSArtifacts.extract(
					JsarRelativeLocations.JSAR_ASSERT_LOCATION + "/",
					PackagingType.JSAR, ScopeType.TEST, location);
		}
	}

}
