package jsdoc.io;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.zip.*;

import org.apache.maven.plugin.logging.Log;

public class ZipFolder {
	private final File inFolder;
	private final File outFolder;
	private final String outFile;
	private final Log log;
	
	public ZipFolder(final File inFolder, final File outFolder, final String outFile, final Log log) {
		this.inFolder = inFolder;
		this.outFolder = outFolder;
		this.outFile = outFile;
		this.log = log;
	}

	public void zipFolder() throws IOException {
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(outFolder + File.separator + outFile)));
		BufferedInputStream in = null;
		byte[] data = new byte[1000];
		Collection<RelativeFile> files = buildFileList(inFolder);
		for (RelativeFile file : files) {
			if (log.isDebugEnabled()) {
				log.debug("zipping file: " + file.getFile().getName());
			}
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
				log.debug("Adding to zip list files from directory " + files[i].getAbsolutePath());
				returnCollection.addAll(readDirectory(files[i], relativePath + File.separator + files[i].getName()));
			} else if (files[i].isFile()) {
				log.debug("Adding to zip list file " + files[i].getAbsolutePath());				
				returnCollection.add(new RelativeFile(files[i], relativePath));
			}
		}
		return returnCollection;
	}
}
