package org.mojo.javascriptframework.mavenutils.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.mojo.javascriptframework.mavenutils.pathing.RelativeFile;

public class ZipUtils {

	static final Logger logger = Logger.getLogger(ZipUtils.class);
	
	/**
	 * Create an archive
	 * @param inFolder
	 * @param outFolder
	 * @param outFile
	 * @throws IOException
	 */
	public void zipFolder(final File inFolder, final File destinationFile) throws IOException {
		BufferedInputStream in = null;
		byte[] data = new byte[1000];

		logger.debug("starting compression of files in folder \"" + inFolder.getAbsolutePath() 
				+ "\" to resulting file \"" + destinationFile + "\".");

		//create the resulting folder structure
		DirectoryIO.createDir(destinationFile);

		//create a zip output stream
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(destinationFile)));

		//loop through the files and add them to the archive
		Collection<RelativeFile> files = buildFileList(inFolder);
		for (RelativeFile file : files) {
			in = new BufferedInputStream(new FileInputStream(file.getFile()), 1000);
			String relFileName;
			if (file.getRelPath().equals("")) {
				relFileName = file.getFile().getName();
			} else {
				relFileName = file.getRelPath() + File.separator + file.getFile().getName();
			}
			out.putNextEntry(new ZipEntry(relFileName));
			int count;
			while ((count = in.read(data, 0, 1000)) != -1) {
				out.write(data, 0, count);
			}
			out.closeEntry();
		}
		out.flush();
		out.close();
	}

	private Collection<RelativeFile> buildFileList(final File inFolder) {
		return readDirectory(inFolder, "");
	}
	
	private Collection<RelativeFile> readDirectory(final File inFolder, final String relativePath) {
		Collection<RelativeFile> returnCollection = new ArrayList<RelativeFile>();
		File[] files = inFolder.listFiles();
		for (int i=0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				returnCollection.addAll(readDirectory(files[i], relativePath + File.separator + files[i].getName()));
			} else if (files[i].isFile()) {
				returnCollection.add(new RelativeFile(files[i], relativePath));
			}
		}
		return returnCollection;
	}
	
	/**
	 * Unzip all Zipfile contents to a directory
	 * 
	 * @param inputFile the stream of the zipFile to extract artifacts from
	 * @param outputDirectory the location to put the artifacts from the zipfile
	 * @throws IOException 
	 * @throws ZipException 
	 */
	public static void unzip(final ZipInputStream zis, final File outputDir) throws IOException {
		ZipEntry entry = null;
		try {
			while ((entry = zis.getNextEntry()) != null) {
				ZipUtils.unzipEntry(zis, entry, outputDir);
			}
		} finally {
			zis.close();
		}
	}

	public static void deleteDirAndUnzip(final ZipInputStream zis, final File outputDir) throws IOException {
		if (outputDir.exists()) {
			FileUtils.deleteDirectory(outputDir);
		}
		unzip(zis, outputDir);
	}
	
	private static void unzipEntry(ZipInputStream zis, ZipEntry entry, File outputDir) throws IOException {

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

		IOUtils.copy(inputStream, outputStream);
		outputStream.flush();
	}
}