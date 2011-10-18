package com.github.jlgrock.javascriptframework.mavenutils.pathing;

import java.io.File;

/**
 * Stores the relative path of a file, while keeping the reference to the File
 * object.
 */
public class RelativeFile {
	/**
	 * The original File reference.
	 */
	private final File file;
	
	/**
	 * The relative path reference.
	 */
	private final String relPath;

	/**
	 * The Constructor.
	 * 
	 * @param fileIn the original file reference
	 * @param relPathIn the relative path storage
	 */
	public RelativeFile(final File fileIn, final String relPathIn) {
		this.file = fileIn;
		if (relPathIn.startsWith(File.separator)) {
			this.relPath = relPathIn.substring(1, relPathIn.length());
		} else {
			this.relPath = relPathIn;
		}
	}

	/**
	 * Accessor method for the file.
	 * 
	 * @return the file
	 */
	public final File getFile() {
		return file;
	}

	/**
	 * Accessor method for relative path.
	 * 
	 * @return the relative path
	 */
	public final String getRelPath() {
		return relPath;
	}
}
