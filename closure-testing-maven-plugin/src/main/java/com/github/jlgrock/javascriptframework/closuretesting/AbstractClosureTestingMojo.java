package com.github.jlgrock.javascriptframework.closuretesting;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Abstract Closure Testing Mojo.
 */
public abstract class AbstractClosureTestingMojo extends AbstractMojo {
	/**
	 * The logger for this class.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(AbstractClosureTestingMojo.class);

	/**
	 * The test source directory containing test class sources. TODO this should
	 * eventually be ${project.build.testSourceDirectory}, but the value from
	 * the maven model is not pulling in. Need to look into this.
	 */
    @Parameter( property = "testSourceDirectory",
            defaultValue = "${basedir}${file.separator}src${file.separator}test${file.separator}javascript" )
	private File testSourceDirectory;

	/**
	 * Command line working directory.
	 */
    @Parameter( property = "frameworkTargetDirectory",
            defaultValue = "${project.build.directory}${file.separator}javascriptFramework" )
	private File frameworkTargetDirectory;

	/**
	 * The location of the closure library.
	 */
    @Parameter( property = "closureLibraryLocation",
            defaultValue = "${project.build.directory}/javascriptFramework/closure-library" )
	private File closureLibraryLocation;

	/**
	 * The file produced after running the dependencies and files through the
	 * compiler. This should match the name of the closure compiler.
	 */
    @Parameter( property = "compiledFilename", defaultValue = "${project.build.finalName}-min.js", required = true )
	private String compiledFilename;

    /**
     * Set this to "true" to skip running tests, but still compile them. Its use is NOT RECOMMENDED, but quite
     * convenient on occasion.
     */
    @Parameter( property = "skipTests", defaultValue = "false" )
    protected boolean skipTests;

    /**
     * Set this to "true" to bypass unit tests entirely. Its use is NOT RECOMMENDED, especially if you enable it using
     * the "maven.test.skip" property, because maven.test.skip disables both running the tests and compiling the tests.
     * Consider using the <code>skipTests</code> parameter instead.
     */
    @Parameter( property = "maven.test.skip", defaultValue = "false" )
    protected boolean skip;

	/**
	 * If set to true, this forces the plug-in to generate and run the test
	 * cases on the compiled version of the code.
	 */
    @Parameter( property = "runTestsOnCompiled", defaultValue = "false" )
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
	 */
    @Parameter( property = "preamble")
	private String preamble;

	/**
	 * The string for the {prologue} of the testing harness.
	 */
    @Parameter( property = "prologue")
	private String prologue;

	/**
	 * The string for the {epilogue} of the testing harness.
	 */
    @Parameter( property = "epilogue")
	private String epilogue;

	/**
	 * The maximum number of test case failures before failing the build. -1
	 * indicates unlimited.
	 */
    @Parameter( property = "maximumFailures", defaultValue = "5" )
	private int maximumFailures;

	/**
	 * The maximum number of seconds to execute before deciding that a test
	 * case has failed.
	 */
    @Parameter( property = "testTimeoutSeconds", defaultValue = "10" )
	private long testTimeoutSeconds;

	/**
	 * @return the maximum number of seconds to execute before deciding that
	 *         a test case has failed.
	 */
	public final long getTestTimeoutSeconds() {
		return testTimeoutSeconds;
	}

	/**
	 * The maximum number of threads to spawn for running test files. This
	 * parameter may be any value in the range 1 -
	 * <code>Runtime.getRuntime().availableProcessors() - 1</code>. Any value
	 * outside of this range will result in the default (processor count - 1)
	 * number of threads. Setting this property to 1 will disable
	 * multi-threading and run tests serially.
	 */
    @Parameter( property = "maxTestThreads", defaultValue = "-1" )
	private int maxTestThreads;

	/**
	 * Version of browser. Allowed values are: <code>CHROME</code>,
	 * <code>FIREFOX</code> and <code>INTERNET EXPLORER</code>. Abbreviations
	 * for the later two are supported as well: <code>FF</code> and
	 * <code>IE</code> respectively.
	 * If not specified then the version is determined by
	 * <code>BrowserVersion.getDefault()</code>.
	 * @link http://htmlunit.sourceforge.net/apidocs/com/gargoylesoftware/htmlunit/BrowserVersion.html#getDefault()
	 */
    @Parameter( property = "testTimeoutSeconds" )
	private String browserVersion;

	/**
	 * @return the browser version
	 */
	public String getBrowserVersion() {
		return this.browserVersion;
	}

	/**
	 * Gets the maximum number of configured test threads. If the configured
	 * value is &lt; 1, this method returns one less than the number of
	 * available processors, as returned by
	 * <code>Runtime.getRuntime().availableProcessors()</code>. Note that this
	 * calculation may change if this method is called multiple times as
	 * processors are made (un)available to this VM. This method will never
	 * return a value less than 1.
	 * 
	 * @return the maximum number of test threads
	 */
	public final int getMaxTestThreads() {
		int max;
		int restrictedMax = Math.max(1, Runtime.getRuntime()
				.availableProcessors() - 1);
		if (maxTestThreads < 1) {
			max = restrictedMax;
		} else if (maxTestThreads > restrictedMax) {
			LOGGER.warn(String
					.format("A maximum of %d test threads may be used on this system.  (%d requested)",
							restrictedMax, maxTestThreads));
			max = restrictedMax;
		} else {
			max = maxTestThreads;
		}
		return max;
	}

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
	 * @return the closureLibraryLocation
	 */
	public final File getClosureLibraryLocation() {
		return closureLibraryLocation;
	}

	/**
	 * @return the skipTests
	 */
	public final boolean isSkipTests() {
		return skipTests;
	}

    /**
     * @return the skip
     */
    public final boolean isSkip() {
        return skip;
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
	public abstract void execute() throws MojoExecutionException,
			MojoFailureException;

	/**
	 * @return the maximum number of failures allowed before failing the build.
	 *         By limiting this, it will speed up the build if there are many
	 *         failures.
	 */
	public final int getMaximumFailures() {
		return maximumFailures;
	}

}
