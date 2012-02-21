package com.github.jlgrock.javascriptframework.jsdocs;

import java.io.File;
import java.util.ArrayList;
/**
 * Generates and aggregates javascript docs, from the jsdoc-toolkit (the final
 * version) and stores them into a js archive.
 * 
 * @goal test-jsdoc
 * 
 */
public class TestJsDocsMojo extends JsDocsMojo {
	/**
	 * The path to the JavaScript source directory). Default is
	 * src/test/javascript
	 */
	private ArrayList<File> sourceDirectories;

	/**
     * Specifies the destination directory where test Javadoc saves the generated HTML files.
     *
     * @parameter expression="${reportTestOutputDirectory}" default-value="${project.reporting.outputDirectory}/testapidocs"
     * @required
     */
    private File reportOutputDirectory;

	@Override
	public final ArrayList<File> getSourceDirectories() {
		if (sourceDirectories == null) {
			ArrayList<File> srcDirs = new ArrayList<File>();
			srcDirs.add(new File(getBaseDir(), "src/test/javascript"));
			srcDirs.add(new File(getBaseDir(), "target/javascriptFramework/internDependencies/debugSource"));
			return srcDirs;
		}
		return sourceDirectories;
	}

	/**
	 * @return the output directory for the report
	 */
	public final File getReportOutputDirectory() {
		return reportOutputDirectory;
	}

	@Override
	protected final String getClassifier() {
        return "test-jsdocs";
    }
}
