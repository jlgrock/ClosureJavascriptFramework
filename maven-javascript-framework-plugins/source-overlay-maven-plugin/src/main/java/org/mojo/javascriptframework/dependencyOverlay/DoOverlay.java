package org.mojo.javascriptframework.dependencyOverlay;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.mojo.javascriptframework.mavenutils.logging.MojoLogAppender;
import org.mojo.javascriptframework.mavenutils.mavenobjects.ExtractSrcAftifacts;

/**
 * Will overlay the files from the current source directory into the target directory specified.  If 
 * a duplicate file is encountered, it will overwrite it.
 *
 * @goal Overlay
 * @threadSafe
 * @requiresDependencyResolution runtime
 */
public class DoOverlay extends AbstractDependencyOverlay{

	private static final Logger logger = Logger.getLogger( DoOverlay.class );
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		MojoLogAppender.beginLogging(this);
		try {
			logger.info("Beginning extraction of artifacts to overlay at location \"" + outputDirectory + "\"");
			ExtractSrcAftifacts.copyDirectory(srcDirectory, outputDirectory);
		} catch (IOException e) {
			throw new MojoExecutionException("Unable to access artifact dependency object: " + e.getMessage());
		} finally {
			MojoLogAppender.endLogging();
		}
	}
}