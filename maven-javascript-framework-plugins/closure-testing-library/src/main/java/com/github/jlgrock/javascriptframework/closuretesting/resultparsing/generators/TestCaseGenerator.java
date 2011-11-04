package com.github.jlgrock.javascriptframework.closuretesting.resultparsing.generators;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import com.github.jlgrock.javascriptframework.mavenutils.pathing.FileNameSeparator;

/**
 * A Class that will generate a test case output container and write to file.
 */
public class TestCaseGenerator {

	/**
	 * the location of the sourcefile to generate the test off of.
	 */
	private final File generatorSourceFile;
	
	/**
	 * the location of the closure library.
	 */
	private final File generatorClosureLocation;

	/**
	 * the location of the generated dependency file.
	 */
	private final File generatorDepsLocation;

	/**
	 * the location of the external dependency files.
	 */
	private final Set<File> testDeps;

	/**
	 * Constructor.
	 * 
	 * @param closureLocation
	 *            the location of the closure library
	 * @param depsLocation
	 *            the location of the dependency file
	 * @param sourceFile
	 *            the location of the sourcefile to generate the test off of
	 */
	public TestCaseGenerator(final File closureLocation,
			final File depsLocation, final File sourceFile,
			final Set<File> testDepsIn) {
		generatorClosureLocation = closureLocation;
		generatorSourceFile = sourceFile;
		generatorDepsLocation = depsLocation;
		testDeps = testDepsIn;
	}

	/**
	 * Create the test case at the output directory specified.
	 * 
	 * @param outputDirectory
	 *            the output directory to store the file
	 * @return the test case file
	 * @throws IOException
	 *             if unable to create the directory or write to the test case
	 *             file
	 */
	public final File createTestCase(final File outputDirectory)
			throws IOException {
		FileNameSeparator fns = new FileNameSeparator(generatorSourceFile);
		String outputFilePath = outputDirectory.getAbsoluteFile()
				+ File.separator + fns.getName() + ".html";
		File testCase = new File(outputFilePath);
		new TestCaseRef(generatorClosureLocation, generatorDepsLocation,
				generatorSourceFile, testCase, testDeps).writeToFile();
		return testCase;
	}
}
