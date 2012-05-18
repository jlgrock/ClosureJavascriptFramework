package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.generators;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Will generate a set of test case files. This expects that all source files
 * have been verified as JavaScript prior to usage in this Suite.
 * 
 */
public class SuiteGenerator {
	/**
	 * The set of the source files to convert.
	 */
	private final Set<File> sourceFiles;

	/**
	 * The location of the closure library base.js.
	 */
	private final File closureLibraryBaseLocation;

	/**
	 * The location of the generated dependency file.
	 */
	private final File depsLocation;

	
	/**
	 * The location of the generated dependency file.
	 */
	private final Set<File> testDependencies;

	/**
	 * The preamble for the test.
	 */
	private final String preamble;

	/**
	 * The prologue for the test.
	 */
	private final String prologue;

	/**
	 * The epilogue for the test.
	 */
	private final String epilogue;

	/**
	 * Constructor.
	 * 
	 * @param sourceFilesIn
	 *            the source files to process as part of the suite. Assumed all
	 *            are javascript
	 * @param closureLibraryBaseLocationIn
	 *            the location of the closure library (which contains base.js)
	 * @param dependencyLocationIn
	 *            the location of the generated dependency file
	 * @param testDependenciesIn
	 *            the set of test dependencies
	 * @param preamble
	 * 			  the preamble to the test case
	 * @param prologue 
	 * 			  the prologue to the test case
	 * @param epilogue
	 * 			  the epilogue to the test case
	 */
	public SuiteGenerator(final Set<File> sourceFilesIn,
			final File closureLibraryBaseLocationIn, 
			final File dependencyLocationIn, 
			final Set<File> testDependenciesIn,
			final String preamble, final String prologue, final String epilogue) {
		sourceFiles = sourceFilesIn;
		closureLibraryBaseLocation = closureLibraryBaseLocationIn;
		depsLocation = dependencyLocationIn;
		testDependencies = testDependenciesIn;
		this.preamble = preamble;
		this.prologue = prologue;
		this.epilogue = epilogue;
	}

	/**
	 * Generates the test case objects from the Source Files provided.
	 * 
	 * @return the test case objects
	 */
	private Set<TestCaseGenerator> generateTestCases() {
		Set<TestCaseGenerator> testCaseGenerators = new HashSet<TestCaseGenerator>();
		for (File sourceFile : sourceFiles) {
			testCaseGenerators.add(new TestCaseGenerator(
					closureLibraryBaseLocation, depsLocation, sourceFile, testDependencies, preamble, prologue, epilogue));
		}
		return testCaseGenerators;
	}

	/**
	 * Creates the set of test files.
	 * 
	 * @param sourceLocation
	 * 			  the location of the testing source, used for relative pathing
	 * @param outputDirectory
	 *            the output directory to generate the files to
	 * @return the set of files created
	 * @throws IOException
	 *             if unable to create one or more test cases
	 */
	public final Set<File> generateTestFiles(final File sourceLocation, final File outputDirectory)
			throws IOException {
		Set<File> outputFiles = new HashSet<File>();
		Set<TestCaseGenerator> generatedTestCases = generateTestCases();
		for (TestCaseGenerator testCaseGenerator : generatedTestCases) {
			File testCase = testCaseGenerator.createTestCase(sourceLocation, outputDirectory);
			outputFiles.add(testCase);
		}
		return outputFiles;
	}
}
