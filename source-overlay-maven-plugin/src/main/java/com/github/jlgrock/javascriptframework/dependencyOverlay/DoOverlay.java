package com.github.jlgrock.javascriptframework.dependencyOverlay;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.github.jlgrock.javascriptframework.mavenutils.logging.MojoLogAppender;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.ExtractSrcAftifacts;

/**
 * Will overlay the files from the current source directory into the target
 * directory specified. If a duplicate file is encountered, it will overwrite
 * it.
 * 
 * @goal Overlay
 * @threadSafe
 * @requiresDependencyResolution runtime
 */
public final class DoOverlay extends AbstractDependencyOverlay {

	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(DoOverlay.class);

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		MojoLogAppender.beginLogging(this);
		try {
			LOGGER.info("Beginning extraction of artifacts to overlay at location \""
					+ getOutputDirectory() + "\"");
			ExtractSrcAftifacts.copyDirectory(getSrcDirectory(),
					getOutputDirectory());
		} catch (IOException e) {
			throw new MojoExecutionException(
					"Unable to access artifact dependency object: "
							+ e.getMessage());
		} finally {
			MojoLogAppender.endLogging();
		}
	}
}
