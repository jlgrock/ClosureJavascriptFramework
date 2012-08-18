package com.github.jlgrock.javascriptframework.closuretesting;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Abstract Closure Testing Mojo.
 */
public abstract class AbstractClosureTestingMojo extends AbstractMojo {

	/**
	 * The test source directory containing test class sources. TODO this should
	 * eventually be ${project.build.testSourceDirectory}, but the value from
	 * the maven model is not pulling in. Need to look into this.
	 * 
	 * @required
	 * @parameter default-value=
	 *            "${basedir}${file.separator}src${file.separator}test${file.separator}javascript"
	 */
	private File testSourceDirectory;

	/**
	 * Command line working directory. TODO this should eventually be
	 * ${project.build.testSourceDirectory}, but the value from the maven model
	 * is not pulling in. Need to look into this.
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}${file.separator}javascriptFramework"
	 */
	private File frameworkTargetDirectory;

	/**
	 * The location of the closure library.
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}${file.separator}javascriptFramework${file.separator}closure-library"
	 */
	private File closureLibraryLocation;

	/**
	 * The file produced after running the dependencies and files through the
	 * compiler.  This should match the name of the closure compiler.
	 * 
	 * @parameter default-value="${project.build.finalName}-min.js"
	 * @required
	 */
	private String compiledFilename;
	
	/**
	 * Set this to "true" to skip running tests, but still compile them. Its use
	 * is NOT RECOMMENDED, but quite convenient on occasion.
	 * 
	 * @parameter default-value="false"
	 */
	private boolean skipTests;

	/**
	 * If set to true, this forces the plug-in to generate and run the test cases on the compiled 
	 * version of the code.
	 * 
	 * @parameter default-value="false"
	 */
	private boolean runTestsOnCompiled;
	
	/**
	 * A list of <exclude> elements specifying the tests (by pattern) that
	 * should be included in testing. When not specified and when the test
	 * parameter is not specified, the default includes will be<br>
	 * &lt;excludes&gt;<br>
	 * &lt;exclude&gt;**\/Test*.java&lt;/exclude&gt;<br>
	 * &lt;exclude&gt;*\/*Test.java&lt;/exclude&gt;<br>
	 * &lt;exclude&gt;**\/*TestCase.java&lt;/exclude&gt;<br>
	 * &lt;/excludes&gt;<br>
	 */
	private List<File> excludes;

	/**
	 * A list of <include> elements specifying the tests (by pattern) that
	 * should be included in testing. When not specified and when the test
	 * parameter is not specified, the default includes will be<br>
	 * &lt;includes&gt;\<br\>
	 * &lt;include&gt;**\/Test*.java&lt;/include&gt;<br>
	 * &lt;include&gt;**\/*Test.java&lt;/include&gt;<br>
	 * &lt;include&gt;**\/*TestCase.java&lt;/include&gt;<br>
	 * &lt;/includes&gt;<br>
	 */
	private List<File> includes;

	/**
	 * The string for the {preamble} of the testing harness.
	 * @parameter default-value=""
	 */
	private String preamble = "";

	/**
	 * The string for the {prologue} of the testing harness.
	 * @parameter default-value=""
	 */
	private String prologue = "";
	
	/**
	 * The string for the {epilogue} of the testing harness.
	 * @parameter default-value=""
	 */
	private String epilogue = "";
	
	/**
	 * The maximum number of test case failures before failing 
	 * the build.  -1 indicates unlimited.
	 * 
	 * @parameter default-value="5"
	 */
	private int maximumFailures;

	/**
	 * @return the testSourceDirectory
	 */
	public final File getTestSourceDirectory() {
		return testSourceDirectory;
	}

	/**
	 * @return the frameworkTargetDirectory
	 */	
	public final File getFrameworkTargetDirectory() {
		return frameworkTargetDirectory;
	}
	/**
	 * @return the closureLibrarylocation
	 */
	public final File getClosureLibrarylocation() {
		return closureLibraryLocation;
	}

	/**
	 * @return the skipTests
	 */
	public final boolean isSkipTests() {
		return skipTests;
	}

	/**
	 * @return the excludes
	 */
	public final List<File> getExcludes() {
		return excludes;
	}

	/**
	 * @return the includes
	 */
	public final List<File> getIncludes() {
		return includes;
	}

	/**
	 * @return the runTestsOnCompiled
	 */
	public final boolean isRunTestsOnCompiled() {
		return runTestsOnCompiled;
	}
	
	/**
	 * @return the compiledFilename
	 */	
	public final String getCompiledFilename() {
		return compiledFilename;
	}

	/**
	 * @return the preamble block
	 */
	public final String getPreamble() {
		return preamble;
	}

	/**
	 * @return the prologue block
	 */
	public final String getPrologue() {
		return prologue;
	}

	/**
	 * @return the epilogue block
	 */
	public final String getEpilogue() {
		return epilogue;
	}

	@Override
	public abstract void execute() throws MojoExecutionException, MojoFailureException;

	/**
	 * @return the maximum number of failures allowed before failing the build.  By limiting this, it will
	 * speed up the build if there are many failures.
	 */
	public int getMaximumFailures() {
		return maximumFailures;
	}

}
