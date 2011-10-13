package org.mojo.javascriptframework.mavenutils.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.mojo.javascriptframework.mavenutils.io.readers.FilteredReader;

/**
 * Simple file copy utility.
 */
public class FileIO {
	static final Logger logger = Logger.getLogger(FileIO.class);

	/**
	 * Will copy content from an inputstream to an output stream, closing the files when it has completed
	 * @param from The expected file to read in from
	 * @param to The expected file to write to
	 * @throws IOException
	 */
	public static void copyStream(final InputStream from, final OutputStream to) throws IOException {
		logger.debug("copying stream...");
		try {
		    IOUtils.copy(from, to);
		}  catch (IOException ioe) {
			logger.error("There was a problem copying the resource.");
			throw ioe;
		} finally {
		    IOUtils.closeQuietly(from);
		    IOUtils.closeQuietly(to);
		}
	}

	/**
	 * Take a bunch of readers (files or otherwise) and concatenate 
	 * their input together into one output file
	 * 
	 * @param readers 
	 * @param outputFile
	 * @throws IOException
	 */
    public void concatenateStreams(Reader[] readers, File outputFile) throws IOException {
    	if (outputFile == null)
    		throw new IOException("outputFile is required for concatenation of files.");

    	logger.debug("Begining stream concatenation");
    	List<Reader> readerList = Arrays.asList(readers);
    	FileWriter outWriter = null;
    	
    	//create the output dir
    	DirectoryIO.createDir(outputFile.getParentFile());
    	outputFile.createNewFile();
    	
    	//create the output file
    	try {
    		outWriter = new FileWriter(outputFile);
    	
	        for (Reader listItem : readerList) {
	        	//replace tabs with 4 spaces for linting
	        	FilteredReader fr = new FilteredReader(listItem, "\t", "    ");
	        	String line;
	        	do {
		    		line = fr.readLine();
		    		if (line != null) {
		    			outWriter.append(line + "\n");
		    		}
	        	} while(line != null);
	        	listItem.close();
	        }
    	} finally {
    		try {
    			outWriter.close();
    		} catch(Exception e) {
    			//Do nothing
    		}
    	}
    }
    
    /**
     * Parses the extension of a file
     * 
     * @param file the file to parse
     * @return the extension of the file
     */
    public static String getFileExtension(final File file) {
    	String name = file.getName();
    	int pos = name.lastIndexOf('.');
    	return name.substring(pos+1);
    }
    
    /**
     * Remove the extension from a file
     * 
     * @param file the file to parse
     * @return the filename with the extension removed
     */
    public static String removeFileExtension(final File file) {
    	String name = file.getName();
    	if (!name.contains(".")) {
    		return name;
    	}
    	int pos = name.lastIndexOf('.');
    	return name.substring(0, pos);
    }
    
    public static void changeExtension(final File file, final String newExtension) throws IOException {
    	String newFilename = removeFileExtension(file) + "." + newExtension;
    	File file2 = new File(newFilename);
    	boolean success = file.renameTo(file2);
    	if (!success) {
    		throw new IOException("Could not rename file from \"" + file.getName() + "\" to \"" + file2.getName() + "\".");
    	}
    }
}
