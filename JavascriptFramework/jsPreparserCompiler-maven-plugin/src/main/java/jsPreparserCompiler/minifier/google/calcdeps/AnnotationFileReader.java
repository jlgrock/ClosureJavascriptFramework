package jsPreparserCompiler.minifier.google.calcdeps;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnnotationFileReader {
	public static Pattern req_regex = Pattern.compile("goog\\.require\\s*\\(\\s*[\\'\\\"]([^\\)]+)[\\'\\\"]\\s*\\)");
	public static Pattern prov_regex = Pattern.compile("goog\\.provide\\s*\\(\\s*[\\'\\\"]([^\\)]+)[\\'\\\"]\\s*\\)");
	public static Pattern ns_regex = Pattern.compile("^ns:((\\w+\\.)*(\\w+))$");

	public static DependencyInfo parseForDependencyInfo(File file) throws IOException {
		DependencyInfo dep = new DependencyInfo(file);
		FileInputStream filestream = null;
		try {
			if (!file.exists() || !file.isFile()) {
				throw new IOException("the File at location " + file.getCanonicalPath() + " does not exist");
			}
			filestream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(filestream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));

		    String strLine;
		    while ((strLine = br.readLine()) != null) {
		    	Matcher m;
		    	//goog.requires
		    	m = req_regex.matcher(strLine);
		    	if (m.lookingAt()){
		    		dep.addToRequires(m.group(1));
		    	}
		    	//goog.provides
		    	m = prov_regex.matcher(strLine);
		    	if (m.lookingAt()){
		    		for (int i=0; i< m.groupCount(); i++) {
		    			dep.addToProvides(m.group(1));
		    		}
		    	}
		    }
		}
		finally {
			if (filestream != null)
				filestream.close();
		}
		return dep;
	}
}
