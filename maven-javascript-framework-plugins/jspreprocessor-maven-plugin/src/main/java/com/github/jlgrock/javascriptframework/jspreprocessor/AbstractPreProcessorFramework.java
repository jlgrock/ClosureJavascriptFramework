package com.github.jlgrock.javascriptframework.jspreprocessor;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.github.jlgrock.javascriptframework.mavenutils.io.DirectoryIO;
import com.github.jlgrock.javascriptframework.mavenutils.logging.MojoLogAppender;

/**
 * eventually, this should do a bunch of things, but right now, it will just copy from
 * src/main/javascript to /target/javascript-generated.
 *
 * @author <a href="mailto:grantjl@umich.edu">Justin Grant</a>
 */
public abstract class AbstractPreProcessorFramework extends AbstractMojo {

	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(AbstractPreProcessorFramework.class);

	@Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
		MojoLogAppender.beginLogging(this);
		try {
			//TODO load in a multitude of preprocessor plugins that will each act on the file and 
			//eventually put it into the destination directory
			DirectoryIO.recursivelyDeleteDirectory(getDestinationDirectory());
			File source = getSourceDirectory();
			File destination = getDestinationDirectory();
			if (source.exists()) {
				DirectoryIO.copyDirectory(source, destination);
			} else {
				LOGGER.info("No directory found at location \"" 
						+ source.getAbsolutePath() + "\".  Skipping pre-processing for this phase.");
			}
		} catch(Exception e) {
			LOGGER.error("There was an error in the preprocessor: " + e.getMessage(), e);
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			MojoLogAppender.endLogging();
		}
	}

	/**
	 * Accessor method for source directory.
	 * 
	 * @return the source directory used for the preprocessor step.
	 */
	public abstract File getSourceDirectory();

	/**
	 * Accessor method for destination directory.
	 * 
	 * @return the destination directory used for the preprocessor step.
	 */
	public abstract File getDestinationDirectory();
}
