package com.github.jlgrock.javascriptframework.jspreprocessor;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import com.github.jlgrock.javascriptframework.mavenutils.io.DirectoryIO;
import com.github.jlgrock.javascriptframework.mavenutils.logging.MojoLogAppender;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.JsarRelativeLocations;
import com.github.jlgrock.javascriptframework.mavenutils.pathing.FileListBuilder;

/**
 * Currently this will search for $$assert in your code and create a copy of the code that 
 * either replace it with additional information or remove the $$assert statement completely.
 * This results in two processed copies of the code, one for assertion and one for debugging.
 * The debugging generated code will eventually be used in the closure compiler.
 * 
 * @author <a href="mailto:grantjl@umich.edu">Justin Grant</a>
 * 
 *         Several sections of this code has been taken from the FileUtils
 *         Apache library.
 */
public abstract class AbstractPreProcessorFrameworkMojo extends AbstractMojo {

	/**
	 * The Maven Project.
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(AbstractPreProcessorFrameworkMojo.class);

	@Override
	public final void execute() throws MojoExecutionException,
			MojoFailureException {
		MojoLogAppender.beginLogging(this);
		try {
			File topLevelAssertionDir = JsarRelativeLocations
					.getAssertionSourceLocation(getFrameworkTargetDirectory());
			File topLevelProcessedDir = JsarRelativeLocations
					.getDebugSourceLocation(getFrameworkTargetDirectory());
			File externsDir = JsarRelativeLocations
					.getExternsLocation(getFrameworkTargetDirectory());
			
			DirectoryIO.recursivelyDeleteDirectory(topLevelProcessedDir);
			File source = getSourceDirectory();
			File externs = getExternsDirectory();
			if (source.exists()) {
				copyAndPreprocessDirectory(source, topLevelAssertionDir, false);
				copyAndPreprocessDirectory(source, topLevelProcessedDir, true);
				if (externs.exists()) {
					DirectoryIO.copyDirectory(externs, externsDir);
				}
			} else {
				LOGGER.info("No directory found at location \""
						+ source.getAbsolutePath()
						+ "\".  Skipping pre-processing for this phase.");
			}
		} catch (Exception e) {
			LOGGER.error(
					"There was an error in the preprocessor: " + e.getMessage(),
					e);
			throw new MojoExecutionException(e.getMessage(), e);
		} finally {
			MojoLogAppender.endLogging();
		}
	}

	/**
	 * Copy the content and augment it for all files in the given source directory.
	 * @param srcDir the source directory
	 * @param destDir the directory to copy the augmented files to
	 * @param removeAssertions whether or not to remove assertions
	 * @throws IOException if there was a problem reading or writing to files in the srcDir or destDir
	 */
	private void copyAndPreprocessDirectory(final File srcDir,
			final File destDir, final boolean removeAssertions)
			throws IOException {
		Set<File> files = FileListBuilder.buildFilteredList(srcDir, "js");
		for (File file : files) {
			File copiedFile = makeRelativeFile(file, destDir);
			copyAndPreprocessFile(file, copiedFile, removeAssertions);
		}

	}

	/**
	 * Will make the file relative to the directory given.
	 * @param file the file to make
	 * @param destDir the destination path to make it at
	 * @return the relative file
	 */
	private File makeRelativeFile(final File file, final File destDir) {
		String rootPath = getSourceDirectory().getAbsolutePath();
		String newRoot = destDir.getAbsolutePath();
		String newFileStr = file.getAbsolutePath().replace(rootPath, newRoot);
		return new File(newFileStr);
	}

	/**
	 * Copy and preprocess each file.
	 * 
	 * @param srcFile the source directory to read from
	 * @param destFile the destination directory to write to
	 * @param removeAssertions whether or not to remove assertions from the file entirely
	 * @throws IOException if there was a problem reading or writing to files
	 */
	abstract void copyAndPreprocessFile(final File srcFile,
			final File destFile, final boolean removeAssertions)
			throws IOException;

	/**
	 * Accessor method for source directory.
	 * 
	 * @return the source directory used for the preprocessor step.
	 */
	public abstract File getSourceDirectory();

	/**
	 * Accessor method for externs directory.
	 * 
	 * @return the externs directory used for the preprocessor step.
	 */
	public abstract File getExternsDirectory();

	/**
	 * Accessor method for destination directory.
	 * 
	 * @return the destination directory used for the preprocessor step.
	 */
	public abstract File getFrameworkTargetDirectory();

	/**
	 * @return the project
	 */
	protected final MavenProject getProject() {
		return project;
	}
}
