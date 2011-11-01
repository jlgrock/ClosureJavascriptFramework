package com.github.jlgrock.javascriptframework.jsdocs;

import java.io.File;
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
	 * src/main/javascript
	 * 
	 * @parameter expression="${basedir}/src/main/javascript"
	 */
	private File sourceDirectory;

	/**
     * Specifies the destination directory where test Javadoc saves the generated HTML files.
     *
     * @parameter expression="${reportTestOutputDirectory}" default-value="${project.reporting.outputDirectory}/testapidocs"
     * @required
     */
    private File reportOutputDirectory;

	@Override
	public File getSourceDirectory() {
		return sourceDirectory;
	}

	/**
	 * @return the output directory for the report
	 */
	public File getReportOutputDirectory() {
		return reportOutputDirectory;
	}

	@Override
    protected String getClassifier() {
        return "test-jsdocs";
    }
}
