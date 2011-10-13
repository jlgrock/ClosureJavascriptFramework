/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mojo.javascriptframework.mavenutils.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.log4j.Logger;

public class DirectoryIO {

	private static final Logger logger = Logger.getLogger(DirectoryIO.class);

	/**
	 * Copy a directory, including only those that are visible
	 * 
	 * @param srcDir
	 *            the directory to copy from
	 * @param destDir
	 *            the directory to copy to
	 * @throws IOException
	 */
	public static void copyDirectory(File srcDir, File destDir)
			throws IOException {
		logger.debug("Begin copy of source directory \""
				+ srcDir.getAbsolutePath() + "\" to destination \""
				+ destDir.getAbsoluteFile() + "\".");
		if (!srcDir.exists()) {
			throw new IOException("Directory at location \"" + srcDir.getAbsolutePath() + "\" does not exist.");
		}
		IOFileFilter filter = FileFileFilter.FILE;
		filter = FileFilterUtils.or(DirectoryFileFilter.DIRECTORY, filter);
		//filter = FileFilterUtils.and(HiddenFileFilter.VISIBLE, filter);
		FileUtils.copyDirectory(srcDir, destDir, filter);
	}

	/**
	 * Will do a recursive deletion of all files and folders based off of a
	 * given directory
	 * 
	 * @param dir
	 *            The directory to start the deleting
	 * @throws IOException
	 */
	static public void recursivelyDeleteDirectory(File dir) throws IOException {
		logger.debug("starting delete of directory \"" + dir.getAbsoluteFile()
				+ "\".");
		if (!dir.exists()) {
			logger.debug("deletion of directory ignored as it does not exist.");
			return;
		}
		if (!dir.isDirectory())
			throw new IOException("The path \"" + dir.getAbsolutePath()
					+ "\" is not a valid directory and cannot be deleted.");
		if (dir.exists()) {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					recursivelyDeleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		boolean noProblems = dir.delete();
		if (!noProblems) {
			throw new IOException(
					"There was a problem deleting the directory \""
							+ dir.getAbsolutePath() + "\"");
		}
	}

	public static void createDir(File dir) throws IOException {
		if(!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new IOException("Can not create dir " + dir);
			}
		}
	}
}
