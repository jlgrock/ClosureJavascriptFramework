package com.github.jlgrock.javascriptframework.mavenutils.pathing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

/**
 * Search through a directory recursively to find a file. This is very useful to
 * not force people to use full paths when a Mojo requires you to enter files
 * into the pom or other config file.
 * 
 */
public final class SearchFileRecursive {

	/**
	 * Private constructor for utility classes.
	 */
	private SearchFileRecursive() {}
	
	/**
	 * Find the Java File object based off of a starting directory.
	 * 
	 * @param filename the filename to search for
	 * @param startingDirectory the directory to start searching at
	 * @return the File discovered
	 * @throws FileNotFoundException will throw an exception if no file is found matching the criteria 
	 */
	public static File findAbsoluteFile(final String filename,
			final String startingDirectory) throws FileNotFoundException {
		File returnFile = null;
		try {
			File startingDirFile = new File(startingDirectory);
			Collection<File> files = FileUtils.listFiles(startingDirFile,
					FileFilterUtils.nameFileFilter(filename),
					TrueFileFilter.INSTANCE);

			if (files.size() == 0) {
				throw new FileNotFoundException("file '" + filename
						+ "' not found in directory '" + startingDirectory);
			} else if (files.size() > 1) {
				throw new FileNotFoundException(
						"multiple files with filename '" + filename
								+ "' found in directory '" + startingDirectory);
			} else {
				for (File f : files) {
					returnFile = f;
				}
			}
		} catch (FileNotFoundException fnfe) {
			throw fnfe;
		} catch (Exception e) {
			throw new FileNotFoundException("'" + filename
					+ "' not found in directory '" + startingDirectory + "'");
		}
		return returnFile;
	}
}
