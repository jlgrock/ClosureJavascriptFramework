package com.github.jlgrock.javascriptframework.jspreprocessor;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * eventually, this should do a bunch of things, but right now, it will just copy from
 * src/main/javascript to /target/javascript-generated.
 *
 * @author <a href="mailto:grantjl@umich.edu">Justin Grant</a>
 * @requiresProject
 * @goal test-preprocessors
 * @threadSafe
 */
public class TestPreProcessor extends AbstractPreProcessorFrameworkMojo {
	/**
	 * @parameter default-value="${project.basedir}${file.separator}src${file.separator}test${file.separator}javascript"
	 */
	private File sourceDirectory;

	/**
	 * @parameter default-value="${project.basedir}${file.separator}src${file.separator}test${file.separator}externs"
	 */
	private File externsDirectory;

	/**
	 * @parameter default-value="${project.build.directory}${file.separator}javascriptframework"
	 */
	private File frameworkTargetDirectory;

	@Override
	public final File getSourceDirectory() {
		return sourceDirectory;
	}
	
	@Override
	public final File getExternsDirectory() {
		return externsDirectory;
	}

	@Override
	public final File getFrameworkTargetDirectory() {
		return frameworkTargetDirectory;
	}

	@Override
	public final void copyAndPreprocessFile(final File srcFile, final File destFile, final boolean removeAsserts) throws IOException {
		FileUtils.copyFile(srcFile, destFile);
	}
}
