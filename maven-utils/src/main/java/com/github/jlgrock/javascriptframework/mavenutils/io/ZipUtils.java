package com.github.jlgrock.javascriptframework.mavenutils.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.github.jlgrock.javascriptframework.mavenutils.pathing.RelativeFile;

/**
 * A general usage class for doing zip file and zip stream manipulations.
 */
public class ZipUtils {

	/**
	 * The Logger.
	 */
	static final Logger LOGGER = Logger.getLogger(ZipUtils.class);
	
	/**
	 * Private Constructor for utility classes.
	 */
	private ZipUtils() {
	}
	
	/**
	 * The default buffer size for reading.
	 */
	private static final int BUFFER_SIZE = 1024;
	/**
	 * Zip up a directory and store it into a zip file.
	 * 
	 * @param inFolder the Folder to zip up
	 * @param destinationFile the archive file that you would like to create
	 * @throws IOException If unable to read files from the inFolder or write to the destinationFile
	 */
	public static final void zipFolder(final File inFolder, final File destinationFile) throws IOException {
		BufferedInputStream in = null;
		byte[] data = new byte[BUFFER_SIZE];

		LOGGER.debug("starting compression of files in folder \"" + inFolder.getAbsolutePath() 
				+ "\" to resulting file \"" + destinationFile + "\".");

		//create the resulting folder structure
		DirectoryIO.createDir(destinationFile.getParentFile());

		//create a zip output stream
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(destinationFile)));

		//loop through the files and add them to the archive
		Collection<RelativeFile> files = buildFileList(inFolder);
		for (RelativeFile file : files) {
			in = new BufferedInputStream(new FileInputStream(file.getFile()), BUFFER_SIZE);
			String relFileName;
			if (file.getRelPath().equals("")) {
				relFileName = file.getFile().getName();
			} else {
				relFileName = file.getRelPath() + File.separator + file.getFile().getName();
			}
			out.putNextEntry(new ZipEntry(relFileName));
			int count;
			while ((count = in.read(data, 0, BUFFER_SIZE)) != -1) {
				out.write(data, 0, count);
			}
			out.closeEntry();
		}
		out.flush();
		out.close();
	}

	/**
	 * Read all files in a directory.
	 * 
	 * @param inFolder the directory to read from
	 * @return the collection of files, relative to the inFolder
	 */
	private static Collection<RelativeFile> buildFileList(final File inFolder) {
		return readDirectory(inFolder, "");
	}
	
	/**
	 * Read all files in a directory.
	 * 
	 * @param inFolder the directory to read from
	 * @param relativePath the collection of files, relative to the inFolder
	 * @return the collection of relative files from reading the directory
	 */
	private static Collection<RelativeFile> readDirectory(final File inFolder, final String relativePath) {
		Collection<RelativeFile> returnCollection = new ArrayList<RelativeFile>();
		File[] files = inFolder.listFiles();
		for (int i=0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				returnCollection.addAll(readDirectory(files[i], relativePath + "/" + files[i].getName()));
			} else if (files[i].isFile()) {
				returnCollection.add(new RelativeFile(files[i], relativePath));
			}
		}
		return returnCollection;
	}
	
	/**
	 * Unzip all Zipfile contents to a directory.
	 * 
	 * @param zis the stream of the zipFile to extract artifacts from
	 * @param outputDir the location to put the artifacts from the zipfile
	 * @throws IOException if unable to read from the zip stream
	 */
	public static void unzip(final ZipInputStream zis, final File outputDir) throws IOException {
		unzip(zis, null, outputDir);
	}
	
	/**
	 * Unzip Zipfile contents matching zipEntryName to a directory.
	 * 
	 * @param zis the stream of the zipFile to extract artifacts from
	 * @param zipEntryName the name of the file within the zip to extract
	 * @param outputDir the location to put the artifacts from the zipfile
	 * @throws IOException if unable to read from the zip stream
	 */
	public static void unzip(final ZipInputStream zis, final String zipEntryName, final File outputDir) throws IOException {
		ZipEntry entry = null;
		try {
			while ((entry = zis.getNextEntry()) != null) {
				if (zipEntryName == null || entry.getName().startsWith(zipEntryName)) {
					try {
						ZipUtils.unzipEntry(zis, entry, outputDir);
					} finally {
						zis.closeEntry();
					}
				}
			}
		} finally {
			zis.close();
		}
	}

	/**
	 * Delete a directory and unzip to that directory.
	 * 
	 * @param zis the zipstream to unzip
	 * @param outputDir the output directory to delete and then write to
	 * @throws IOException if unable to delete the directory or unzip the file
	 */
	public static void deleteDirAndUnzip(final ZipInputStream zis, final File outputDir) throws IOException {
		if (outputDir.exists()) {
			FileUtils.deleteDirectory(outputDir);
		}
		unzip(zis, outputDir);
	}
	
	/**
	 * Unzip a single entry from a zipstream and write it to a directory.
	 * 
	 * @param zis the zipstream to read from
	 * @param entry the entry to read
	 * @param outputDir the directory to copy to
	 * @throws IOException if unable to create a file,  write to a file, or read from a file
	 */
	private static void unzipEntry(final ZipInputStream zis, final ZipEntry entry, final File outputDir) throws IOException {

		//create the directory for the entry
		if (entry.isDirectory()) {
			DirectoryIO.createDir(new File(outputDir, entry.getName()));
			return;
		}

		//create the directory for the output file
		File outputFile = new File(outputDir, entry.getName());
		if (!outputFile.getParentFile().exists()) {
			DirectoryIO.createDir(outputFile.getParentFile());
		}

		BufferedInputStream inputStream = new BufferedInputStream(zis);
		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));

		try {
			IOUtils.copy(inputStream, outputStream);
			outputStream.flush();
		} finally {
			outputStream.close();
		}
	}
}
