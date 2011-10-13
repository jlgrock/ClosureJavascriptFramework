package org.mojo.javascriptframework.jspreprocessor;

import java.io.File;

/**
 * eventually, this should do a bunch of things, but right now, it will just copy from
 * src/main/javascript to /target/javascript-generated
 *
 * @author <a href="mailto:grantjl@umich.edu">Justin Grant</a>
 * @requiresProject
 * @goal compile-preprocessors
 * @threadSafe
 */
public class CompilePreProcessor extends AbstractPreProcessorFramework {
	/**
	 * @parameter default-value="${project.basedir}${file.separator}src${file.separator}main${file.separator}javascript"
	 */
	private File sourceDirectory;

	/**
	 * @parameter default-value="${project.build.directory}${file.separator}javascriptframework${file.separator}processedJavascript"
	 */
	private File destinationDirectory;

	@Override
	public File getSourceDirectory() {
		return sourceDirectory;
	}
	
	@Override
	public File getDestinationDirectory() {
		return destinationDirectory;
	}
}
