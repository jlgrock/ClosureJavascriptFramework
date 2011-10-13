package org.mojo.javascriptframework.jspreprocessor;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.mojo.javascriptframework.mavenutils.io.DirectoryIO;
import org.mojo.javascriptframework.mavenutils.logging.MojoLogAppender;

/**
 * eventually, this should do a bunch of things, but right now, it will just copy from
 * src/main/javascript to /target/javascript-generated
 *
 * @author <a href="mailto:grantjl@umich.edu">Justin Grant</a>
 * @requiresProject
 */
public abstract class AbstractPreProcessorFramework extends AbstractMojo {

	static Logger logger = Logger.getLogger(AbstractPreProcessorFramework.class);

    /**
     * @parameter default-value="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

	public void execute() throws MojoExecutionException, MojoFailureException {
		MojoLogAppender.beginLogging(this);
		try {
			//TODO load in a multitude of preprocessor plugins that will each act on the file and 
			//eventually put it into the destination directory
			cleanup();
			File source = getSourceDirectory();
			File destination = getDestinationDirectory();
			if (source.exists()) {
				DirectoryIO.copyDirectory(source, destination);
			} else {
				logger.info("No directory found at location \"" 
						+ source.getAbsolutePath() + "\".  Skipping pre-processing for this phase.");
			}
		} catch(Exception e) {
			logger.error("There was an error in the preprocessor: " + e.getMessage(), e);
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			MojoLogAppender.endLogging();
		}
	}

	private void cleanup() throws IOException {
		// TODO this should be moved to a separate clean phased class
		DirectoryIO.recursivelyDeleteDirectory(getDestinationDirectory());
	}
	
	public abstract File getSourceDirectory();

	public abstract File getDestinationDirectory();
}