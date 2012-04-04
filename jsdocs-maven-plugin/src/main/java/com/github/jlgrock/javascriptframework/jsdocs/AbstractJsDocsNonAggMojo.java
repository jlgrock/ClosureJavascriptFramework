package com.github.jlgrock.javascriptframework.jsdocs;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.github.jlgrock.javascriptframework.mavenutils.pathing.FileListBuilder;

/**
 * An abstract class that aids in generating the common source Files.
 *
 */
public abstract class AbstractJsDocsNonAggMojo extends AbstractJsDocsMojo {

	/**
	 * The path to the JavaScript source directory). Default is
	 * src/main/javascript
	 * 
	 * @parameter
	 */
	private Set<File> sourceFiles;
	
	@Override
	public final Set<File> getSourceFiles() {
		Set<File> srcFiles = new HashSet<File>();
		if (sourceFiles == null) {
			srcFiles.addAll(FileListBuilder.buildFilteredList(new File(getBaseDir(), "src/main/javascript"), "js"));
		} else {
			srcFiles = sourceFiles;
		}
		return srcFiles;
	}
}
