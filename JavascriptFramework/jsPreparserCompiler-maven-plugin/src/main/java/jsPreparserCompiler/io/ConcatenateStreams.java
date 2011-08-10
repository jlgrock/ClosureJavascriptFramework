package jsPreparserCompiler.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ConcatenateStreams {
	private final BufferedReader[] readers;
	
    public ConcatenateStreams(final BufferedReader[] readersIn) throws IOException {
    	this.readers = new BufferedReader[readersIn.length];
    	if (readersIn != null)
    		System.arraycopy(readersIn, 0, this.readers, 0, readersIn.length);
    }
    
    public void createOutput(File outputFile) throws IOException {
    	if (outputFile == null)
    		throw new IOException("outputFile is required for concatenation of files.");

    	List<BufferedReader> readerList = Arrays.asList(readers);
    	
    	//create the output dir
    	outputFile.getParentFile().mkdirs();
    	outputFile.createNewFile();
    	
    	//create the output file
    	FileWriter outWriter = new FileWriter(outputFile);
        for (BufferedReader listItem : readerList) {
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
        outWriter.close();
    }
}
