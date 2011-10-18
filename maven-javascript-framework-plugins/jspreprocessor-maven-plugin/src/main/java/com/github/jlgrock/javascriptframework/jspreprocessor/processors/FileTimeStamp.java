package org.mojo.javascriptframework.jspreprocessor.processors;

import org.apache.log4j.Logger;
import org.apache.maven.project.MavenProject;

/**
 * Will stamp every file that is processed.
 */
public class FileTimeStamp {

	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(FileTimeStamp.class);

	;

	/**
	 * Constructor.
	 */
	FileTimeStamp() {
		LOGGER.debug("starting timestamp processor");
	}

	/**
	 * fill this in later...
	 * 
	 * @param project
	 *            asd TODO .
	 */
	public final void executeFileTimeStamp(final MavenProject project) {
		// TODO add this information to the top of every file
		project.getVersion();
		project.getGroupId();
		project.getArtifactId();
		System.getProperty("java.version");
		System.getProperty("os.name");
		System.getProperty("user.name");
		// getCopyright();
		// getLicense();
	}
}
