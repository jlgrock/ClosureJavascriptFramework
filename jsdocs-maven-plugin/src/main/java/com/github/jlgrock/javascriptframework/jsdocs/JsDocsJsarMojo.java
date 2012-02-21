package com.github.jlgrock.javascriptframework.jsdocs;

import java.io.File;

/**
 * Generates javascript docs from the jsdoc-toolkit (the final version) and
 * stores them into a js archive.
 * 
 * @goal jsar
 * 
 */
public class JsDocsJsarMojo extends JsDocsMojo {
	/**
	 * Specifies the directory where the generated jar file will be put.
	 * 
	 * @parameter expression="${project.build.directory}"
	 */
	private File jsarOutputDirectory;

	@Override
	protected final File getArchiveOutputDirectory() {
		return jsarOutputDirectory;
	}
}
