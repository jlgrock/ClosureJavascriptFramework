package com.github.jlgrock.javascriptframework.closuretesting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.ParseRunner;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.TestResultType;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.TestUnitDriver;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.generators.SuiteGenerator;
import com.github.jlgrock.javascriptframework.closuretesting.resultparsing.testingcomponents.TestCase;
import com.github.jlgrock.javascriptframework.mavenutils.io.DirectoryIO;
import com.github.jlgrock.javascriptframework.mavenutils.logging.MojoLogAppender;
import com.github.jlgrock.javascriptframework.mavenutils.mavenobjects.JsarRelativeLocations;
import com.github.jlgrock.javascriptframework.mavenutils.pathing.FileListBuilder;

/**
 * Will execute the jsclosure suite in a selenium testbed and execute it,
 * parsing values for problems. If problems arise, this can stop the build.
 * 
 * @goal js-closure-test
 * @phase test
 */
public class ClosureTestingMojo extends AbstractClosureTestingMojo {

	/**
	 * The Logger.
	 */
	private static final Logger LOGGER = Logger
			.getLogger(ClosureTestingMojo.class);

	@Override
	public final void execute() throws MojoExecutionException,
			MojoFailureException {
		MojoLogAppender.beginLogging(this);
		try {
			List<File> files = generateFiles();
			if (!isSkipTests()) {
				List<TestCase> testCases = parseFiles(files,
						getMaximumFailures(), getTestTimeoutSeconds(),
						getMaxTestThreads());

				// Encountered Error(s)
				if (testCases.size() > 0) {
					printFailures(testCases);
					throw new MojoFailureException(
							"There were test case failures.");
				}
			}
		} catch (MojoFailureException mje) {
			throw mje;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			throw new MojoExecutionException(e.getMessage());
		} finally {
			MojoLogAppender.endLogging();
		}
	}

	/**
	 * Print failures. Will be done whenever an error is encountered.
	 * 
	 * @param testCases
	 *            the test cases to examine and print (if there is a failure)
	 */
	private void printFailures(final List<TestCase> testCases) {
		StringBuffer sb = new StringBuffer();
		for (TestCase testCase : testCases) {
			if (!testCase.getResult().equals(TestResultType.PASSED)) {
				sb.append(testCase.toString());
			}
		}
		LOGGER.error(sb.toString());
	}

	/**
	 * Will generate the files used in the google testing.
	 * 
	 * @return the set of files that were created
	 * @throws IOException
	 *             if there is a problem reading or writing to the files
	 */
	private List<File> generateFiles() throws IOException {
		File testOutputDir = JsarRelativeLocations
				.getTestSuiteLocation(getFrameworkTargetDirectory());
		File testDepsDir = JsarRelativeLocations
				.getTestLocation(getFrameworkTargetDirectory());
		File depsFileLocation = JsarRelativeLocations
				.getTestDepsLocation(getFrameworkTargetDirectory());
		List<File> returnFiles = new ArrayList<File>();

		DirectoryIO.recursivelyDeleteDirectory(testOutputDir);
		File baseLocation = new File(getClosureLibraryLocation()
				.getAbsoluteFile()
				+ File.separator
				+ "closure"
				+ File.separator + "goog" + File.separator + "base.js");

		LOGGER.info("Generating Test Suite...");
		List<File> fileSet = calculateFileSet();
		List<File> testDeps = FileListBuilder.buildFilteredList(testDepsDir,
				"js");
		List<File> depsFileSet = FileListBuilder.buildFilteredList(
				depsFileLocation, "js");
		File depsFile = null;
		if (depsFileSet.size() == 1) {
			depsFile = depsFileSet.toArray(new File[depsFileSet.size()])[0];
		} else {
			throw new IOException(
					"Could not find debug/deps file (or found more than one) at location '"
							+ depsFileLocation + "'.");
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Files that will be included in testing:" + fileSet);
			LOGGER.debug("Base Location:" + baseLocation);
			LOGGER.debug("calc deps Location:" + depsFile);
			LOGGER.debug("Testing Dependency Location:" + testDepsDir);
			LOGGER.debug("Testing Source Directory:" + getTestSourceDirectory());
		}

		SuiteGenerator suite = new SuiteGenerator(fileSet, baseLocation,
				depsFile, testDeps, getPreamble(), getPrologue(), getEpilogue());

		returnFiles.addAll(suite.generateTestFiles(getTestSourceDirectory(),
				testOutputDir));

		if (isRunTestsOnCompiled()) {
			File testCompiledOutputDir = JsarRelativeLocations
					.getCompiledTestSuiteLocation(getFrameworkTargetDirectory());
			File compiledFile = new File(
					JsarRelativeLocations
							.getCompileLocation(getFrameworkTargetDirectory()),
					getCompiledFilename());

			SuiteGenerator suiteCompiled = new SuiteGenerator(fileSet,
					baseLocation, compiledFile, testDeps, getPreamble(),
					getPrologue(), getEpilogue());
			returnFiles.addAll(suiteCompiled.generateTestFiles(
					getTestSourceDirectory(), testCompiledOutputDir));
		}

		for (File file : returnFiles) {
			LOGGER.debug("filename: " + file.getAbsolutePath());
		}
		LOGGER.debug("baseLocation: " + baseLocation.getAbsolutePath());
		LOGGER.debug("testOutputDir: " + testOutputDir.getAbsolutePath());

		return returnFiles;
	}

	/**
	 * Parse the files created.
	 * 
	 * @param files
	 *            the files to parse
	 * @param maxThreads
	 *            the maximum number of threads to spawn for test execution
	 * @param maxFailures
	 *            the maximum number of failures to allow during the parsing.
	 * @param testTimeoutSeconds
	 *            the maximum number of seconds to execute before deciding that
	 *            a test case has failed.
	 * @return the set of test cases received from parsing
	 */
	private static List<TestCase> parseFiles(final List<File> files,
			final int maxFailures, final long testTimeoutSeconds,
			final int maxThreads) {
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
			for (int idx = 0; idx < threadCount; idx++) {
				runnerQueue.add(new ParseRunner(new TestUnitDriver(true),
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

	/**
	 * Will calculate the set of files.
	 * 
	 * @return the file set
	 */
	private List<File> calculateFileSet() {
		LOGGER.info("Calculating File Set...");
		List<File> files = new ArrayList<File>();
		files.addAll(FileListBuilder.buildFilteredList(
				getTestSourceDirectory(), "js"));
		if (getIncludes() != null) {
			files.addAll(getIncludes());
		}
		if (getExcludes() != null) {
			files.removeAll(getExcludes());
		}
		return files;
	}
}
