package org.mojo.javascriptframework.mavenutils.pathing;

import java.io.File;

/**
 * Get the relative path of a file, while keeping the reference to the File object
 */
public class RelativeFile {
	private final File file;
	private final String relPath;
	
	public RelativeFile(final File file, final String relPath) {
		this.file = file;
		if (relPath.startsWith(File.separator)) {
			this.relPath = relPath.substring(1, relPath.length());
		} else {
			this.relPath = relPath;
		}
	}
	
	public File getFile() {
		return file;
	}
	
	public String getRelPath() {
		return relPath;
	}
}