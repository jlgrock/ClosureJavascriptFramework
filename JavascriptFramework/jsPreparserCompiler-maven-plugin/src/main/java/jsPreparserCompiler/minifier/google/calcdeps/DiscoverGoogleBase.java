package jsPreparserCompiler.minifier.google.calcdeps;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import jsPreparserCompiler.io.JSDirectoryWalker;

public class DiscoverGoogleBase {
	public static File discover(File dir) throws IOException {
		Set<DirectoryWalkResult> results = new HashSet<DirectoryWalkResult>();
		results.addAll(JSDirectoryWalker.directorySearch(dir));
		
		for (DirectoryWalkResult dwr : results) {
			for (File f : dwr.getFiles()) {
				if (f.getName().equalsIgnoreCase("base.js") && f.getParentFile().getName().equalsIgnoreCase("goog")
						&& f.getParentFile().getParentFile().getName().equalsIgnoreCase("closure")
						&& !f.getParentFile().getParentFile().getParentFile().getName().equalsIgnoreCase("third_party")) {
					return f.getParentFile();//TODO check the internals of the file, like the python script
					//TODO also, there has to be a better way than this check
				}
			}
		}
		return null;
	}
}
