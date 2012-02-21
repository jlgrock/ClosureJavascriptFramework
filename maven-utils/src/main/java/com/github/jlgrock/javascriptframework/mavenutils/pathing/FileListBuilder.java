package com.github.jlgrock.javascriptframework.mavenutils.pathing;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * Simple class for building a file list.
 */
public final class FileListBuilder {
	/**
	 * private empty Constructor for utility class.
	 */
	private FileListBuilder() {
	}

	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(FileListBuilder.class);

	/**
	 * Build a list of all files within a given root. If the root that is given
	 * is a file, it will return just that file in a list. If the root is a
	 * directory, it will recursively find all files within that directory and
	 * return these file objects within a list.
	 * 
	 * @param root
	 *            the directory to start from
	 * @return the set of filtered files
	 */
	public static Set<File> buildList(final File root) {
		return buildFilteredList(root, null);
	}

	/**
	 * Build a list of all files within a given root with a filter by extension.
	 * If the root that is given is a file, it will return just that file in a
	 * list. If the root is a directory, it will recursively find all files
	 * within that directory and return these file objects within a list.
	 * 
	 * @param root
	 *            the directory to start from
	 * @param extension
	 *            the file extension to filter by
	 * @return the set of filtered files
	 */
	public static Set<File> buildFilteredList(final File root,
			final String extension) {
		if (extension == null) {
			LOGGER.debug("building list with no extension filters...");
		} else {
			LOGGER.debug("building list with by extension filter \"" + extension + "\"");
		}
		Set<File> fileList = null;
		if (root.exists()) {
			DirectoryScanner ds = new DirectoryScanner();
			ds.setBasedir(root);
			String includes = "**/*";
			if (extension != null) {
				includes += "." + extension;
			}
			ds.setIncludes(includes.split(" "));
			//TODO can add exclude functionality later...
			// excludes
			//ds.setExcludes(this.excludes.split(" "));
			//ds.addDefaultExcludes();
			ds.setCaseSensitive(true);
			ds.scan();
			
			String[] relPaths = ds.getIncludedFiles();
			
			fileList = turnRelativeIntoFiles(root, relPaths);
		} else {
			fileList = new HashSet<File>();
		}
		LOGGER.debug("fileList result:" + fileList);
		return fileList;
	}

	/**
	 * Creates files off of relative paths from a root.
	 * @param root 
	 * 			the root file to base the relative
	 * @param relativePaths
	 * 			the paths to create the files from
	 * @return
	 * 			the set of files created
	 */
	public static Set<File> turnRelativeIntoFiles(final File root, final String[] relativePaths) {
		Set<File> files = new HashSet<File>();
		for (int i = 0; i < relativePaths.length; i++) {
			File file = new File(root, relativePaths[i]);
			files.add(file);
		}
		return files;
	}
}
