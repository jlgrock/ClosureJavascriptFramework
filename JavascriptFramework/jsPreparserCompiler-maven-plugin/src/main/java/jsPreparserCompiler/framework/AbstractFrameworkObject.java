package namespaceclosure.framework;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public abstract class AbstractFrameworkObject {
	
	protected static String HEADER_BORDER = "///////////////////////////////////////////////////////////////////////////";
	protected static String NEWLINE = "\n";
	protected static String TAB = "\t";
    
	/**
	 * Returns an input stream that contains all of the output of that object
	 */
	public abstract ArrayList<? extends BufferedReader> toBufferedReader() throws IOException;
}
