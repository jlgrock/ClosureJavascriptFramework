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
public final class JsarTestDependencyMojo extends AbstractDependencyMojo {
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

	@Override
	public MavenProject getProject() {
		return project;
	}
	
	/**
	 * The default directory to extract dependency files to. This will do
	 * anything with a classifier that is unspecified or "internal".
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}${file.separator}javascriptFramework"
	 */
	private File frameworkTargetDirectory;
	
	@Override
	public File getFrameworkTargetDirectory() {
		return frameworkTargetDirectory;
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

		
	}

}
