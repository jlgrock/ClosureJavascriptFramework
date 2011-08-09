package namespaceclosure.framework;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public abstract class AbstractFileObject extends AbstractFrameworkObject {
    private static String HEADER_FILENAME_PREFIX = "// File parsed from original file ";

    protected static String buildFile(String filename, FileInputStream filestream, boolean includeTab) throws IOException {
		StringBuilder sb = new StringBuilder();
		// Get the object of DataInputStream
	    DataInputStream in = new DataInputStream(filestream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));

	    sb.append(buildHeader(filename));

	    String strLine;
	    
	    //Read File Line By Line
	    while ((strLine = br.readLine()) != null)   {
	    	if (includeTab) {
	    		sb.append(TAB);
	    		sb.append(TAB);
		    }
	    	sb.append(strLine);
	    	sb.append(NEWLINE);
	    }
	    in.close();
	    //Add a couple of newlines to the end to make the next file readable (if there is one)
	    sb.append(NEWLINE);
	    sb.append(NEWLINE);
		return sb.toString();
	}
	
    private static String buildHeader(String filename) {
    	StringBuilder istream = new StringBuilder();
		istream.append(HEADER_BORDER);
		istream.append(NEWLINE);
		
		istream.append(HEADER_FILENAME_PREFIX);
		istream.append(filename);
		istream.append(NEWLINE);
		
		istream.append(HEADER_BORDER);
		istream.append(NEWLINE);
		istream.append(NEWLINE);
		
		return istream.toString();
	}

	@Override
	public abstract ArrayList<? extends BufferedReader> toBufferedReader() throws IOException;
}
