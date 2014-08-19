package com.github.jlgrock.javascriptframework.closuretestingreport;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import org.apache.log4j.Logger;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.ParseRunner;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.TestResultType;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.TestUnitDriver;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCase;
import com.github.jlgrock.javascriptframework.mavenutils.logging.MojoLogAppender;
import com.github.jlgrock.javascriptframework.mavenutils.pathing.FileListBuilder;

/**
 * Will execute the jsclosure suite in a selenium testbed and execute it,
 * parsing values for problems. If problems arise, this can stop the build.
 */
@Mojo( name = "report",
        defaultPhase = LifecyclePhase.SITE,
        requiresProject = true,
        requiresReports = true)
public class ClosureTestingReportMojo extends AbstractMavenReport {
	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(ClosureTestingReportMojo.class);

	/**
	 * The framework directory.  This allows us to get the relative pathing to the expected generated source
	 */
    @Parameter( property = "frameworkTargetDirectory",
            defaultValue = "${project.build.directory}${file.separator}javascriptFramework",
            required = true)
	private File frameworkTargetDirectory;

	/**
	 * Directory where reports will go.
	 */
    @Parameter( property = "outputDirectory",
            defaultValue = "${project.reporting.outputDirectory}",
            required = true,
            readonly = true)
	private String outputDirectory;

	/**
	 * The maven project.
	 */
    @Parameter( defaultValue = "${project}", readonly = true )
    private MavenProject project;

	/**
	 * The site renderer.
	 */
    @Component
	private Renderer siteRenderer;

	/**
	 * Add this if you want to create the report without using Doxia, e.g. via
	 * XSL transformation from some XML file, then simply add the following
	 * method to your Mojo
	 * 
	 * @return
	 */

	/**
	 * Gets the description for the report.
	 * 
	 * @param locale
	 *            the locale
	 * @return the description
	 */
	public final String getDescription(final Locale locale) {
		return "Report on Closure Unit Testing";
	}

	@Override
	public final String getName(final Locale locale) {
		return "Closure Unit Testing";
	}

	@Override
	public final String getOutputName() {
		return "closure-unit-testing-report";
	}
	
	/**
	 * The maximum number of seconds to execute before deciding that a test
	 * case has failed.
	 */
    @Parameter( property = "testTimeoutSeconds", defaultValue = "10")
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
    @Parameter( property = "maxTestThreads", defaultValue = "-1")
	private int maxTestThreads;

	/**
	 * Version of browser. Allowed values are: <code>CHROME</code>,
	 * <code>FIREFOX</code> and <code>INTERNET EXPLORER</code>. Abbreviations
	 * for the later two are supported as well: <code>FF</code> and
	 * <code>IE</code> respectively.
	 * If not specified then the version is determined by
	 * <code>BrowserVersion.getDefault()</code>.
	 * @link http://htmlunit.sourceforge.net/apidocs/com/gargoylesoftware/htmlunit/BrowserVersion.html#getDefault()
	 *
	 * @parameter
	 */
    @Parameter( property = "browserVersion")
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
	
	@Override
	protected final void executeReport(final Locale locale)
			throws MavenReportException {
		MojoLogAppender.beginLogging(this);
		try {
			List<File> files = FileListBuilder.buildFilteredList(getFrameworkTargetDirectory(),
					"html");
			// Reporting will always show all failures
			List<TestCase> testCases = parseFiles(files,
					-1, getTestTimeoutSeconds(),
					getMaxTestThreads(), getBrowserVersion());
			ClosureTestingReportGenerator renderer = new ClosureTestingReportGenerator(
					getSink(), testCases);
			renderer.render();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new MavenReportException(e.getMessage());
		} finally {
			MojoLogAppender.endLogging();
		}
	}

