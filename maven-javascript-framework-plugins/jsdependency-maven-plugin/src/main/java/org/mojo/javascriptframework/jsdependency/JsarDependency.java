package org.mojo.javascriptframework.jsdependency;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.mojo.javascriptframework.mavenutils.io.DirectoryIO;
import org.mojo.javascriptframework.mavenutils.io.ResourceIO;
import org.mojo.javascriptframework.mavenutils.io.ZipUtils;
import org.mojo.javascriptframework.mavenutils.logging.MojoLogAppender;
import org.mojo.javascriptframework.mavenutils.mavenobjects.ArtifactExtractor;
import org.mojo.javascriptframework.mavenutils.mavenobjects.ClassifierType;
import org.mojo.javascriptframework.mavenutils.mavenobjects.PackagingType;
import org.mojo.javascriptframework.mavenutils.mavenobjects.ScopeType;

/**
 * Get files.
 *
 * @goal js-dependency
 * @phase compile
 * @requiresDependencyResolution runtime
 */
public class JsarDependency extends AbstractMojo {
	private static final Logger logger = Logger.getLogger( JsarDependency.class );
	
	/**
	 * The default directory to extract dependency files to.  This will do anything with a classifier that
	 * is unspecified or "internal".
	 * 
	 * @parameter default-value="${project.build.directory}${file.separator}javascriptFramework${file.separator}dependencies"
	 */
	protected File outputDirectory;
	
	/**
	 * The default directory to extract dependency files marked with classifier of "external".  
	 * This likely shouldn't be changed unless there is a conflict with another plugin.
	 * 
	 * @parameter default-value="${project.build.directory}${file.separator}javascriptFramework${file.separator}externalDependencies"
	 */
	protected File externalDirectory;
	
	/**
	 * The default directory to extract dependency files marked with classifier of "external".  
	 * This likely shouldn't be changed unless there is a conflict with another plugin.
	 * 
	 * @parameter default-value="${project.build.directory}${file.separator}javascriptFramework"
	 */
	protected File closureLibExtractDirectory;
	
	/**
     * The Maven Project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    
	public void execute() throws MojoExecutionException, MojoFailureException {
		MojoLogAppender.beginLogging(this);
		try {
			logger.info("Creating output directory at location \"" + outputDirectory.getAbsolutePath() + "\".");
			DirectoryIO.createDir(outputDirectory.getParentFile());
			
			extractDependencies();
			
		} catch (Exception e) {
			throw new MojoExecutionException("Unable to access artifact dependency object: " + e.getMessage());
		} finally {
			MojoLogAppender.endLogging();
		}
	}
	
	@SuppressWarnings("unchecked")
	private void extractDependencies() throws ZipException, IOException {
		ArtifactExtractor extractJSArtifacts = new ArtifactExtractor(project.getArtifacts());
		
		//extract internal dependencies
		logger.info("Extracting internal jsar artifacts to location \"" + outputDirectory.getAbsolutePath() + "\"");
		extractJSArtifacts.extract(PackagingType.JSAR, ScopeType.COMPILE, ClassifierType.INTERNAL, outputDirectory);

		//extract external dependencies
		//TODO make this get the external dependencies somehow
		//logger.info("Extracting external jsar artifacts to location \"" + externalDirectory.getAbsolutePath() + "\"");
		//extractJSArtifacts.extract(PackagingType.JSAR, ScopeType.COMPILE, ClassifierType.EXTERNAL, externalDirectory);
		
		//extract google dependencies (if needed)
		logger.info("Extracting google closure library to location \"" + closureLibExtractDirectory.getAbsolutePath() + "\"");
		ZipUtils.unzip(ResourceIO.getResourceAsZipStream("closure-library.zip"), closureLibExtractDirectory);
	}

}
