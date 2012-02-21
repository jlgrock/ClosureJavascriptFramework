package com.github.jlgrock.javascriptframework.mavenutils.pathing;

import java.io.File;

/**
 * A class used to separate the file into its component parts.
 *
 */
public class FileNameSeparator {
	/**
	 * The path to the file.
	 */
	private String path;
	
	/**
	 * The file name.
	 */
	private String name;
	
	/**
	 * The file extension.
	 */
	private String extension;

	/**
	 * Constructor for files.
	 * 
	 * @param fileIn the file 
	 */
	public FileNameSeparator(final File fileIn) {
		if (fileIn == null) {
			path = "";
			name = "";
			extension = "";
		} else {
			path = fileIn.getParentFile().getAbsolutePath();
			parseFilename(fileIn.getName());
		}
	}

	/**
	 * Constructor for string representation of a file. This should be the
	 * absolute path
	 * 
	 * @param absoluteFile
	 *            the absolute file path to parse
	 */
	public FileNameSeparator(final String absoluteFile) {
		this(new File((absoluteFile == null) ? "" : absoluteFile));
	}

	/**
	 * Parse the filename into its sub-parts.
	 * 
	 * @param filename
	 *            the filename to parse
	 */
	private void parseFilename(final String filename) {
		int index = filename.lastIndexOf(".");
		int length = filename.length();
		if (index == length || index == -1) {
			name = filename;
			extension = "";
		} else {
			name = filename.substring(0, index);
			extension = filename.substring(index+1, length);
		}
	}

	/**
	 * Accessor method for path.
	 * 
	 * @return the path of the file
	 */
	public final String getPath() {
		return this.path;
	}

	/**
	 * Accessor method for name.
	 * 
	 * @return the name of the file (minus an extension, if it exists)
	 */
	public final String getName() {
		return this.name;
	}

	/**
	 * Accessor method for extension.
	 * 
	 * @return the extension of the file (if it exists)
	 */
	public final String getExtension() {
		return this.extension;
	}
}
