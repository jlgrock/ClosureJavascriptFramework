package jsPreparserCompiler.io;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import jsPreparserCompiler.minifier.google.calcdeps.DirectoryWalkResult;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;

public class JSDirectoryWalker extends DirectoryWalker<DirectoryWalkResult> {
	private HashMap<File, DirectoryWalkResult> dirs = new HashMap<File, DirectoryWalkResult>();
	
	private JSDirectoryWalker(FileFilter filter) {
		super(filter, -1);
	}
	
	private JSDirectoryWalker() {
        this(FileFilterUtils.or(FileFilterUtils
        		.and(FileFilterUtils.directoryFileFilter(),
        				HiddenFileFilter.VISIBLE),
        		FileFilterUtils.and(
				FileFilterUtils.fileFileFilter(),
				FileFilterUtils.suffixFileFilter(".js"))));
	}
	
	public static Set<DirectoryWalkResult> multipleDirectorySearch(Set<File> refs) throws IOException {
		Set<DirectoryWalkResult> returnCollection = new HashSet<DirectoryWalkResult>();
		for (File ref : refs) {
			if (ref.isDirectory()) {
				JSDirectoryWalker jsdw = new JSDirectoryWalker();
				Set<DirectoryWalkResult> results = new HashSet<DirectoryWalkResult>();
				jsdw.walk(ref, results);
				returnCollection.addAll(results);
			} else {
				DirectoryWalkResult dwr = new DirectoryWalkResult();
				dwr.setDirectory(ref.getParentFile());
				dwr.addFile(ref);
				returnCollection.add(dwr);
			}
		}
		return returnCollection;
	}
	
	public static Set<DirectoryWalkResult> directorySearch(File ref) throws IOException {
		JSDirectoryWalker jsdw = new JSDirectoryWalker();
		Set<DirectoryWalkResult> results = new HashSet<DirectoryWalkResult>();
		jsdw.walk(ref, results);
		return results;
	}
	
	@Override
	protected boolean handleDirectory(File directory, int depth, Collection<DirectoryWalkResult> results) {
		DirectoryWalkResult dwr = new DirectoryWalkResult();
		dwr.setDirectory(directory);
		dirs.put(directory, dwr);
		DirectoryWalkResult subdir = dirs.get(directory.getParentFile());
		if (subdir != null)
			subdir.addSubdir(dwr);
		
		return true;
	}

	@Override
	protected void handleFile(File file, int depth, Collection<DirectoryWalkResult> results) {
		dirs.get(file.getParentFile()).addFile(file);
	}
	
	@Override
	protected void handleEnd(Collection<DirectoryWalkResult> results) throws IOException {
		results.addAll(dirs.values());
	}
}
