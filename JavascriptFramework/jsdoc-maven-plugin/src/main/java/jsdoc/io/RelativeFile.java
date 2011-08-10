package jsdoc.io;

import java.io.File;

public class RelativeFile {
	private File file;
	private String relPath;
	
	RelativeFile(final File file, final String relPath) {
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