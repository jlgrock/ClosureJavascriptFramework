package jsPreparserCompiler.minifier;

import java.io.File;

import org.apache.maven.plugin.logging.Log;

public class MinificationOptions {
	private Log log;
	private MinificationType minificationType;
	private File compileDir;
	private String compileFilename;
	private String assertFilename;
	private boolean createAssertFile;
	
	public boolean isCreateAssertFile() {
		return createAssertFile;
	}
	public void setCreateAssertFile(boolean createAssertFile) {
		this.createAssertFile = createAssertFile;
	}
	public Log getLog() {
		return log;
	}
	public void setLog(Log log) {
		this.log = log;
	}
	public MinificationType getMinificationType() {
		return minificationType;
	}
	public void setMinificationType(MinificationType minificationType) {
		this.minificationType = minificationType;
	}
	public File getCompileDir() {
		return compileDir;
	}
	public void setCompileDir(File compileDir) {
		this.compileDir = compileDir;
	}
	public String getCompileFilename() {
		return compileFilename;
	}
	public void setCompileFilename(String compileFilename) {
		this.compileFilename = compileFilename;
	}
	public String getAssertFilename() {
		return assertFilename;
	}
	public void setAssertFilename(String assertFilename) {
		this.assertFilename = assertFilename;
	}
}
