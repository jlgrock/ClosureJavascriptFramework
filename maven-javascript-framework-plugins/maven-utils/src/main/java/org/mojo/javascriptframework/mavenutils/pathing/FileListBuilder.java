package org.mojo.javascriptframework.mavenutils.pathing;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Simple class for building a file list
 */
public class FileListBuilder {
	private static Logger logger = Logger.getLogger(FileListBuilder.class);

	/**
	 * Build a list of all files within a given root.  If the root that
	 * is given is a file, it will return just that file in a list.  If the 
	 * root is a directory, it will recursively find all files within
	 * that directory and return these file objects within a list.
	 */
	public static Set<File> buildList(final File root) {
		return buildFilteredList(root, null);
	}
	
	public static Set<File> buildFilteredList(final File root, final String extension) {
		if (extension == null) {
        	logger.debug("building list with no filters...");
        } else {
        	logger.debug("building list with filter \"" + extension + "\"");
        }
        Set<File> fileList = recursivelyAnalyze(root, extension);
		return fileList;
	}
	
	private static Set<File> recursivelyAnalyze(final File root, final String extension) {
		Set<File> fileList = new HashSet<File>();
		File[] list = root.listFiles();
		if (list != null && list.length != 0) {
	        for ( File f : list ) {
	            if ( f.isDirectory() ) {
	            	fileList.addAll(recursivelyAnalyze(f.getAbsoluteFile(), extension));
	            }
	            else {
	            	String filename = f.getName();
	            	if (filename != null && (extension == null || filename.endsWith("." + extension))) {
	            		logger.debug("In buildlist, adding file to list : \"" + f.getAbsolutePath() + "\"");
	            		fileList.add(f);
	            	}
	            }
	        }
		}
        return fileList;
	}
}
