package com.github.jlgrock.javascriptframework.jsdependency;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.github.jlgrock.javascriptframework.mavenutils.io.ResourceIO;
import com.github.jlgrock.javascriptframework.mavenutils.io.ZipUtils;

/**
 * Get files.
 * 
 * @goal js-dependency
 * @phase compile
 * @requiresDependencyResolution runtime
 */
public final class JsarDependencyMojo extends AbstractDependencyMojo {
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

	@SuppressWarnings("unchecked")
	@Override
	protected void extractDependencies() throws IOException,
			MojoFailureException, MojoExecutionException {
		extractInterns(true);
		extractExterns(getProject().getArtifacts());

		// extract google dependencies (if needed) - it could be provided by
		// something else
		if (!isSkipGoogleExtraction()) {
			extractAndRenameLibrary();
		}
	}

}
