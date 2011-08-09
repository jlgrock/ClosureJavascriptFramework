package namespaceclosure.minifier.google.calcdeps;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import namespaceclosure.io.JSDirectoryWalker;
import namespaceclosure.minifier.google.GoogleMinificationType;

import org.apache.maven.plugin.logging.Log;

public class CalcDepsOptions {
	private File googleBaseHome = null;
	private Set<File> inputs = null;
	private Set<File> paths = null;
	private Set<File> deps = null;
	private Set<File> excludes = null;
	private File assertFile = null;
	private File compileFile = null;
	private File depsOutputFile = null;
	private File compileDir = null;
	private Log log;
	private GoogleMinificationType minificationType = GoogleMinificationType.GOOGLE_WHITESPACE;
	
	public CalcDepsOptions() {
		this.deps = new HashSet<File>();
		
	}
	
	public Set<File> getInputs() {
		return inputs;
	}
	
	public Set<File> getPaths() {
		return paths;
	}
	public Set<File> getDeps() {
		return deps;
	}

	public Set<File> getExcludes() {
		return excludes;
	}
	
	public File getAssertfile() {
		return assertFile;
	}
	
	public File getCompileFile() {
		return compileFile;
	}
	public void setInputs(Collection<File> inputs) {
		this.inputs = new HashSet<File>();
		this.inputs.addAll(inputs);
	}
	public void calculatePathsAndInputs(File compileDir) throws IOException {
		Set<DirectoryWalkResult> results = new HashSet<DirectoryWalkResult>();
		results.addAll(JSDirectoryWalker.directorySearch(compileDir));
		
		Set<File> resultPaths = new HashSet<File>();
		Set<File> resultInputs = new HashSet<File>();
		
		//first add the google base to the paths
		resultPaths.add(getGoogleBaseFile());
		
		//TODO this needs to be adjusted - it is automatically going up two dirs from the base.js
		for (DirectoryWalkResult dwr : results) {
			resultPaths.addAll(dwr.getFiles());
			if (!isChildFile(dwr.getDirectory(), getGoogleBaseHome().getParentFile().getParentFile())) {
				resultInputs.addAll(dwr.getFiles());
			}
		}
		setPaths(resultPaths);
		setInputs(resultInputs);
	}
	
	/**
	 * paths must contain the google base.js file
	 * @param paths
	 */
	public void setPaths(Collection<File> paths) {
		this.paths = new HashSet<File>();
		this.paths.addAll(paths);
	}
	
	public Log getLog() {
		return log;
	}
	public void setLog(Log log) {
		this.log = log;
	}
	public void setDeps(Collection<File> deps) {
		this.deps = new HashSet<File>();
		this.deps.addAll(deps);
	}
	public void setExcludes(Collection<File> excludes) {
		this.excludes = new HashSet<File>();
		this.excludes.addAll(excludes);
	}
	public void setCompileFile(File compileFile) {
		this.compileFile = compileFile;
	}
	public void setAssertFile(File assertFile) {
		this.assertFile = assertFile;
	}
	public void setAssertFile(File compileDir, String assertFilename) throws IOException {
		setAssertFile(new File(compileDir.getCanonicalFile() + File.separator + assertFilename));
	}
	public void setMinificationType(GoogleMinificationType minificationType) {
		this.minificationType = minificationType;
	}
	public GoogleMinificationType getMinificationType() {
		return minificationType;
	}
	public String getMinificationName() {
		return minificationType.name;
	}
	
	public void setDepsOutputFile(File depsOutputFile) {
		this.depsOutputFile = depsOutputFile;
	}
	public File getDepsOutputFile() {
		return depsOutputFile;
	}
	public void setCompileFile(File compileDir, String compileFilename) throws IOException {
		setCompileFile(new File(compileDir.getCanonicalFile() + File.separator + compileFilename));
	}
	
	public File getGoogleBaseHome() {
		return this.googleBaseHome;
	}
	
	public File getGoogleBaseFile() {
		return new File(this.googleBaseHome + File.separator + "base.js");
	}
	
	public void setGoogleBaseHome(File googleBaseHome){
		this.googleBaseHome = googleBaseHome;
	}
	
	public File getCompileDir() {
		return this.compileDir;
	}
	
	public void setCompileDir(File compileDir) {
		this.compileDir = compileDir;
	}
	
	@Override
	public String toString() {
		String assertFN = (assertFile != null)?assertFile.getAbsolutePath():"null";
		String compilerFN = (compileFile != null)?compileFile.getAbsolutePath():"null";

		return "getGoogleBaseFile(): " + getGoogleBaseFile();
//		+ "\npaths: " + paths + "\ndeps: " + deps + "\nexcludes: " 
//		+ excludes + "\nassertFile: " + assertFN + "\ncompileFile: " 
//		+ compilerFN + "\ndepsOutputFile: " + depsOutputFile + "\ncompileDir: " + compileDir;
	}
	
	private boolean isChildFile(File maybeChild, File possibleParent) throws IOException {
		return maybeChild.getCanonicalPath().startsWith(possibleParent.getCanonicalPath());
    }

}
