package com.github.jlgrock.javascriptframework.mavenutils.pathing;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

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
	private static Logger logger = Logger.getLogger(FileListBuilder.class);

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
			logger.debug("building list with no filters...");
		} else {
			logger.debug("building list with filter \"" + extension + "\"");
		}
		Set<File> fileList = recursivelyAnalyze(root, extension);
		return fileList;
	}

	/**
	 * Recursively analyze all files and directories to check whether it matches.
	 * the extension
	 * 
	 * @param root
	 *            where to start from
	 * @param extension
	 *            the extension of the files that you want in the set
	 * @return the set of files with the extension specified from the root
	 *         specified
	 */
	private static Set<File> recursivelyAnalyze(final File root,
			final String extension) {
		Set<File> fileList = new HashSet<File>();
		if (root != null) {
			File[] list = root.listFiles();
			if (list != null && list.length != 0) {
				for (File f : list) {
					if (f.isDirectory()) {
						fileList.addAll(recursivelyAnalyze(f.getAbsoluteFile(),
								extension));
					} else {
						FileNameSeparator fns = new FileNameSeparator(f);
						if (extension == null || fns.getExtension().equalsIgnoreCase(extension)) {
							logger.debug("In buildlist, adding file to list : \""
									+ f.getAbsolutePath() + "\"");
							fileList.add(f);
						}
					}
				}
			}
		}
		return fileList;
	}
}