	/**
	 * Parse the files created.
	 * 
	 * @param files
	 *            the files to parse
	 * @param maxFailures
	 *            the maximum number of failures to allow during the parsing.
	 * @param testTimeoutSeconds
	 *            the maximum number of seconds to execute before deciding that
	 *            a test case has failed.
	 * @param maxThreads
	 *            the maximum number of threads to spawn for test execution
	 * @param browserVersion
	 *            requested browser version (use null for the default version)
	 * @return the set of test cases received from parsing
	 */
	private static List<TestCase> parseFiles(final List<File> files,
											 final int maxFailures, final long testTimeoutSeconds,
											 final int maxThreads, final String browserVersion) {
		final List<TestCase> failures = new ArrayList<TestCase>();
		int fileCount = (files != null ? files.size() : 0);
		int threadCount = Math.min(fileCount, maxThreads);
		LOGGER.info(String.format("Parsing %d Test Files (%d threads)...",
				fileCount, threadCount));

		if (fileCount > 0) {
			// create a synchronized list so test threads can determine whether
			// or not
			// the maximum failure count has reached and threads do not attempt
			// simultaneous
			// writes to the underlying failures list; we do this separately so
			// we don't return
			// the synchronized list to the calling method
			final List<TestCase> syncFailures = Collections
					.synchronizedList(failures);
			// initialize the thread pool for test execution, using a fixed-size
			// thread pool if multiple threads are specified and
			// a single-threaded pool if running in serial mode
			final ExecutorService threadPool = (maxThreads > 1 ? Executors
					.newFixedThreadPool(threadCount) : Executors
					.newSingleThreadExecutor());
			// initialize the ParseRunner queue; one ParseRunner per thread
			final BlockingQueue<ParseRunner> runnerQueue = new ArrayBlockingQueue<ParseRunner>(
					threadCount);
			BrowserVersion bv = TestUnitDriver.getBrowserVersionSafe(browserVersion);
			LOGGER.debug("HtmlUnit browser version: " + bv.getNickname());
			for (int idx = 0; idx < threadCount; idx++) {
				runnerQueue.add(new ParseRunner(
						new TestUnitDriver(true, bv),
						testTimeoutSeconds));
			}

			// the latch that will be used as the control gate to indicate tests
			// have completed
			final CountDownLatch latch = new CountDownLatch(fileCount);

			for (final File file : files) {
				final Callable<Void> testTask = new Callable<Void>() {
					@Override
					public Void call() throws Exception {
						// if we have reached the maximum number of failures,
						// return without testing
						if (maxFailures > 0) {
							synchronized (syncFailures) {
								if (syncFailures.size() > maxFailures) {
									latch.countDown();
									return null;
								}
							}
						}

						// get the next available ParseRunner
						final ParseRunner runner = runnerQueue.take();
						try {
							final TestCase testCase = runner.parseFile(file);
							if (!TestResultType.PASSED.equals(testCase
									.getResult())) {
								synchronized (syncFailures) {
									syncFailures.add(testCase);
								}
							}
						} finally {
							if (runner != null) {
								runnerQueue.put(runner);
							}
							latch.countDown();
						}
						return null;
					}
				};
				threadPool.submit(testTask);
			}

			// stop the thread pool, preventing additional tasks from being
			// submitted
			threadPool.shutdown();
			// wait for all test cases to complete execution
			try {
				latch.await();
			} catch (InterruptedException ie) {
				// attempt to stop execution gracefully
				threadPool.shutdownNow();
			} finally {
				// clean up test resources
				if (runnerQueue.size() != threadCount) {
					throw new IllegalStateException(
							"ParseRunners were not properly returned to the queue.");
				}
				while (!runnerQueue.isEmpty()) {
					runnerQueue.remove().quit();
				}
			}
		}
		return failures;
	}

	@Override
	protected final String getOutputDirectory() {
		return outputDirectory;
	}

	@Override
	protected final MavenProject getProject() {
		return project;
	}

	@Override
	protected final Renderer getSiteRenderer() {
		return siteRenderer;
	}
	
	/**
	 * @return frameworkTargetDirectory
	 */
	protected final File getFrameworkTargetDirectory() {
		return frameworkTargetDirectory;
	}

}
