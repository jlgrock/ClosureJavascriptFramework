package com.github.jlgrock.javascriptframework.jsdocs;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.maven.artifact.Artifact;

import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.ArtifactExtractor;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.JsarRelativeLocations;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.PackagingType;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.ScopeType;
import com.github.jlgrock.javascriptframework.mavenutils.pathing.FileListBuilder;

/**
 * An abstract class that aids in generating the common source Files.
 *
 */
public abstract class AbstractJsDocsAggMojo extends AbstractJsDocsMojo {

	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(AbstractJsDocsAggMojo.class);

	/**
	 * The default directory to extract dependency files.
	 * This likely shouldn't be changed unless there is a
	 * conflict with another plugin.
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}${file.separator}apidocsAggFiles"
	 */
	private File aggregationFilesLocation;
	
	/**
	 * @return aggregationFiles
	 */
	protected final File getAggregationFilesLocation() {
		return aggregationFilesLocation;
	}
	
	/**
	 * The path to the JavaScript source directory). Default is
	 * src/main/javascript
	 * 
	 * @parameter
	 */
	private Set<File> sourceFiles;
	
	@Override
	public final Set<File> getSourceFiles() {
		Set<File> srcFiles = new HashSet<File>();
		if (sourceFiles == null) {
			srcFiles.addAll(FileListBuilder.buildFilteredList(new File(getBaseDir(), "src/main/javascript"), "js"));
			extractAggFiles();
			srcFiles.addAll(FileListBuilder.buildFilteredList(getAggregationFilesLocation(), "js"));
		} else {
			srcFiles = sourceFiles;
		}
		return srcFiles;
	}
	
	/**
	 * Extract the interns (assert/debug) files from the package.
	 * 
	 */
	protected final void extractAggFiles() {

		@SuppressWarnings("unchecked")
		Set<Artifact> artifactSet = (Set<Artifact>) getProject().getArtifacts();
		ArtifactExtractor extractJSArtifacts = new ArtifactExtractor(
				artifactSet);

		File assertLocation = JsarRelativeLocations
				.getInternsAssertLocation(getAggregationFilesLocation());
		LOGGER.info("Extracting internal assert dependencies to location \""
				+ assertLocation.getAbsolutePath()
				+ File.separator
				+ JsarRelativeLocations.JSAR_ASSERTION_SOURCE_LOCATION
				+ "\"");
		try {
			extractJSArtifacts.extract(
				JsarRelativeLocations.JSAR_ASSERTION_SOURCE_LOCATION + "/",
				PackagingType.JSAR, ScopeType.COMPILE, assertLocation);
		} catch(IOException ioe) {
			LOGGER.error("There was a problem extracting artifacts to location '" + getAggregationFilesLocation() + "'");
		}
	}
}
