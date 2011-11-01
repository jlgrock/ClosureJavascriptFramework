package com.github.jlgrock.javascriptframework.jsdocs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkFactory;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.reporting.MavenReportException;
import org.mozilla.javascript.tools.shell.Main;

import com.github.jlgrock.javascriptframework.mavenutils.logging.MojoLogAppender;
/**
 * Generates javascript docs from the jsdoc-toolkit (the final version).
 * 
 * @goal jsdoc
 *
 */
public class JsDocsMojo extends AbstractJsDocsMojo {	
	/**
	 * The path to the JavaScript source directory). Default is
	 * src/main/javascript
	 * 
	 * @parameter expression="${basedir}/src/main/javascript"
	 */
	private File sourceDirectory;

	/**
	 * Specifies the destination directory where javadoc saves the generated
	 * HTML files.
	 * 
	 * @parameter expression="${project.reporting.outputDirectory}/jsapidocs" default-value="${project.reporting.outputDirectory}/jsapidocs"
	 */
	private File reportOutputDirectory;
    
	public JsDocsMojo() {
		System.out.println("initialized JsDocsMojo...");
	}
	
	@Override
	public File getSourceDirectory() {
		return sourceDirectory;
	}

	@Override
    protected boolean isAggregator() {
        return false;
    }
	
	@Override
    protected String getClassifier() {
        return "jsdocs";
    }
	
	/**
	 * This method is called when the report generation is invoked by maven-site-plugin.
	 *
	 * @param aSink
	 * @param aSinkFactory
	 * @param aLocale
	 * @throws MavenReportException
	 */
	public void generate( Sink aSink, SinkFactory aSinkFactory, Locale aLocale )
	throws MavenReportException {
		System.out.println("starting report generation (3)...");
		try {
			execute();
		} catch (Exception e) {
			throw new MavenReportException(e.getMessage(), e);
		}
	}
	
	public static void main(String args[]) {
		List<String> arguments = new ArrayList<String>();
		arguments.add("C:\\Workspaces\\maven-plugins\\testDependencyResolution\\target\\jsdoctoolkit24\\app\\run.js");
		arguments.add("-d=C:\\Workspaces\\maven-plugins\\testDependencyResolution\\target\\jsapidocs\\jsdocs");
		arguments.add("-t=C:\\Workspaces\\maven-plugins\\testDependencyResolution\\target\\jsdoctoolkit24\\templates\\jsdoc");
		arguments.add("C:\\Workspaces\\maven-plugins\\testDependencyResolution\\src\\main\\javascript\\com\\potomacfusion\\synapse\\cdm\\arrayIterator.js");
		arguments.add("C:\\Workspaces\\maven-plugins\\testDependencyResolution\\src\\main\\javascript\\com\\potomacfusion\\synapse\\cdm\\notification\\eventType.js");
		arguments.add("C:\\Workspaces\\maven-plugins\\testDependencyResolution\\src\\main\\javascript\\com\\potomacfusion\\synapse\\cdm\\simple.js");
		arguments.add("C:\\Workspaces\\maven-plugins\\testDependencyResolution\\src\\main\\javascript\\com\\potomacfusion\\synapse\\cdm\\composite.js");
		arguments.add("C:\\Workspaces\\maven-plugins\\testDependencyResolution\\src\\main\\javascript\\com\\potomacfusion\\synapse\\cdm\\collection.js");
		arguments.add("C:\\Workspaces\\maven-plugins\\testDependencyResolution\\src\\main\\javascript\\com\\potomacfusion\\synapse\\cdm\\cdmProperty.js");
		arguments.add("C:\\Workspaces\\maven-plugins\\testDependencyResolution\\src\\main\\javascript\\com\\potomacfusion\\synapse\\cdm\\notification\\observable.js");
		arguments.add("C:\\Workspaces\\maven-plugins\\testDependencyResolution\\src\\main\\javascript\\com\\potomacfusion\\synapse\\cdm\\recordSet.js");
		arguments.add("C:\\Workspaces\\maven-plugins\\testDependencyResolution\\src\\main\\javascript\\com\\potomacfusion\\synapse\\cdm\\property.js");
		arguments.add("C:\\Workspaces\\maven-plugins\\testDependencyResolution\\src\\main\\javascript\\com\\potomacfusion\\synapse\\cdm\\predefcdmtypes.js");
		arguments.add("C:\\Workspaces\\maven-plugins\\testDependencyResolution\\src\\main\\javascript\\com\\potomacfusion\\synapse\\cdm\\usercdmtypes.js");
		arguments.add("C:\\Workspaces\\maven-plugins\\testDependencyResolution\\src\\main\\javascript\\com\\potomacfusion\\synapse\\cdm\\mapping_validator.js");
		arguments.add("C:\\Workspaces\\maven-plugins\\testDependencyResolution\\src\\main\\javascript\\com\\potomacfusion\\synapse\\cdm\\notification\\observer.js");
		arguments.add("C:\\Workspaces\\maven-plugins\\testDependencyResolution\\src\\main\\javascript\\com\\potomacfusion\\synapse\\cdm\\record.js");
		arguments.add("C:\\Workspaces\\maven-plugins\\testDependencyResolution\\src\\main\\javascript\\com\\potomacfusion\\synapse\\cdm\\notification\\message.js");
		arguments.add("C:\\Workspaces\\maven-plugins\\testDependencyResolution\\src\\main\\javascript\\com\\potomacfusion\\synapse\\cdm\\recordSetCollection.js");
		arguments.add("C:\\Workspaces\\maven-plugins\\testDependencyResolution\\src\\main\\javascript\\com\\potomacfusion\\synapse\\cdm\\parser.js");
		arguments.add("C:\\Workspaces\\maven-plugins\\testDependencyResolution\\src\\main\\javascript\\com\\potomacfusion\\synapse\\cdm\\iterator.js");
		arguments.add("C:\\Workspaces\\maven-plugins\\testDependencyResolution\\src\\main\\javascript\\com\\potomacfusion\\synapse\\cdm\\primitive.js");
		arguments.add("-j=C:\\Workspaces\\maven-plugins\\testDependencyResolution\\target\\jsdoctoolkit24\\app\\run.js");
		Main.exec(arguments.toArray(new String[0]));
	}

	@Override
	protected File getArchiveOutputDirectory() {
		// does not archive
		return null;
	}

	/**
	 * @return reportOutputDirectory
	 */
	protected File getReportOutputDirectory() {
		return reportOutputDirectory;
	}
}
