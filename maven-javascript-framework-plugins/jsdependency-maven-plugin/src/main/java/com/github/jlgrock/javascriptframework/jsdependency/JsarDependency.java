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
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.PackagingType;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.ScopeType;

/**
 * Get files.
 *
 * @goal js-dependency
 * @phase compile
 * @requiresDependencyResolution runtime
 */
public class JsarDependency extends AbstractMojo {
	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger( JsarDependency.class );
	
	/**
	 * The default directory to extract dependency files to.  This will do anything with a classifier that
	 * is unspecified or "internal".
	 * 
	 * @parameter default-value="${project.build.directory}${file.separator}javascriptFramework${file.separator}dependencies"
	 */
	private File outputDirectory;
	
	/**
	 * The default directory to extract dependency files marked with classifier of "external".  
	 * This likely shouldn't be changed unless there is a conflict with another plugin.
	 * 
	 * @parameter default-value="${project.build.directory}${file.separator}javascriptFramework${file.separator}externalDependencies"
	 */
	private File externalDirectory;
	
	/**
	 * The default directory to extract dependency files marked with classifier of "external".  
	 * This likely shouldn't be changed unless there is a conflict with another plugin.
	 * 
	 * @parameter default-value="${project.build.directory}${file.separator}javascriptFramework"
	 */
	private File closureLibExtractDirectory;
	
	/**
     * The Maven Project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    
    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
		MojoLogAppender.beginLogging(this);
		try {
			LOGGER.info("Creating output directory at location \"" + outputDirectory.getAbsolutePath() + "\".");
			DirectoryIO.createDir(outputDirectory.getParentFile());
			
			extractDependencies();
			
		} catch (Exception e) {
			throw new MojoExecutionException("Unable to access artifact dependency object: " + e.getMessage());
		} finally {
			MojoLogAppender.endLogging();
		}
	}
	
	/**
	 * Extract the dependencies from the jsar.
	 * 
	 * @throws IOException if there is a problem with the extraction
	 */
	private void extractDependencies() throws IOException {
		@SuppressWarnings("unchecked")
		ArtifactExtractor extractJSArtifacts = new ArtifactExtractor(project.getArtifacts());
		
		//extract internal dependencies
		LOGGER.info("Extracting internal jsar artifacts to location \"" + outputDirectory.getAbsolutePath() + "\"");
		extractJSArtifacts.extract(PackagingType.JSAR, ScopeType.COMPILE, outputDirectory);

		//extract external dependencies
		LOGGER.info("Extracting external fjsar artifacts to location \"" + externalDirectory.getAbsolutePath() + "\"");
		extractJSArtifacts.extract(PackagingType.JSAR, ScopeType.TEST, externalDirectory);
		
		//extract google dependencies (if needed)
		LOGGER.info("Extracting google closure library to location \"" + closureLibExtractDirectory.getAbsolutePath() + "\"");
		ZipUtils.unzip(ResourceIO.getResourceAsZipStream("closure-library.zip"), closureLibExtractDirectory);
	}

}
