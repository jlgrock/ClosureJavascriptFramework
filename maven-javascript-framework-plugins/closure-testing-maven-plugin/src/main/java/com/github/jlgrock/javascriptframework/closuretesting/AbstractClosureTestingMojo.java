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
	 *            "${project.build.directory}${file.separator}javascriptFramework${file.separator}testSuite"
	 */
	private File testOutputDirectory;

	/**
	 * The location of the closure library.
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}${file.separator}javascriptFramework${file.separator}closure-library"
	 */
	private File closureLibraryLocation;

	/**
	 * The location of generated dependency javascript file.
	 * 
	 * @parameter default-value=
	 *            "${project.build.directory}${file.separator}deps.js"
	 */
	private File dependencyLocation;

	/**
	 * Set this to "true" to skip running tests, but still compile them. Its use
	 * is NOT RECOMMENDED, but quite convenient on occasion.
	 * 
	 * @parameter default-value="false"
	 */
	private boolean skipTests;

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
	 * @return the testSourceDirectory
	 */
	public final File getTestSourceDirectory() {
		return testSourceDirectory;
	}

	/**
	 * @return the testClassesDirectory
	 */
	public final File getTestOutputDirectory() {
		return testOutputDirectory;
	}

	/**
	 * @return the closureLibrarylocation
	 */
	public final File getClosureLibrarylocation() {
		return closureLibraryLocation;
	}

	/**
	 * @return the dependencyLocation
	 */
	public final File getDependencyLocation() {
		return this.dependencyLocation;
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

	@Override
	public abstract void execute() throws MojoExecutionException, MojoFailureException;

}
